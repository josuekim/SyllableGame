package com.h2kresearch.syllablegame;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.h2kresearch.syllablegame.utils.CommonUtils;
import com.h2kresearch.syllablegame.utils.CommonUtils.ConsonantType;
import com.h2kresearch.syllablegame.utils.CommonUtils.VowelType;
import java.util.ArrayList;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class SyllableGameActivity extends ParentActivity {

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

  int[] consonantsIdList;
  int[] vowelRightIdList;
  int[] vowelBottomIdList;

  char currentConsonant;
  char currentVowel;

  boolean flagMoved = false;

  int completeCnt = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_syllable_game);

    Intent preIntent = getIntent();
    select = preIntent.getStringArrayExtra("select");

    ArrayList<String[]> wordList = CommonUtils.deCombinationList(select);
    Object[] dec_consonants = CommonUtils.removeDuplicateArray(wordList.get(0));
    Object[] dec_vowels = CommonUtils.removeDuplicateArray(wordList.get(1));

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

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
//        mLessonIntent = new Intent(SyllableGameActivity.this, LessonActivity.class);
//        mLessonIntent.putExtra("select", select);
//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//          @Override
//          public void run() {
//            startActivity(mLessonIntent);
//          }
//        }, 100);
        onBackPressed();
      }
    });

    float xPixels = TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 77, getResources().getDisplayMetrics());
    float yPixels = TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96, getResources().getDisplayMetrics());

    int resourceId = -1;
    consonantsIdList = new int[dec_consonants.length];
    img_consonant = new ImageView[dec_consonants.length];

    for (ConsonantType ct : ConsonantType.values()) {
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
          consonantsIdList[i] = img_consonant[i].getId();
          img_consonant[i].setContentDescription(dec_consonants[i].toString());
        }
      }
    }

    int countRight = 0;
    int countBottom = 0;
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
    img_vowelRight = new ImageView[countRight];
    img_vowelBottom = new ImageView[countBottom];
    countRight = 0;
    countBottom = 0;
    for (VowelType vt : VowelType.values()) {
      for (int i = 0; i < dec_vowels.length; i++) {
        if (vt.getVow().equals(dec_vowels[i].toString())) {
          if (vt.getSide().equals("R")) {
            xPixels = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96,
                    getResources().getDisplayMetrics());
            img_vowelRight[countRight] = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) xPixels,
                LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.TOP;
            img_vowelRight[countRight].setLayoutParams(layoutParams);
            img_vowelRight[countRight].setAdjustViewBounds(true);
            img_vowelRight[countRight].setPadding(0, 10, 10, 0);
            resourceId = getResources()
                .getIdentifier("vowel" + (vt.ordinal() + 1), "drawable", getPackageName());
            img_vowelRight[countRight].setImageResource(resourceId);
            vowelList.addView(img_vowelRight[countRight]);
            img_vowelRight[countRight].setOnTouchListener(mTouchListener);
            img_vowelRight[countRight].setId(i + 200);
            vowelRightIdList[countRight] = img_vowelRight[countRight].getId();
            img_vowelRight[countRight].setContentDescription(dec_vowels[i].toString());
            countRight++;
          } else if (vt.getSide().equals("B")) {
            xPixels = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 77,
                    getResources().getDisplayMetrics());
            img_vowelBottom[countBottom] = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) xPixels,
                LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.TOP;
            img_vowelBottom[countBottom].setLayoutParams(layoutParams);
            img_vowelBottom[countBottom].setAdjustViewBounds(true);
            img_vowelBottom[countBottom].setPadding(0, 10, 10, 0);
            resourceId = getResources()
                .getIdentifier("vowel" + (vt.ordinal() + 1), "drawable", getPackageName());
            img_vowelBottom[countBottom].setImageResource(resourceId);
            vowelList.addView(img_vowelBottom[countBottom]);
            img_vowelBottom[countBottom].setOnTouchListener(mTouchListener);
            img_vowelBottom[countBottom].setId(i + 300);
            vowelBottomIdList[countBottom] = img_vowelBottom[countBottom].getId();
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

  View.OnLongClickListener mLongClickListener = new View.OnLongClickListener(){

    @Override
    public boolean onLongClick(View v) {

      ClipData clipData = ClipData.newPlainText("", "");
      v.startDrag(clipData, new ImageDrag(v, (int) v.getX(), (int) v.getY()), v, 0);
      v.setVisibility(View.INVISIBLE);

      return true;
    }
  };
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
              for (int consonantId : consonantsIdList) {
                if (consonantId == view.getId()) {
                  frame_consonant.removeAllViews();
                  flag2 = true;
                }
              }
              currentConsonant = view.getContentDescription().charAt(0);

            } else if (v.getId() == R.id.vowelRight) {
              flag1 = true;
              for (int vowelRight : vowelRightIdList) {
                if (vowelRight == view.getId()) {
                  frame_vowelRight.removeAllViews();
                  frame_vowelBottom.removeAllViews();
                  flag2 = true;
                }
              }
              currentVowel = view.getContentDescription().charAt(0);

            } else if (v.getId() == R.id.vowelBottom) {
              flag1 = true;
              for (int vowelBottom : vowelBottomIdList) {
                if (vowelBottom == view.getId()) {
                  frame_vowelRight.removeAllViews();
                  frame_vowelBottom.removeAllViews();
                  flag2 = true;
                }
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

              Toast.makeText(SyllableGameActivity.this, "우와 멋진데~", Toast.LENGTH_SHORT).show();

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

            if (completeCnt != 1) {
              completeCnt++;
              int resourceId = getResources()
                  .getIdentifier("count" + completeCnt, "drawable", getPackageName());
              img_counting.setImageDrawable(getResources().getDrawable(resourceId));

            } else {
              img_counting.setImageDrawable(getResources().getDrawable(R.drawable.count10));

              Dialog custom = new Dialog(SyllableGameActivity.this);
              custom.setContentView(R.layout.custom_dialog);
              ImageView applaud = (ImageView) custom.findViewById(R.id.imageForApplaud);
              applaud.setImageResource(R.drawable.rabbit);
              Glide.with(custom.getContext()).load(R.drawable.rabbit).into(applaud);
              custom.show();

              mLessonIntent = new Intent(SyllableGameActivity.this, LessonActivity.class);
              mLessonIntent.putExtra("select", select);

              Handler handler = new Handler();
              handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                  startActivity(mLessonIntent);
                }
              }, 1500);
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

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);

    // Display Metrics
