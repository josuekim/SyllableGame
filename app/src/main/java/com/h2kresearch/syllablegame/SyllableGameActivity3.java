package com.h2kresearch.syllablegame;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.h2kresearch.syllablegame.database.DatabaseAccess;
import com.h2kresearch.syllablegame.model.ConfigurationModel;
import com.h2kresearch.syllablegame.utils.CommonUtils;
import com.h2kresearch.syllablegame.utils.CommonUtils.ConsonantType;
import com.h2kresearch.syllablegame.utils.CommonUtils.VowelType;
import com.h2kresearch.syllablegame.utils.MusicService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("unchecked")
public class SyllableGameActivity3 extends AppCompatActivity {

  private static final int SEND_THREAD_START_MESSAGE = 0;
  private static final int FLIP_FLOP_IMAGE = 1;
  private static final int SEND_THREAD_STOP_MESSAGE = 2;

  private ListenWordHandler mHandler = null;

  FrameLayout frame_consonant;
  FrameLayout frame_vowelRight;
  FrameLayout frame_vowelBottom;
  FrameLayout frame_fullsize;
  RelativeLayout puzzleLayout;

  LinearLayout consonantList;
  LinearLayout vowelList;

  ImageView[] img_consonant;
  ImageView[] img_vowelRight;
  ImageView[] img_vowelBottom;
  ImageView img_counting;
  ImageView nextImage;

  TextView backBtn;

  Intent mLessonIntent;
  String[] select;

  Object[] dec_consonants;
  Object[] dec_vowels;

  int consonantsId;
  int vowelRightId;
  int vowelBottomId;
  int[] correctAnswer = {-1,-1,-1};
  int[] correctSound = {-1,-1,-1};

  char currentConsonant;
  char currentVowel;

  boolean flagMoved = false;

  int completeCnt;

  DatabaseAccess mDB;

  ConfigurationModel mConf;

  float mPuzzleGap = 30.0f;
  int mPuzzleWidth = 240;
  int mPuzzleHeight = 300;

  public void makeExamples(Object[] dec_cList, Object[] dec_vList){
    ArrayList<Integer> cons = new ArrayList<>();
    ArrayList<Integer> vows = new ArrayList<>();

    for(ConsonantType ct : ConsonantType.values()) {
      for(Object dec_c : dec_cList){
        if(ct.getName().equals(dec_c.toString())) {
          cons.add((ct.ordinal() + 1));
        }
      }
    }
    for(VowelType vt : VowelType.values()){
      for(Object dec_v : dec_vList){
        if(vt.getVow().equals(dec_v.toString())) {
          vows.add((vt.ordinal() + 1));
        }
      }
    }
    Random rand = new Random(System.currentTimeMillis());
    int conInt = cons.get(rand.nextInt(cons.size()));
    correctAnswer[0] = getResources().getIdentifier("consonant" + conInt, "drawable", getPackageName());
    correctSound[0] = getResources().getIdentifier("sound" + (conInt*11), "raw", getPackageName());

    for(int i = 0; i < img_consonant.length; i++){
      Map tagMap = (Map)img_consonant[i].getTag();
      if(correctAnswer[0] == (int)tagMap.get("resourceId")){
        consonantsId = img_consonant[i].getId();
        currentConsonant = img_consonant[i].getContentDescription().charAt(0);
      }
    }

    int vowInt = vows.get(rand.nextInt(vows.size()));
    if((vowInt >= 1 && vowInt <= 4) || vowInt ==10){
      correctAnswer[1] = getResources().getIdentifier("vowel" + (vowInt), "drawable", getPackageName());
      correctSound[1] = getResources().getIdentifier("sound" + (vowInt), "raw", getPackageName());
      for(int i = 0; i < img_vowelRight.length; i++){
        Map tagMap = (Map)img_vowelRight[i].getTag();
        if(correctAnswer[1] == (int)tagMap.get("resourceId")){
          vowelRightId = img_vowelRight[i].getId();
          vowelBottomId = -1;
          currentVowel = img_vowelRight[i].getContentDescription().charAt(0);
        }
      }
    }else{
      correctAnswer[2] = getResources().getIdentifier("vowel" + (vowInt), "drawable", getPackageName());
      correctSound[1] = getResources().getIdentifier("sound" + (vowInt), "raw", getPackageName());
      for(int i = 0; i < img_vowelBottom.length; i++){
        Map tagMap = (Map)img_vowelBottom[i].getTag();
        if(correctAnswer[2] == (int)tagMap.get("resourceId")){
          vowelBottomId = img_vowelBottom[i].getId();
          vowelRightId = -1;
          currentVowel = img_vowelBottom[i].getContentDescription().charAt(0);
        }
      }
    }

    long examId = mDB.insertDailyExam(conInt,(vowInt+14));
    mConf.setExamId(examId);

    correctSound[2] = getResources().getIdentifier("sound" + (conInt * 11 + vowInt), "raw", getPackageName());
    MusicService.MediaPlay(getApplicationContext(), correctSound[2]);

  }

