package com.h2kresearch.syllablegame;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.h2kresearch.syllablegame.adapter.HangulListAdapter;
import com.h2kresearch.syllablegame.utils.CommonUtils;
import com.h2kresearch.syllablegame.utils.CommonUtils.ConsonantType;
import com.h2kresearch.syllablegame.utils.CommonUtils.VowelType;
import com.h2kresearch.syllablegame.utils.MusicService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyllableGameActivity extends AppCompatActivity {

  private static final int SEND_THREAD_START_MESSAGE = 0;
  private static final int FLIP_FLOP_IMAGE = 1;
  private static final int SEND_THREAD_STOP_MESSAGE = 2;

  private ListenWordHandler mHandler = null;

  FrameLayout frame_consonant;
  FrameLayout frame_vowelRight;
  FrameLayout frame_vowelBottom;
  FrameLayout frame_fullsize;
  RelativeLayout puzzleLayout;

  LinearLayout vowelContents;

  ListView consonantList;
  ListView vowelList;

  ImageView[] img_consonant;
  ImageView[] img_vowel;
  ImageView nextImage;
  ImageView scrollDown1;
  ImageView scrollDown2;

  TextView backBtn;

  Intent mLessonIntent;
  String[] select;

  Object[] dec_consonants;
  Object[] dec_vowels;

  int[] consonantsIdList;
  int[] vowelRightIdList;
  int[] vowelBottomIdList;

  int[] currentSound = {-1,-1,-1};
  int[] currentWord = {-1,-1};

  boolean flagMoved = false;

  int completeCnt = 0;

  float mPuzzleGap = 30.0f;
  int mPuzzleWidth = 240;
  int mPuzzleHeight = 300;
  int mListItemHeight = 1000;

  int scrollIndex = 1;
  int scrollIndex1 = 1;

  class WordAdapter extends ArrayAdapter{
    private ImageView[] words;
    public int numImage = 4;

    public WordAdapter(Context context, int textViewResourceId, ImageView[] words){
      super(context, textViewResourceId, words);
      this.words = words;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
      Context context = parent.getContext();

      /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
      if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_view_item, parent, false);
      }

      ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img);

      /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */
      iv_img.setImageDrawable(words[position].getDrawable());
      iv_img.setContentDescription(words[position].getContentDescription());
      iv_img.setOnTouchListener(mTouchListener);
      iv_img.setTag(words[position].getTag());

      LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.itemView);
      LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
      params.height = mListItemHeight/numImage;
      linearLayout.setLayoutParams(params);
      linearLayout.invalidate();

      return convertView;
    }

  }

  public void makeDragItems(){
    //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0);
    //layoutParams.weight = 1;
    //layoutParams.setMargins(0,10,0,10);

    int resourceId;
    consonantsIdList = new int[dec_consonants.length];
    img_consonant = new ImageView[dec_consonants.length];

    for (ConsonantType ct : ConsonantType.values()) {
      for (int i = 0; i < dec_consonants.length; i++) {
        if (ct.getName().equals(dec_consonants[i].toString())) {
          img_consonant[i] = new ImageView(this);
          resourceId = getResources()
              .getIdentifier("consonant" + (ct.ordinal() + 1), "drawable", getPackageName());
          img_consonant[i].setImageResource(resourceId);
          //img_consonant[i].setLayoutParams(layoutParams);
          //img_consonant[i].setAdjustViewBounds(true);

          //img_consonant[i].setOnTouchListener(mTouchListener);
          img_consonant[i].setId((ct.ordinal() + 1) + 100);
          Map param = new HashMap();
          param.put("realId",(ct.ordinal() + 1) + 100);
          img_consonant[i].setTag(param);
          consonantsIdList[i] = img_consonant[i].getId();
          img_consonant[i].setContentDescription(dec_consonants[i].toString());
        }
      }
    }

    int countRight = 0;
    int countBottom = 0;
    int countVowel;
    for (VowelType vt : VowelType.values()) {
      for (int i = 0; i < dec_vowels.length; i++) {
        if (vt.getVow().equals(dec_vowels[i].toString())) {
          if (vt.getSide().equals("R")) {
            countRight++;
          } else if (vt.getSide().equals("B")) {
            countBottom++;
          }
        }
      }
    }

    vowelRightIdList = new int[countRight];
    vowelBottomIdList = new int[countBottom];
    countVowel = countRight + countBottom;
    img_vowel = new ImageView[countVowel];
    countRight = 0;
    countBottom = 0;
    countVowel = 0;
    for (VowelType vt : VowelType.values()) {
      for (int i = 0; i < dec_vowels.length; i++) {
        if (vt.getVow().equals(dec_vowels[i].toString())) {
          if (vt.getSide().equals("R")) {
            img_vowel[countVowel] = new ImageView(this);
            resourceId = getResources()
                .getIdentifier("vowel" + (vt.ordinal() + 1), "drawable", getPackageName());
            img_vowel[countVowel].setImageResource(resourceId);
            img_vowel[countVowel].setId((vt.ordinal() + 1) + 200);
            Map param = new HashMap();
            param.put("realId",(vt.ordinal() + 1) + 200);
            img_vowel[countVowel].setTag(param);
            vowelRightIdList[countRight] = img_vowel[countVowel].getId();
            img_vowel[countVowel].setContentDescription(dec_vowels[i].toString());
            countRight++;
            countVowel++;
          } else if (vt.getSide().equals("B")) {
            img_vowel[countVowel] = new ImageView(this);
            resourceId = getResources()
                .getIdentifier("vowel" + (vt.ordinal() + 1), "drawable", getPackageName());
            img_vowel[countVowel].setImageResource(resourceId);
            img_vowel[countVowel].setId((vt.ordinal() + 1) + 300);
            Map param = new HashMap();
            param.put("realId",(vt.ordinal() + 1) + 300);
            img_vowel[countVowel].setTag(param);
            vowelBottomIdList[countBottom] = img_vowel[countVowel].getId();
            img_vowel[countVowel].setContentDescription(dec_vowels[i].toString());
            countBottom++;
            countVowel++;
          }
        }
      }
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_syllable_game);

    Intent preIntent = getIntent();
    select = preIntent.getStringArrayExtra("select");

    ArrayList<String[]> wordList = CommonUtils.deCombinationList(select);
    dec_consonants = CommonUtils.removeDuplicateArray(wordList.get(0));
    dec_vowels = CommonUtils.removeDuplicateArray(wordList.get(1));

    puzzleLayout = (RelativeLayout) findViewById(R.id.puzzleLayout);

    vowelContents = (LinearLayout) findViewById(R.id.vowelContents);

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

    backBtn = (TextView) findViewById(R.id.textViewL);
    backBtn.setOnClickListener(mClickListener);

    scrollDown1 = (ImageView) findViewById(R.id.scrollConsonants);
    scrollDown1.setOnLongClickListener(new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        mHandler1.sendEmptyMessageDelayed(10, 0);
        return false;
      }
    });
    scrollDown1.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mHandler1.removeMessages(10);
        consonantList.smoothScrollToPositionFromTop(scrollIndex++,0);
      }
    });
    scrollDown2 = (ImageView) findViewById(R.id.scrollVowels);
    scrollDown2.setOnLongClickListener(new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        mHandler2.sendEmptyMessageDelayed(10, 0);
        return false;
      }
    });
    scrollDown2.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mHandler2.removeMessages(10);
        vowelList.smoothScrollToPositionFromTop(scrollIndex1++,0);
      }
    });