//    DisplayMetrics displayMetrics = new DisplayMetrics();
//    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//    int dp = Math.round(height / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

//    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)frame_vowelRight.getLayoutParams();
//
//    // Linear Layout (Total Size)
//    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.LinearLayout);
//    int maxWidth = linearLayout.getWidth();
//    int maxHeight = linearLayout.getHeight();
//
//    // Width/Height Ratio
//    int originalW = frame_consonant.getWidth();
//    int originalH = frame_consonant.getHeight();
//    int originalM = ((RelativeLayout.LayoutParams)frame_vowelRight.getLayoutParams()).getMarginStart();
//
//    // Ratio
//    float ratio = (float) originalW / (float) originalH;
//    ratio = 0.8f;
//
//    // Target Size
//    float limit = 0.5f;
//    int height = (int) (maxHeight * limit);
//    int width = (int) (height * ratio);
//    float scale = (float)width / (float)originalW;
//    int margin = (int) (originalM * scale);
//
//    // Assign
//    frame_consonant.getLayoutParams().width = width;
//    frame_consonant.getLayoutParams().height = height;
//
//    frame_vowelRight.getLayoutParams().width = height;
//    frame_vowelRight.getLayoutParams().height = width;
//    ((RelativeLayout.LayoutParams)frame_vowelRight.getLayoutParams()).leftMargin = margin;
//    ((RelativeLayout.LayoutParams)frame_vowelRight.getLayoutParams()).setMarginStart(margin);
//
//    frame_vowelBottom.getLayoutParams().width = width;
//    frame_vowelBottom.getLayoutParams().height = width;
  }
}
