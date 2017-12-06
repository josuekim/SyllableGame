package com.h2kresearch.syllablegame;

import static android.speech.tts.TextToSpeech.ERROR;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import android.widget.Toast;
import com.h2kresearch.syllablegame.utils.CommonUtils;
import com.h2kresearch.syllablegame.utils.CommonUtils.ConsonantType;
import com.h2kresearch.syllablegame.utils.CommonUtils.VowelType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class SyllableGameActivity2 extends AppCompatActivity {

  private static final int SEND_THREAD_START_MESSAGE = 0;
  private static final int FLIP_FLOP_IMAGE = 1;
  private static final int SEND_THREAD_STOP_MESSAGE = 2;

  private ListenWordHandler mHandler = null;

  private TextToSpeech tts;

  FrameLayout frame_consonant;
  FrameLayout frame_vowelRight;
  FrameLayout frame_vowelBottom;
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

  char currentConsonant;
  char currentVowel;

  boolean flagMoved = false;

  int completeCnt = 0;

  public void makeExamples(Object[] dec_c, Object[] dec_v){
    ArrayList<Integer> cons = new ArrayList<>();
    ArrayList<Integer> vows = new ArrayList<>();

    for(ConsonantType ct : ConsonantType.values()){
      for(int i = 0; i < dec_c.length; i++){
        if(ct.getName().equals(dec_c[i].toString())) {
          cons.add((ct.ordinal() + 1));
        }
      }
    }
    for(VowelType vt : VowelType.values()){
      for(int i = 0; i < dec_v.length; i++){
        if(vt.getVow().equals(dec_v[i].toString())) {
          vows.add((vt.ordinal() + 1));
        }
      }
    }
    Random rand = new Random(System.currentTimeMillis());
    int conInt = rand.nextInt(cons.size());
    correctAnswer[0] = getResources().getIdentifier("consonant" + cons.get(conInt), "drawable", getPackageName());
    frame_consonant.setBackground(getResources().getDrawable(getResources().getIdentifier("consonant" + cons.get(conInt) +"_blur", "drawable", getPackageName())));

    for(int i = 0; i < img_consonant.length; i++){
      if(correctAnswer[0] == (int)img_consonant[i].getTag()){
        consonantsId = img_consonant[i].getId();
        currentConsonant = img_consonant[i].getContentDescription().charAt(0);
      }
    }

    int vowInt = vows.get(rand.nextInt(vows.size()));
    if((vowInt >= 1 && vowInt <= 4) || vowInt ==10){
      correctAnswer[1] = getResources().getIdentifier("vowel" + (vowInt), "drawable", getPackageName());
      frame_vowelRight.setBackground(getResources().getDrawable(getResources().getIdentifier("vowel" + (vowInt) +"_blur", "drawable", getPackageName())));
      frame_vowelBottom.setBackground(getResources().getDrawable(R.drawable.blank_3));
      for(int i = 0; i < img_vowelRight.length; i++){
        if(correctAnswer[1] == (int)img_vowelRight[i].getTag()){
          vowelRightId = img_vowelRight[i].getId();
          vowelBottomId = -1;
          currentVowel = img_vowelRight[i].getContentDescription().charAt(0);
        }
      }
    }else{
      correctAnswer[2] = getResources().getIdentifier("vowel" + (vowInt), "drawable", getPackageName());
      frame_vowelBottom.setBackground(getResources().getDrawable(getResources().getIdentifier("vowel" + (vowInt) +"_blur", "drawable", getPackageName())));
      frame_vowelRight.setBackground(getResources().getDrawable(R.drawable.blank_2));
      for(int i = 0; i < img_vowelBottom.length; i++){
        if(correctAnswer[2] == (int)img_vowelBottom[i].getTag()){
          vowelBottomId = img_vowelBottom[i].getId();
          vowelRightId = -1;
          currentVowel = img_vowelBottom[i].getContentDescription().charAt(0);
        }
      }
    }

    char completeWord = CommonUtils
        .characterCombination(currentConsonant, currentVowel, ' ');

    tts.setPitch(1f);
    tts.setSpeechRate(0.8f);
    tts.speak(String.valueOf(completeWord), TextToSpeech.QUEUE_FLUSH, null);

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_syllable_game2);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    TextView tv = (TextView) findViewById(R.id.toolbar_title);
    tv.setText("음절 조합 연습");

    Intent preIntent = getIntent();
    select = preIntent.getStringArrayExtra("select");

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

    backBtn = (TextView) findViewById(R.id.backButton);
    backBtn.setOnClickListener(mClickListener);



    float xPixels = TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 83, getResources().getDisplayMetrics());

    int resourceId = -1;
    img_consonant = new ImageView[dec_consonants.length];

    for(ConsonantType ct : ConsonantType.values()) {
      for (int i = 0; i < dec_consonants.length; i++) {
        if (ct.getName().equals(dec_consonants[i].toString())) {
          img_consonant[i] = new ImageView(this);
          LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) xPixels,
              LayoutParams.WRAP_CONTENT);
          layoutParams.gravity = Gravity.TOP;
          img_consonant[i].setLayoutParams(layoutParams);
          img_consonant[i].setAdjustViewBounds(true);
          img_consonant[i].setPadding(50, 10, 5, 0);
          resourceId = getResources()
              .getIdentifier("consonant" + (ct.ordinal() + 1), "drawable", getPackageName());
          img_consonant[i].setImageResource(resourceId);
          consonantList.addView(img_consonant[i]);
          img_consonant[i].setOnTouchListener(mTouchListener);
          img_consonant[i].setId((ct.ordinal() + 1) + 100);
          img_consonant[i].setTag(resourceId);
          img_consonant[i].setContentDescription(dec_consonants[i].toString());
        }
      }
    }

    int countRight = 0;
    int countBottom = 0;
    for(VowelType vt : VowelType.values()){
      for(int i = 0; i <dec_vowels.length; i++){
        if(vt.getVow().equals(dec_vowels[i].toString())){
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
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams((int) xPixels, LayoutParams.WRAP_CONTENT);
            layoutParams.gravity= Gravity.TOP;
            img_vowelRight[countRight].setLayoutParams(layoutParams);
            img_vowelRight[countRight].setAdjustViewBounds(true);
            img_vowelRight[countRight].setPadding(50,10,10,0);
            resourceId = getResources().getIdentifier("vowel" + (vt.ordinal()+1), "drawable", getPackageName());
            img_vowelRight[countRight].setImageResource(resourceId);
            vowelList.addView(img_vowelRight[countRight]);
            img_vowelRight[countRight].setOnTouchListener(mTouchListener);
            img_vowelRight[countRight].setId((vt.ordinal()+1)+200);
            img_vowelRight[countRight].setTag(resourceId);
            img_vowelRight[countRight].setContentDescription(dec_vowels[i].toString());
            countRight++;
          }else if(vt.getSide().equals("B")){
            xPixels = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 99, getResources().getDisplayMetrics());
            img_vowelBottom[countBottom] = new ImageView(this);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams((int) xPixels, LayoutParams.WRAP_CONTENT);
            layoutParams.gravity= Gravity.TOP;
            img_vowelBottom[countBottom].setLayoutParams(layoutParams);
            img_vowelBottom[countBottom].setAdjustViewBounds(true);
            img_vowelBottom[countBottom].setPadding(92,10,10,0);
            resourceId = getResources().getIdentifier("vowel" + (vt.ordinal()+1), "drawable", getPackageName());
            img_vowelBottom[countBottom].setImageResource(resourceId);
            vowelList.addView(img_vowelBottom[countBottom]);
            img_vowelBottom[countBottom].setOnTouchListener(mTouchListener);
            img_vowelBottom[countBottom].setId((vt.ordinal()+1)+300);
            img_vowelBottom[countBottom].setTag(resourceId);
            img_vowelBottom[countBottom].setContentDescription(dec_vowels[i].toString());
            countBottom++;
          }
        }
      }
    }

    img_counting = (ImageView) findViewById(R.id.complete_counting);

    tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
      @Override
      public void onInit(int status) {
        if (status != ERROR) {
          tts.setLanguage(Locale.KOREAN);
          makeExamples(dec_consonants,dec_vowels);
        }
      }
    });

  }
  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (tts != null) {
      tts.stop();
      tts.shutdown();
      tts = null;
    }
  }

  class ImageDrag extends View.DragShadowBuilder {

    View mView;
    int mX;
    int mY;
    int mWidth;
    int mHeight;

    public ImageDrag(View v, int x, int y) {
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
      float xPixels = TypedValue
          .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
      float yPixels = TypedValue
          .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 125, getResources().getDisplayMetrics());
      shadowSize.set((int) xPixels, (int) yPixels);
      shadowTouchPoint.set((int) xPixels / 2, (int) yPixels / 2);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
      canvas.scale(1.5f, 1.5f);
      super.onDrawShadow(canvas);
    }
  }

  View.OnTouchListener mTouchListener = new View.OnTouchListener() {

    @Override
    public boolean onTouch(View v, MotionEvent event) {
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          ClipData clipData = ClipData.newPlainText("", "");
          v.startDrag(clipData, new SyllableGameActivity2.ImageDrag(v, (int) event.getX(), (int) event.getY()), v, 0);
          v.setVisibility(View.INVISIBLE);

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
            frame_consonant.setX(frame_consonant.getX() - 30f);
            frame_vowelRight.setX(frame_vowelRight.getX() + 30f);
            frame_vowelBottom.setY(frame_vowelBottom.getY() + 60f);
            frame_vowelBottom.setX(frame_vowelBottom.getX() - 30f);
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
            if (v.getId() == R.id.consonantBlock) {
              flag1 = true;
              if (consonantsId == view.getId()) {
                frame_consonant.removeAllViews();
                flag2 = true;
                getNum = (int)view.getId() - 100;
                imageId = getResources()
                    .getIdentifier("consonant" + getNum +"_black", "drawable", getPackageName());
              }
              currentConsonant = view.getContentDescription().charAt(0);

            } else if (v.getId() == R.id.vowelRight) {
              flag1 = true;
              if (vowelRightId == view.getId()) {
                frame_vowelRight.removeAllViews();
                frame_vowelBottom.removeAllViews();
                flag2 = true;
                getNum = (int)view.getId() - 200;
                imageId = getResources()
                    .getIdentifier("vowel" + getNum +"_black", "drawable", getPackageName());
              }
              currentVowel = view.getContentDescription().charAt(0);

            } else if (v.getId() == R.id.vowelBottom) {
              flag1 = true;
              if (vowelBottomId == view.getId()) {
                frame_vowelRight.removeAllViews();
                frame_vowelBottom.removeAllViews();
                flag2 = true;
                getNum = (int)view.getId() - 300;
                imageId = getResources()
                    .getIdentifier("vowel" + getNum +"_black", "drawable", getPackageName());
              }
              currentVowel = view.getContentDescription().charAt(0);
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
              char completeWord = CommonUtils
                  .characterCombination(currentConsonant, currentVowel, ' ');

              tts.setPitch(1f);
              tts.setSpeechRate(0.8f);
              tts.speak(String.valueOf(completeWord), TextToSpeech.QUEUE_FLUSH, null);

              //Toast.makeText(SyllableGameActivity2.this, "우와 멋진데~", Toast.LENGTH_SHORT).show();

            }

            if (flagMoved) {
              frame_consonant.setX(frame_consonant.getX() + 30f);
              frame_vowelRight.setX(frame_vowelRight.getX() - 30f);
              frame_vowelBottom.setY(frame_vowelBottom.getY() - 60f);
              frame_vowelBottom.setX(frame_vowelBottom.getX() + 30f);
              flagMoved = false;
            }

          }
          return true;
        case DragEvent.ACTION_DRAG_ENDED:
          if (flagMoved) {
            frame_consonant.setX(frame_consonant.getX() + 30f);
            frame_vowelRight.setX(frame_vowelRight.getX() - 30f);
            frame_vowelBottom.setY(frame_vowelBottom.getY() - 60f);
            frame_vowelBottom.setX(frame_vowelBottom.getX() + 30f);
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

            //frame_consonant.removeAllViews();
            //frame_vowelBottom.removeAllViews();
            //frame_vowelRight.removeAllViews();

            /*if (completeCnt != 9) {
              completeCnt++;
              int resourceId = getResources()
                  .getIdentifier("count" + completeCnt, "drawable", getPackageName());
              img_counting.setImageDrawable(getResources().getDrawable(resourceId));
              makeExamples(dec_consonants,dec_vowels);

            } else {
              img_counting.setImageDrawable(getResources().getDrawable(R.drawable.count10));

              mLessonIntent = new Intent(SyllableGameActivity2.this, LessonActivity.class);
              mLessonIntent.putExtra("select", select);

              Handler handler = new Handler();
              handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                  startActivity(mLessonIntent);
                }
              }, 500);
            }*/

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
    }
  };

  View.OnClickListener mNextClickListener = new View.OnClickListener(){
    private int mSeq = 1;

    @Override
    public void onClick(View v) {
      if(v.getId() == nextImage.getId()){
        if(mSeq == 1){
          mSeq = 2;

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
              frame_consonant.setVisibility(View.INVISIBLE);
              frame_vowelRight.setVisibility(View.INVISIBLE);
              frame_vowelBottom.setVisibility(View.INVISIBLE);
              if(frame_consonant.getTag() != null){
                if(frame_vowelRight.getTag() != null){
                  imageId = ((int)frame_consonant.getTag() - 1) * 10 + (int)frame_vowelRight.getTag();
                }else if(frame_vowelBottom.getTag() != null){
                  imageId = ((int)frame_consonant.getTag() - 1) * 10 + (int)frame_vowelBottom.getTag();
                }
              }
              FrameLayout newFrame = new FrameLayout(getApplicationContext());
              newFrame.setBackground(getResources().getDrawable(getResources().getIdentifier("syl"+imageId+"_black","drawable", getPackageName())));

              char completeWord = CommonUtils
                  .characterCombination(currentConsonant, currentVowel, ' ');

              tts.setPitch(1f);
              tts.setSpeechRate(0.8f);
              tts.speak(String.valueOf(completeWord), TextToSpeech.QUEUE_FLUSH, null);

              puzzleLayout.addView(newFrame);
            }
          }, 2500);




        }else if(mSeq == 2){
          Toast.makeText(SyllableGameActivity2.this, "ggg", Toast.LENGTH_SHORT).show();
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
          } else {
            resourceId = getResources()
                .getIdentifier("vowel" + imageNum +"_pink", "drawable", getPackageName());
            speakWord = currentVowel;
          }
          iv.setImageDrawable(getResources().getDrawable(resourceId));
          newParent.addView(iv);

          tts.setPitch(1f);
          tts.setSpeechRate(0.8f);
          tts.speak(String.valueOf(speakWord), TextToSpeech.QUEUE_FLUSH, null);

          msg = this.obtainMessage();
          msg.what = FLIP_FLOP_IMAGE;
          msg.arg1 = 1;
          mHandler.sendMessageDelayed(msg,1000);

          break;
        case FLIP_FLOP_IMAGE:
          Log.d("thread", "flip_flop ing");
          newParent.removeAllViews();
          ImageView iv2 = new ImageView(getApplicationContext());
          if(chkConVow == 1){
            resourceId = getResources()
                .getIdentifier("consonant" + imageNum +"_black", "drawable", getPackageName());
          }else {
            resourceId = getResources()
                .getIdentifier("vowel" + imageNum +"_black", "drawable", getPackageName());
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

}