  public void makeDragItems(){

    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0);
    layoutParams.weight = 1;
    layoutParams.setMargins(0,10,0,10);

    float xPixels = TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 83, getResources().getDisplayMetrics());

    int resourceId = -1;
    img_consonant = new ImageView[dec_consonants.length];

    for(ConsonantType ct : ConsonantType.values()) {
      for (int i = 0; i < dec_consonants.length; i++) {
        if (ct.getName().equals(dec_consonants[i].toString())) {
          img_consonant[i] = new ImageView(this);
          resourceId = getResources()
              .getIdentifier("consonant" + (ct.ordinal() + 1), "drawable", getPackageName());
          img_consonant[i].setImageResource(resourceId);
          consonantList.addView(img_consonant[i]);

          img_consonant[i].setLayoutParams(layoutParams);
          img_consonant[i].setAdjustViewBounds(true);

          img_consonant[i].setOnTouchListener(mTouchListener);
          img_consonant[i].setId((ct.ordinal() + 1) + 100);
          Map tagMap = new HashMap();
          tagMap.put("resourceId",resourceId);
          tagMap.put("wordId",(ct.ordinal() + 1));
          img_consonant[i].setTag(tagMap);
          img_consonant[i].setContentDescription(dec_consonants[i].toString());

          break;
        }
      }
    }

    int countRight = 0;
    int countBottom = 0;
    for(VowelType vt : VowelType.values()){
      for(Object dec_vowel : dec_vowels){
        if(vt.getVow().equals(dec_vowel.toString())){
          if(vt.getSide().equals("R")){
            countRight++;
          }else if(vt.getSide().equals("B")){
            countBottom++;
          }
        }
      }
    }

    img_vowelRight = new ImageView[countRight];
    img_vowelBottom = new ImageView[countBottom];
    countRight = 0;
    countBottom = 0;
    for(VowelType vt : VowelType.values()){
      for(int i = 0; i <dec_vowels.length; i++) {
        if (vt.getVow().equals(dec_vowels[i].toString())) {
          if(vt.getSide().equals("R")){
            xPixels = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
            img_vowelRight[countRight] = new ImageView(this);
//            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams((int) xPixels, LayoutParams.WRAP_CONTENT);
//            layoutParams.gravity= Gravity.TOP;
//            img_vowelRight[countRight].setLayoutParams(layoutParams);
//            img_vowelRight[countRight].setAdjustViewBounds(true);
//            img_vowelRight[countRight].setPadding(50,10,10,0);
            resourceId = getResources().getIdentifier("vowel" + (vt.ordinal()+1), "drawable", getPackageName());
            img_vowelRight[countRight].setImageResource(resourceId);
            vowelList.addView(img_vowelRight[countRight]);

            img_vowelRight[countRight].setLayoutParams(layoutParams);
            img_vowelRight[countRight].setAdjustViewBounds(true);


            img_vowelRight[countRight].setOnTouchListener(mTouchListener);
            img_vowelRight[countRight].setId((vt.ordinal()+1)+200);
            Map tagMap = new HashMap();
            tagMap.put("resourceId",resourceId);
            tagMap.put("wordId",(vt.ordinal() + 15));
            img_vowelRight[countRight].setTag(tagMap);
            img_vowelRight[countRight].setContentDescription(dec_vowels[i].toString());
            countRight++;
          }else if(vt.getSide().equals("B")){
            xPixels = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 99, getResources().getDisplayMetrics());
            img_vowelBottom[countBottom] = new ImageView(this);
//            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams((int) xPixels, LayoutParams.WRAP_CONTENT);
//            layoutParams.gravity= Gravity.TOP;
//            img_vowelBottom[countBottom].setLayoutParams(layoutParams);
//            img_vowelBottom[countBottom].setAdjustViewBounds(true);
//            img_vowelBottom[countBottom].setPadding(92,10,10,0);
            resourceId = getResources().getIdentifier("vowel" + (vt.ordinal()+1), "drawable", getPackageName());
            img_vowelBottom[countBottom].setImageResource(resourceId);
            vowelList.addView(img_vowelBottom[countBottom]);

            img_vowelBottom[countBottom].setLayoutParams(layoutParams);
            img_vowelBottom[countBottom].setAdjustViewBounds(true);

            img_vowelBottom[countBottom].setOnTouchListener(mTouchListener);
            img_vowelBottom[countBottom].setId((vt.ordinal()+1)+300);
            Map tagMap = new HashMap();
            tagMap.put("resourceId",resourceId);
            tagMap.put("wordId",(vt.ordinal() + 15));
            img_vowelBottom[countBottom].setTag(tagMap);
            img_vowelBottom[countBottom].setContentDescription(dec_vowels[i].toString());
            countBottom++;
          }
        }
      }
    }
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_syllable_game3);

    completeCnt = 0;

    Intent preIntent = getIntent();
    select = preIntent.getStringArrayExtra("select");