//    nextImage = new ImageView(getApplicationContext());
//    nextImage.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
//    vowelContents.addView(nextImage);
//    nextImage.setVisibility(View.GONE);
    nextImage = findViewById(R.id.nextImage);
    nextImage.setOnClickListener(mNextClickListener);

    makeDragItems();

//    WordAdapter consonantAdapter = new WordAdapter(this, -1, img_consonant);
//    HangulListAdapter hangulListAdapter = new HangulListAdapter(consonantAdapter);
//    WordAdapter vowelAdapter = new WordAdapter(this, -1, img_vowel);
//    vowelAdapter.numImage = 5;
//    HangulListAdapter hangulListAdapter1 = new HangulListAdapter(vowelAdapter);
//
//    consonantList = (ListView) findViewById(R.id.consonantList);
//    consonantList.setAdapter(hangulListAdapter);
//
//    vowelList = (ListView) findViewById(R.id.vowelList);
//    vowelList.setAdapter(hangulListAdapter1);

  }

  Handler mHandler1 = new Handler(){
    public void handleMessage(Message msg){
      consonantList.smoothScrollToPositionFromTop(scrollIndex++,0);

      mHandler1.sendEmptyMessageDelayed(10,300);
    }
  };
  Handler mHandler2 = new Handler(){
    public void handleMessage(Message msg){
      vowelList.smoothScrollToPositionFromTop(scrollIndex1++,0);

      mHandler2.sendEmptyMessageDelayed(10,300);
    }
  };

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
          Map param = (Map)view.getTag();
          int getNum = -1;
          int imageId = -1;
          if (view != null) {
            FrameLayout newParent = (FrameLayout) v;
            boolean flag1 = false;
            boolean flag2 = false;
            if (v.getId() == R.id.consonantBlock) {
              flag1 = true;
              for (int consonantId : consonantsIdList) {
                if (consonantId == (int)param.get("realId")) {
                  frame_consonant.removeAllViews();
                  flag2 = true;
                  getNum = (int)param.get("realId") - 100;
                  imageId = getResources().getIdentifier("consonant" + getNum + "_black", "drawable", getPackageName());
                  currentSound[0] = getResources().getIdentifier("sound" + (getNum*11), "raw", getPackageName());
                  currentWord[0] = getNum;
                }
              }

            } else if (v.getId() == R.id.vowelRight) {
              flag1 = true;
              for (int vowelRight : vowelRightIdList) {
                if (vowelRight == (int)param.get("realId")) {
                  frame_vowelRight.removeAllViews();
                  frame_vowelBottom.removeAllViews();
                  flag2 = true;
                  getNum =(int)param.get("realId") - 200;
                  imageId = getResources().getIdentifier("vowel"  +getNum + "_black","drawable", getPackageName());
                  currentSound[1] = getResources().getIdentifier("sound" + (getNum), "raw", getPackageName());
                  currentWord[1] = getNum;
                }
              }

            } else if (v.getId() == R.id.vowelBottom) {
              flag1 = true;
              for (int vowelBottom : vowelBottomIdList) {
                if (vowelBottom == (int)param.get("realId")) {
                  frame_vowelRight.removeAllViews();
                  frame_vowelBottom.removeAllViews();
                  flag2 = true;
                  getNum =(int)param.get("realId") - 300;
                  imageId = getResources().getIdentifier("vowel"  +getNum + "_black","drawable", getPackageName());
                  currentSound[1] = getResources().getIdentifier("sound" + (getNum), "raw", getPackageName());
                  currentWord[1] = getNum;
                }
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
              currentSound[2] = getResources().getIdentifier("sound" + (currentWord[0] * 11 + currentWord[1]), "raw", getPackageName());
              MusicService.MediaPlay(getApplicationContext(), currentSound[2]);
            }

            if (flagMoved) {
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
            frame_consonant.setX(frame_consonant.getX() + mPuzzleGap);
            frame_vowelRight.setX(frame_vowelRight.getX() - mPuzzleGap);
            frame_vowelBottom.setY(frame_vowelBottom.getY() - mPuzzleGap*2);
            frame_vowelBottom.setX(frame_vowelBottom.getX() + mPuzzleGap);
            flagMoved = false;
          }

          ((View) (event.getLocalState())).setVisibility(View.VISIBLE);

          if (frame_consonant.getChildCount() == 1 && (frame_vowelRight.getChildCount() == 1
              || frame_vowelBottom.getChildCount() == 1)) {

            consonantList.setVisibility(View.GONE);
            scrollDown1.setVisibility(View.GONE);
            vowelList.setVisibility(View.GONE);
            scrollDown2.setVisibility(View.GONE);

            frame_consonant.setClickable(true);
            frame_vowelRight.setClickable(true);
            frame_vowelBottom.setClickable(true);

            nextImage.setClickable(true);
            nextImage.setVisibility(View.VISIBLE);

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
        msg.obj = frame_fullsize;
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
          nextImage.setVisibility(View.GONE);

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
              frame_consonant.setVisibility(View.GONE);
              frame_vowelRight.setVisibility(View.GONE);
              frame_vowelBottom.setVisibility(View.GONE);
              if(frame_consonant.getTag() != null){
                if(frame_vowelRight.getTag() != null){
                  imageId = ((int)frame_consonant.getTag() - 1) * 10 + (int)frame_vowelRight.getTag();
                }else if(frame_vowelBottom.getTag() != null){
                  imageId = ((int)frame_consonant.getTag() - 1) * 10 + (int)frame_vowelBottom.getTag();
                }
              }
              frame_fullsize = new FrameLayout(getApplicationContext());
              RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
              frame_fullsize.setLayoutParams(params);

              int padding = puzzleLayout.getHeight() * 20 / 100;
              frame_fullsize.setPadding(padding,padding,padding,padding);

              ImageView fullWordView = new ImageView(getApplicationContext());
              fullWordView.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("syl"+imageId+"_black","drawable", getPackageName())));
              frame_fullsize.addView(fullWordView);
              frame_fullsize.setTag(imageId);
              frame_fullsize.setOnClickListener(mClickListener);

              MusicService.MediaPlay(getApplicationContext(), currentSound[2]);

              puzzleLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
              puzzleLayout.addView(frame_fullsize);
              nextImage.setVisibility(View.VISIBLE);
            }
          }, 2500);

          completeCnt++;
          int resourceId = getResources()
              .getIdentifier("complete_counting" + completeCnt, "id", getPackageName());
          ImageView complete_img = (ImageView) findViewById(resourceId);
          int resourceId2 = getResources()
              .getIdentifier("han" + ((currentWord[0]*22 + currentWord[1] * 2) + 1), "drawable", getPackageName());
          complete_img.setImageDrawable(getResources().getDrawable(resourceId2));

          mSeq = 2;

        }else if(mSeq == 2){
          if(completeCnt == 5){
            mLessonIntent = new Intent(SyllableGameActivity.this, LessonActivity.class);
            mLessonIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mLessonIntent.putExtra("select", select);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
              @Override
              public void run() {
                startActivity(mLessonIntent);
              }
            }, 0);
          }

          nextImage.setVisibility(View.GONE);
          consonantList.setVisibility(View.VISIBLE);
          scrollDown1.setVisibility(View.VISIBLE);
          vowelList.setVisibility(View.VISIBLE);
          scrollDown2.setVisibility(View.VISIBLE);

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

        }
      }
    }
  };

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
          chkConVow = msg.arg1;
          newParent = (FrameLayout) msg.obj;
          imageNum = (int)newParent.getTag();
          newParent.removeAllViews();
          ImageView iv = new ImageView(getApplicationContext());
          if(chkConVow == 1){
            resourceId = getResources()
                .getIdentifier("consonant" + imageNum +"_pink", "drawable", getPackageName());

            MusicService.MediaPlay(getApplicationContext(), currentSound[0]);
          } else if(chkConVow == 2){
            resourceId = getResources()
                .getIdentifier("vowel" + imageNum +"_pink", "drawable", getPackageName());
            MusicService.MediaPlay(getApplicationContext(), currentSound[1]);
          } else {
            resourceId = getResources().getIdentifier("syl"+newParent.getTag()+"_pink","drawable", getPackageName());

            MusicService.MediaPlay(getApplicationContext(), currentSound[2]);

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
    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) frame_consonant.getLayoutParams();
    params.width = width;
    params.height = height + 4;
    params.leftMargin = margin;
    params.topMargin = margin;
    frame_consonant.setLayoutParams(params);
    frame_consonant.invalidate();

    params = (RelativeLayout.LayoutParams) frame_vowelRight.getLayoutParams();
    params.width = height;
    params.height = width;
    params.leftMargin = marginInterval;
    params.setMarginStart(marginInterval);
    params.setMarginEnd(margin);
    frame_vowelRight.setLayoutParams(params);
    frame_vowelRight.invalidate();

    params = (RelativeLayout.LayoutParams) frame_vowelBottom.getLayoutParams();
    params.width = width;
    params.height = width;
    params.bottomMargin = margin;
    frame_vowelBottom.setLayoutParams(params);
    frame_vowelBottom.invalidate();

    ListView listView = (ListView) findViewById(R.id.consonantList);
    mListItemHeight = listView.getHeight();

    WordAdapter consonantAdapter = new WordAdapter(this, -1, img_consonant);
    HangulListAdapter hangulListAdapter = new HangulListAdapter(consonantAdapter);
    WordAdapter vowelAdapter = new WordAdapter(this, -1, img_vowel);
    vowelAdapter.numImage = 5;
    HangulListAdapter hangulListAdapter1 = new HangulListAdapter(vowelAdapter);
    hangulListAdapter1.minDisplayCount = 5;

    consonantList = (ListView) findViewById(R.id.consonantList);
    consonantList.setAdapter(hangulListAdapter);

    vowelList = (ListView) findViewById(R.id.vowelList);
    vowelList.setAdapter(hangulListAdapter1);
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);

    adjustWindowView();
  }
}
