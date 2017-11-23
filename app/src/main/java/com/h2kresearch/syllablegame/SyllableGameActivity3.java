package com.h2kresearch.syllablegame;

import static android.speech.tts.TextToSpeech.ERROR;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import android.widget.TextView;
import android.widget.Toast;
import com.h2kresearch.syllablegame.com.h2kresearch.syllablegame.utils.CommonUtils;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class SyllableGameActivity3 extends AppCompatActivity {

  private TextToSpeech tts;

  FrameLayout frame_consonant;
  FrameLayout frame_vowelRight;
  FrameLayout frame_vowelBottom;

  LinearLayout consonantList;
  LinearLayout vowelList;

  ImageView[] img_consonant;
  ImageView[] img_vowelRight;
  ImageView[] img_vowelBottom;
  ImageView img_counting;

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

  enum ConsonantType {
    CONSONANT1("ㄱ"),CONSONANT2("ㄴ"),CONSONANT3("ㄷ"),CONSONANT4("ㄹ"),CONSONANT5("ㅁ"),CONSONANT6("ㅂ"),
    CONSONANT7("ㅅ"),CONSONANT8("ㅇ"),CONSONANT9("ㅈ"),CONSONANT10("ㅊ"),CONSONANT11("ㅋ"),CONSONANT12("ㅌ"),
    CONSONANT13("ㅍ"),CONSONANT14("ㅎ");

    String con;

    ConsonantType(String con){
      this.con = con;
    }

    public String getName(){
      return con;
    }
  }

  enum VowelType {
    VOWEL1("ㅏ","R"),VOWEL2("ㅑ","R"),VOWEL3("ㅓ","R"),VOWEL4("ㅕ","R"),VOWEL5("ㅗ","B"),
    VOWEL6("ㅛ","B"),VOWEL7("ㅜ","B"),VOWEL8("ㅠ","B"),VOWEL9("ㅡ","B"),VOWEL10("ㅣ","R");

    String vow;
    String side;
    VowelType(String vow, String side){
      this.vow = vow;
      this.side = side;
    }

    public String getVow(){
      return vow;
    }

    public String getSide(){
      return side;
    }
  }

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

    for(int i = 0; i < img_consonant.length; i++){
      if(correctAnswer[0] == (int)img_consonant[i].getTag()){
        consonantsId = img_consonant[i].getId();
        currentConsonant = img_consonant[i].getContentDescription().charAt(0);
      }
    }

    int vowInt = vows.get(rand.nextInt(vows.size()));
    if((vowInt >= 1 && vowInt <= 4) || vowInt ==10){
      correctAnswer[1] = getResources().getIdentifier("vowel" + (vowInt), "drawable", getPackageName());
      for(int i = 0; i < img_vowelRight.length; i++){
        if(correctAnswer[1] == (int)img_vowelRight[i].getTag()){
          vowelRightId = img_vowelRight[i].getId();
          vowelBottomId = -1;
          currentVowel = img_vowelRight[i].getContentDescription().charAt(0);
        }
      }
    }else{
      correctAnswer[2] = getResources().getIdentifier("vowel" + (vowInt), "drawable", getPackageName());
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
    setContentView(R.layout.activity_syllable_game3);

    Intent preIntent = getIntent();
    select = preIntent.getStringArrayExtra("select");

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    TextView tv = (TextView) findViewById(R.id.toolbar_title);
    tv.setText("음소 조합 확인");

    ArrayList<String[]> wordList = CommonUtils.deCombinationList(select);
    dec_consonants = CommonUtils.removeDuplicateArray(wordList.get(0));
    dec_vowels = CommonUtils.removeDuplicateArray(wordList.get(1));

    consonantList = (LinearLayout) findViewById(R.id.consonantList);
    vowelList = (LinearLayout) findViewById(R.id.vowelList);

    frame_consonant = (FrameLayout) findViewById(R.id.consonantBlock);
    frame_consonant.setOnDragListener(mDragListener);

    frame_vowelRight = (FrameLayout) findViewById(R.id.vowelRight);
    frame_vowelRight.setOnDragListener(mDragListener);

    frame_vowelBottom = (FrameLayout) findViewById(R.id.vowelBottom);
    frame_vowelBottom.setOnDragListener(mDragListener);

    TextView backBtn = (TextView) findViewById(R.id.backButton);
    backBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mLessonIntent = new Intent(SyllableGameActivity3.this, LessonActivity.class);
        mLessonIntent.putExtra("select", select);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
            startActivity(mLessonIntent);
          }
        }, 100);
      }
    });


    float xPixels = TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 77, getResources().getDisplayMetrics());

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
          img_consonant[i].setPadding(0, 10, 5, 0);
          resourceId = getResources()
              .getIdentifier("consonant" + (ct.ordinal() + 1), "drawable", getPackageName());
          img_consonant[i].setImageResource(resourceId);
          consonantList.addView(img_consonant[i]);
          img_consonant[i].setOnTouchListener(mTouchListener);
          img_consonant[i].setId(i + 100);
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
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96, getResources().getDisplayMetrics());
            img_vowelRight[countRight] = new ImageView(this);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams((int) xPixels, LayoutParams.WRAP_CONTENT);
            layoutParams.gravity= Gravity.TOP;
            img_vowelRight[countRight].setLayoutParams(layoutParams);
            img_vowelRight[countRight].setAdjustViewBounds(true);
            img_vowelRight[countRight].setPadding(0,10,10,0);
            resourceId = getResources().getIdentifier("vowel" + (vt.ordinal()+1), "drawable", getPackageName());
            img_vowelRight[countRight].setImageResource(resourceId);
            vowelList.addView(img_vowelRight[countRight]);
            img_vowelRight[countRight].setOnTouchListener(mTouchListener);
            img_vowelRight[countRight].setId(i+200);
            img_vowelRight[countRight].setTag(resourceId);
            img_vowelRight[countRight].setContentDescription(dec_vowels[i].toString());
            countRight++;
          }else if(vt.getSide().equals("B")){
            xPixels = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 77, getResources().getDisplayMetrics());
            img_vowelBottom[countBottom] = new ImageView(this);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams((int) xPixels, LayoutParams.WRAP_CONTENT);
            layoutParams.gravity= Gravity.TOP;
            img_vowelBottom[countBottom].setLayoutParams(layoutParams);
            img_vowelBottom[countBottom].setAdjustViewBounds(true);
            img_vowelBottom[countBottom].setPadding(0,10,10,0);
            resourceId = getResources().getIdentifier("vowel" + (vt.ordinal()+1), "drawable", getPackageName());
            img_vowelBottom[countBottom].setImageResource(resourceId);
            vowelList.addView(img_vowelBottom[countBottom]);
            img_vowelBottom[countBottom].setOnTouchListener(mTouchListener);
            img_vowelBottom[countBottom].setId(i+300);
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
          .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());
      float yPixels = TypedValue
          .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());
      shadowSize.set((int) xPixels, (int) yPixels);
      shadowTouchPoint.set((int) xPixels / 2, (int) yPixels / 2);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
      canvas.scale(2.6f, 2.6f);
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
          if (view != null) {
            FrameLayout newParent = (FrameLayout) v;
            boolean flag1 = false;
            boolean flag2 = false;
            if (v.getId() == R.id.consonantBlock) {
              flag1 = true;
              if (consonantsId == view.getId()) {
                frame_consonant.removeAllViews();
                flag2 = true;
              }
              currentConsonant = view.getContentDescription().charAt(0);

            } else if (v.getId() == R.id.vowelRight) {
              flag1 = true;
              if (vowelRightId == view.getId()) {
                frame_vowelRight.removeAllViews();
                frame_vowelBottom.removeAllViews();
                flag2 = true;
              }
              currentVowel = view.getContentDescription().charAt(0);

            } else if (v.getId() == R.id.vowelBottom) {
              flag1 = true;
              if (vowelBottomId == view.getId()) {
                frame_vowelRight.removeAllViews();
                frame_vowelBottom.removeAllViews();
                flag2 = true;
              }
              currentVowel = view.getContentDescription().charAt(0);
            }

            if (flag1 && flag2) {
              ImageView oldView = (ImageView) view;
              ImageView newView = new ImageView(getApplicationContext());
              newView.setImageBitmap(((BitmapDrawable) oldView.getDrawable()).getBitmap());

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

              Toast.makeText(SyllableGameActivity3.this, "우와 멋진데~", Toast.LENGTH_SHORT).show();

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
            waitTime();

            frame_consonant.removeAllViews();
            frame_vowelBottom.removeAllViews();
            frame_vowelRight.removeAllViews();

            if (completeCnt != 9) {
              completeCnt++;
              int resourceId = getResources()
                  .getIdentifier("count" + completeCnt, "drawable", getPackageName());
              img_counting.setImageDrawable(getResources().getDrawable(resourceId));
              makeExamples(dec_consonants,dec_vowels);

            } else {
              img_counting.setImageDrawable(getResources().getDrawable(R.drawable.count10));

              mLessonIntent = new Intent(SyllableGameActivity3.this, LessonActivity.class);
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
          return true;
      }
      return false;
    }
  };

  public synchronized void waitTime() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