//    Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
//    setSupportActionBar(toolbar);
//    getSupportActionBar().setDisplayShowTitleEnabled(false);
//    TextView tv = (TextView) findViewById(R.id.toolbar_title);
//    tv.setText("음절 조합 확인");

    ArrayList<String[]> wordList = CommonUtils.deCombinationList(select);
    dec_consonants = CommonUtils.removeDuplicateArray(wordList.get(0));
    dec_vowels = CommonUtils.removeDuplicateArray(wordList.get(1));

    puzzleLayout = (RelativeLayout) findViewById(R.id.puzzleLayout);

    consonantList = (LinearLayout) findViewById(R.id.consonantList);
    vowelList = (LinearLayout) findViewById(R.id.vowelList);

    frame_consonant = (FrameLayout) findViewById(R.id.consonantBlock);
    frame_consonant.setOnDragListener(mDragListener);
    frame_consonant.setOnClickListener(mClickListener);
    frame_consonant.setClickable(false);

    frame_vowelRight = (FrameLayout) findViewById(R.id.vowelRight);
    frame_vowelRight.setOnDragListener(mDragListener);
    frame_vowelRight.setOnClickListener(mClickListener);
    frame_vowelRight.setClickable(false);

    frame_vowelBottom = (FrameLayout) findViewById(R.id.vowelBottom);
    frame_vowelBottom.setOnDragListener(mDragListener);
    frame_vowelBottom.setOnClickListener(mClickListener);
    frame_vowelBottom.setClickable(false);

    mDB = mDB.getInstance(this);
    mConf = mConf.getInstance();

    backBtn = (TextView) findViewById(R.id.textViewL);
    backBtn.setOnClickListener(mClickListener);

    ImageView listenBtn = (ImageView) findViewById(R.id.repeatButton);
    listenBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        MusicService.MediaPlay(getApplicationContext(), correctSound[2]);
        mDB.updateExamRepeat(mConf.getExamId());
      }
    });

    makeDragItems();

    img_counting = (ImageView) findViewById(R.id.complete_counting);
    makeExamples(dec_consonants,dec_vowels);
  }

  class ImageDrag extends View.DragShadowBuilder {

    View mView;
    int mX;
    int mY;
    int mWidth;
    int mHeight;

    private ImageDrag(View v, int x, int y) {
      super(v);
      mView = v;
      mX = x;
      mY = y;
      mWidth = v.getWidth();
      mHeight = v.getHeight();
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
      super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
//      float xPixels = TypedValue
//          .applyDimension(TypedValue.COMPLEX_UNIT_DIP, mWidth, getResources().getDisplayMetrics());
//      float yPixels = TypedValue
//          .applyDimension(TypedValue.COMPLEX_UNIT_DIP, mHeight, getResources().getDisplayMetrics());
//      shadowSize.set((int) xPixels, (int) yPixels);
      if(mHeight > mWidth) {
        shadowSize.set(mPuzzleWidth, mPuzzleHeight);
        shadowTouchPoint.set((int)(mPuzzleWidth/2), (int)(mPuzzleHeight/2));
      } else if (mWidth > mHeight) {
        shadowSize.set(mPuzzleHeight, mPuzzleWidth);
        shadowTouchPoint.set((int)(mPuzzleHeight/2), (int)(mPuzzleWidth/2));
      } else {
        shadowSize.set(mPuzzleWidth, mPuzzleWidth);
        shadowTouchPoint.set((int)(mPuzzleWidth/2), (int)(mPuzzleWidth/2));
      }
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
      if(mHeight > mWidth) {
        float scale = (float) mPuzzleWidth / (float) mWidth;
        canvas.scale(scale, scale);
      } else if (mWidth > mHeight) {
        float scale = (float) mPuzzleHeight / (float) mWidth;
        canvas.scale(scale, scale);
      } else {
        float scale = (float) mPuzzleWidth / (float) mWidth;
        canvas.scale(scale, scale);
      }
      super.onDrawShadow(canvas);
    }
  }

  View.OnTouchListener mTouchListener = new View.OnTouchListener() {

    @Override
    public boolean onTouch(View v, MotionEvent event) {
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          ClipData clipData = ClipData.newPlainText("", "");
          v.startDrag(clipData, new ImageDrag(v, (int) event.getX(), (int) event.getY()), v, 0);
          v.setVisibility(View.GONE);

          return true;
      }
      return false;
    }
  };

  View.OnDragListener mDragListener = new View.OnDragListener() {

    @Override
    public boolean onDrag(View v, DragEvent event) {
      switch (event.getAction()) {
        case DragEvent.ACTION_DRAG_STARTED:
          if (!flagMoved) {
//            frame_consonant.setX(frame_consonant.getX() - 30f);
//            frame_vowelRight.setX(frame_vowelRight.getX() + 30f);
//            frame_vowelBottom.setY(frame_vowelBottom.getY() + 60f);
//            frame_vowelBottom.setX(frame_vowelBottom.getX() - 30f);
            frame_consonant.setX(frame_consonant.getX()- mPuzzleGap);
            frame_vowelRight.setX(frame_vowelRight.getX() + mPuzzleGap);
            frame_vowelBottom.setY(frame_vowelBottom.getY() + mPuzzleGap*2);
            frame_vowelBottom.setX(frame_vowelBottom.getX() - mPuzzleGap);
            flagMoved = true;
          }
          return true;
        case DragEvent.ACTION_DRAG_ENTERED:
          return true;
        case DragEvent.ACTION_DRAG_LOCATION:
          break;
        case DragEvent.ACTION_DRAG_EXITED:
          return true;
        case DragEvent.ACTION_DROP:
          View view = (View) event.getLocalState();
          int getNum = -1;
          int imageId = -1;
          if (view != null) {
            FrameLayout newParent = (FrameLayout) v;
            boolean flag1 = false;
            boolean flag2 = false;
            Map tagMap = (Map)view.getTag();

            mDB.insertExamResponse(mConf.getExamId(),(int)tagMap.get("wordId"));

            if (v.getId() == R.id.consonantBlock) {
              flag1 = true;
              if (consonantsId == view.getId()) {
                frame_consonant.removeAllViews();
                flag2 = true;
                getNum = (int)view.getId() - 100;
                imageId = getResources()
                    .getIdentifier("consonant" + getNum +"_black", "drawable", getPackageName());
                currentConsonant = view.getContentDescription().charAt(0);
              }else{
                mDB.insertWrongAnswer(mConf.getConStudyId(),(int)tagMap.get("wordId"));
              }

            } else if (v.getId() == R.id.vowelRight) {
              flag1 = true;
              if (vowelRightId == view.getId()) {
                frame_vowelRight.removeAllViews();
                frame_vowelBottom.removeAllViews();
                flag2 = true;
                getNum = (int)view.getId() - 200;
                imageId = getResources()
                    .getIdentifier("vowel" + getNum +"_black", "drawable", getPackageName());
                currentVowel = view.getContentDescription().charAt(0);
              }else{
                mDB.insertWrongAnswer(mConf.getVowStudyId(),(int)tagMap.get("wordId"));
              }

            } else if (v.getId() == R.id.vowelBottom) {
              flag1 = true;
              if (vowelBottomId == view.getId()) {
                frame_vowelRight.removeAllViews();
                frame_vowelBottom.removeAllViews();
                flag2 = true;
                getNum = (int)view.getId() - 300;
                imageId = getResources()
                    .getIdentifier("vowel" + getNum +"_black", "drawable", getPackageName());
                currentVowel = view.getContentDescription().charAt(0);
              }else{
                mDB.insertWrongAnswer(mConf.getVowStudyId(),(int)tagMap.get("wordId"));
              }
            }

            if (flag1 && flag2) {
              ImageView newView = new ImageView(getApplicationContext());

              newView.setImageDrawable(getResources().getDrawable(imageId));

              newParent.setTag(getNum);
              newParent.addView(newView);
              view.setVisibility(View.VISIBLE);

            }

            if (frame_consonant.getChildCount() == 1 && (frame_vowelRight.getChildCount() == 1
                || frame_vowelBottom.getChildCount() == 1)) {
              MusicService.MediaPlay(getApplicationContext(), correctSound[2]);
              mDB.updateExamCorrect(mConf.getExamId());
            }

            if (flagMoved) {
//              frame_consonant.setX(frame_consonant.getX() + 30f);
//              frame_vowelRight.setX(frame_vowelRight.getX() - 30f);
//              frame_vowelBottom.setY(frame_vowelBottom.getY() - 60f);
//              frame_vowelBottom.setX(frame_vowelBottom.getX() + 30f);
              frame_consonant.setX(frame_consonant.getX() + mPuzzleGap);
              frame_vowelRight.setX(frame_vowelRight.getX() - mPuzzleGap);
              frame_vowelBottom.setY(frame_vowelBottom.getY() - mPuzzleGap*2);
              frame_vowelBottom.setX(frame_vowelBottom.getX() + mPuzzleGap);
              flagMoved = false;
            }

          }
          return true;
        case DragEvent.ACTION_DRAG_ENDED:
          if (flagMoved) {
//            frame_consonant.setX(frame_consonant.getX() + 30f);
//            frame_vowelRight.setX(frame_vowelRight.getX() - 30f);
//            frame_vowelBottom.setY(frame_vowelBottom.getY() - 60f);
//            frame_vowelBottom.setX(frame_vowelBottom.getX() + 30f);
            frame_consonant.setX(frame_consonant.getX() + mPuzzleGap);
            frame_vowelRight.setX(frame_vowelRight.getX() - mPuzzleGap);
            frame_vowelBottom.setY(frame_vowelBottom.getY() - mPuzzleGap*2);
            frame_vowelBottom.setX(frame_vowelBottom.getX() + mPuzzleGap);
            flagMoved = false;
          }

          ((View) (event.getLocalState())).setVisibility(View.VISIBLE);

          if (frame_consonant.getChildCount() == 1 && (frame_vowelRight.getChildCount() == 1
              || frame_vowelBottom.getChildCount() == 1)) {

            consonantList.removeAllViews();
            vowelList.removeAllViews();

            frame_consonant.setClickable(true);
            frame_vowelRight.setClickable(true);
            frame_vowelBottom.setClickable(true);

            nextImage = new ImageView(getApplicationContext());
            nextImage.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
            nextImage.setOnClickListener(mNextClickListener);
            vowelList.addView(nextImage);

          }
          return true;
      }
      return false;
    }
  };

  View.OnClickListener mClickListener = new View.OnClickListener(){
    @Override
    public void onClick(View v) {
      if(v.getId() == backBtn.getId()){
        onBackPressed();
      }
      if(v.getId() == frame_consonant.getId()){
        if(frame_consonant.getTag() != null && frame_consonant.isClickable()){
          frame_vowelRight.setClickable(false);
          frame_vowelBottom.setClickable(false);
          FrameLayout newParent = (FrameLayout) v;

          mHandler = new ListenWordHandler();
          Message msg = mHandler.obtainMessage();
          msg.what = SEND_THREAD_START_MESSAGE;
          msg.arg1 = 1;
          msg.obj = newParent;
          mHandler.sendMessage(msg);

        }
      }
      if(v.getId() == frame_vowelRight.getId()){
        if(frame_vowelRight.getTag() != null && frame_vowelRight.isClickable()){
          frame_consonant.setClickable(false);
          frame_vowelBottom.setClickable(false);
          FrameLayout newParent = (FrameLayout) v;

          mHandler = new ListenWordHandler();
          Message msg = mHandler.obtainMessage();
          msg.what = SEND_THREAD_START_MESSAGE;
          msg.arg1 = 2;
          msg.obj = newParent;
          mHandler.sendMessage(msg);
        }
      }
      if(v.getId() == frame_vowelBottom.getId()){
        if(frame_vowelBottom.getTag() != null && frame_vowelBottom.isClickable()){
          frame_consonant.setClickable(false);
          frame_vowelRight.setClickable(false);
          FrameLayout newParent = (FrameLayout) v;

          mHandler = new ListenWordHandler();
          Message msg = mHandler.obtainMessage();
          msg.what = SEND_THREAD_START_MESSAGE;
          msg.arg1 = 2;
          msg.obj = newParent;
          mHandler.sendMessage(msg);
        }
      }
      if(frame_fullsize != null && v.getId() == frame_fullsize.getId()){
        mHandler = new ListenWordHandler();
        Message msg = mHandler.obtainMessage();
        msg.what = SEND_THREAD_START_MESSAGE;
        msg.arg1 = 3;
        msg.obj = v;
        mHandler.sendMessage(msg);
      }
    }
  };

  View.OnClickListener mNextClickListener = new View.OnClickListener(){
    private int mSeq = 1;

    @Override
    public void onClick(View v) {
      if(v.getId() == nextImage.getId()){
        if(mSeq == 1){
          nextImage.setClickable(false);

          frame_consonant.postDelayed(new Runnable() {  //delay button
            public void run() {
              frame_consonant.performClick();
            }
          }, 0);
          frame_vowelRight.postDelayed(new Runnable() {  //delay button
            public void run() {
              frame_vowelRight.performClick();
            }
          }, 1100);
          frame_vowelBottom.postDelayed(new Runnable() {  //delay button
            public void run() {
              frame_vowelBottom.performClick();
            }
          }, 1100);

          puzzleLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
              int imageId = 0;
              int imagePinkId = 0;
              frame_consonant.setVisibility(View.GONE);
              frame_vowelRight.setVisibility(View.GONE);
              frame_vowelBottom.setVisibility(View.GONE);
              if(frame_consonant.getTag() != null){
                if(frame_vowelRight.getTag() != null){
                  imageId = ((int)frame_consonant.getTag() - 1) * 10 + (int)frame_vowelRight.getTag();
                  imagePinkId = ((int)frame_consonant.getTag() * 22 + (int)frame_vowelRight.getTag() * 2) + 1;
                }else if(frame_vowelBottom.getTag() != null){
                  imageId = ((int)frame_consonant.getTag() - 1) * 10 + (int)frame_vowelBottom.getTag();
                  imagePinkId = ((int)frame_consonant.getTag()  * 22 + (int)frame_vowelBottom.getTag() * 2) + 1;
                }
              }
              frame_fullsize = new FrameLayout(getApplicationContext());
              RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
              frame_fullsize.setLayoutParams(params);

              int padding = puzzleLayout.getHeight() * 20 / 100;
              frame_fullsize.setPadding(padding, padding, padding, padding);

              ImageView fullWordView = new ImageView(getApplicationContext());
              fullWordView.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("syl"+imageId+"_black","drawable", getPackageName())));
              frame_fullsize.addView(fullWordView);
              frame_fullsize.setTag(imageId);
              frame_fullsize.setId(imagePinkId);
              frame_fullsize.setOnClickListener(mClickListener);

              MusicService.MediaPlay(getApplicationContext(), correctSound[2]);

              puzzleLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
              puzzleLayout.addView(frame_fullsize);
              nextImage.setClickable(true);
            }
          }, 2500);

          mSeq = 2;

        }else if(mSeq == 2){
          nextImage.setClickable(false);
          vowelList.removeAllViews();
          makeDragItems();
          frame_consonant.setVisibility(View.VISIBLE);
          frame_vowelRight.setVisibility(View.VISIBLE);
          frame_vowelBottom.setVisibility(View.VISIBLE);
          frame_fullsize.setVisibility(View.GONE);

          frame_consonant.removeAllViews();
          frame_vowelBottom.removeAllViews();
          frame_vowelRight.removeAllViews();
          frame_consonant.setTag(null);
          frame_vowelBottom.setTag(null);
          frame_vowelRight.setTag(null);

          mSeq = 1;

          if (completeCnt != 9) {
            completeCnt++;
            int resourceId = getResources()
                .getIdentifier("count" + completeCnt, "drawable", getPackageName());
            img_counting.setImageDrawable(getResources().getDrawable(resourceId));
            makeExamples(dec_consonants,dec_vowels);

          } else {
            img_counting.setImageDrawable(getResources().getDrawable(R.drawable.count10));

            Dialog custom = new Dialog(SyllableGameActivity3.this);
            custom.setContentView(R.layout.custom_dialog);
            ImageView applaud = (ImageView) custom.findViewById(R.id.imageForApplaud);
            Glide.with(getApplicationContext()).load(R.drawable.rabbit).into(applaud);

            mLessonIntent = new Intent(SyllableGameActivity3.this, LessonActivity.class);
            mLessonIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mLessonIntent.putExtra("select", select);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
              @Override
              public void run() {
                startActivity(mLessonIntent);
              }
            }, 500);
          }

        }
      }
    }
  };

  // Handler 클래스
  class ListenWordHandler extends Handler {

    private FrameLayout newParent = null;
    private int imageNum = 0;
    private int resourceId = 0;
    private int chkConVow = 0;

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);

      switch (msg.what) {
        case SEND_THREAD_START_MESSAGE:
          Log.d("thread", "thread start");
          char speakWord = ' ';
          chkConVow = msg.arg1;
          newParent = (FrameLayout) msg.obj;
          imageNum = (int)newParent.getTag();
          newParent.removeAllViews();
          ImageView iv = new ImageView(getApplicationContext());
          if(chkConVow == 1){
            resourceId = getResources()
                .getIdentifier("consonant" + imageNum +"_pink", "drawable", getPackageName());
            speakWord = currentConsonant;

            MusicService.MediaPlay(getApplicationContext(), correctSound[0]);
          } else if(chkConVow == 2){
            resourceId = getResources()
                .getIdentifier("vowel" + imageNum +"_pink", "drawable", getPackageName());
            speakWord = currentVowel;

            MusicService.MediaPlay(getApplicationContext(), correctSound[1]);
          } else {
            resourceId = getResources().getIdentifier("syl"+newParent.getTag()+"_pink","drawable", getPackageName());
            //resourceId = getResources().getIdentifier("han" + newParent.getId(),"drawable", getPackageName());
            speakWord = CommonUtils.characterCombination(currentConsonant, currentVowel, ' ');

            MusicService.MediaPlay(getApplicationContext(), correctSound[2]);
          }
          iv.setImageDrawable(getResources().getDrawable(resourceId));
          newParent.addView(iv);

          msg = this.obtainMessage();
          msg.what = FLIP_FLOP_IMAGE;
          mHandler.sendMessageDelayed(msg,1000);

          break;
        case FLIP_FLOP_IMAGE:
          Log.d("thread", "flip_flop ing");
          newParent.removeAllViews();
          ImageView iv2 = new ImageView(getApplicationContext());
          if(chkConVow == 1){
            resourceId = getResources()
                .getIdentifier("consonant" + imageNum +"_black", "drawable", getPackageName());
          }else if(chkConVow == 2){
            resourceId = getResources()
                .getIdentifier("vowel" + imageNum +"_black", "drawable", getPackageName());
          }else {
            resourceId = getResources().getIdentifier("syl"+newParent.getTag()+"_black","drawable", getPackageName());
          }
          iv2.setImageDrawable(getResources().getDrawable(resourceId));
          newParent.addView(iv2);

          msg = this.obtainMessage();
          msg.what = SEND_THREAD_STOP_MESSAGE;
          mHandler.sendMessage(msg);

          frame_consonant.setClickable(true);
          frame_vowelRight.setClickable(true);
          frame_vowelBottom.setClickable(true);

          break;

        case SEND_THREAD_STOP_MESSAGE:
          Log.d("thread", "thread stop");
          this.removeCallbacksAndMessages(null);
          break;

        default:
          break;
      }
    }

  };

  void adjustWindowView() {

    // Linear Layout (Total Size)
    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.LinearLayout);
    int maxWidth = linearLayout.getWidth();
    int maxHeight = linearLayout.getHeight();

    float limit = 0.35f;
    float ratioWH = 0.8f;
    float ratioIH = 0.588f;
    float ratioMH = 0.2f;
    float ratioGH = 0.1f;

    float imgHeight = maxHeight * limit;
    float imgWidth = imgHeight * ratioWH;
    float imgMarginInterval = imgHeight * ratioIH;
    float imgMargin = imgHeight*ratioMH;

    mPuzzleGap = maxHeight * limit * ratioGH;
    mPuzzleWidth = (int) imgWidth;
    mPuzzleHeight = (int) imgHeight;

    int width = (int) imgWidth;
    int height = (int) imgHeight;
    int marginInterval = (int) imgMarginInterval;
    int margin = (int) imgMargin;

    // Assign
    frame_consonant.getLayoutParams().width = width;
    frame_consonant.getLayoutParams().height = height+4;
    ((RelativeLayout.LayoutParams)frame_consonant.getLayoutParams()).leftMargin = margin;
    ((RelativeLayout.LayoutParams)frame_consonant.getLayoutParams()).topMargin = margin;

    frame_vowelRight.getLayoutParams().width = height;
    frame_vowelRight.getLayoutParams().height = width;
    ((RelativeLayout.LayoutParams)frame_vowelRight.getLayoutParams()).leftMargin = marginInterval;
    ((RelativeLayout.LayoutParams)frame_vowelRight.getLayoutParams()).setMarginStart(marginInterval);
    ((RelativeLayout.LayoutParams)frame_vowelRight.getLayoutParams()).setMarginEnd(margin);

    frame_vowelBottom.getLayoutParams().width = width;
    frame_vowelBottom.getLayoutParams().height = width;
    ((RelativeLayout.LayoutParams)frame_vowelBottom.getLayoutParams()).bottomMargin = margin;

    frame_consonant.removeAllViews();
    frame_vowelRight.removeAllViews();
    frame_vowelBottom.removeAllViews();
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);

    adjustWindowView();
  }
}
