package com.h2kresearch.syllablegame;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.h2kresearch.syllablegame.com.h2kresearch.syllablegame.utils.CommonUtils;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class SyllableGameActivity extends AppCompatActivity {

  private TextToSpeech tts;

  FrameLayout frame_consonant;
  FrameLayout frame_vowelLeft;
  FrameLayout frame_vowelBottom;

  LinearLayout consonantList;

  ImageView[] img_consonant = new ImageView[6];
  ImageView[] img_vowelLeft = new ImageView[4];
  ImageView[] img_vowelBottom = new ImageView[2];
  ImageView img_counting;

  int[] consonantsIdList;
  int[] vowelLeftIdList;
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
    String[] select = preIntent.getStringArrayExtra("select");

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    consonantList = (LinearLayout) findViewById(R.id.consonantList);

    frame_consonant = (FrameLayout) findViewById(R.id.consonantBlock);
    frame_consonant.setOnDragListener(mDragListener);

    frame_vowelLeft = (FrameLayout) findViewById(R.id.vowelLeft);
    frame_vowelLeft.setOnDragListener(mDragListener);

    frame_vowelBottom = (FrameLayout) findViewById(R.id.vowelBottom);
    frame_vowelBottom.setOnDragListener(mDragListener);

    int resourceId = -1;
    consonantsIdList = new int[6];
    for (int i = 0; i < 6; i++) {
      resourceId = getResources().getIdentifier("consonant" + (i + 1), "id", getPackageName());
      img_consonant[i] = (ImageView) findViewById(resourceId);
      img_consonant[i].setOnTouchListener(mTouchListener);
      consonantsIdList[i] = resourceId;
    }

    //동적 이미지뷰 추가 코드 샘플
    float xPixels = TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 77, getResources().getDisplayMetrics());
    float yPixels = TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96, getResources().getDisplayMetrics());

    ImageView iv = new ImageView(this);
    iv.setLayoutParams(new LinearLayout.LayoutParams((int) xPixels, (int) yPixels));
    iv.setBackgroundResource(R.drawable.consonant7);
    consonantList.addView(iv);
    ///////////////////////////////////

    vowelLeftIdList = new int[4];
    for (int i = 0; i < 4; i++) {
      resourceId = getResources().getIdentifier("vowel" + (i + 1), "id", getPackageName());
      img_vowelLeft[i] = (ImageView) findViewById(resourceId);
      img_vowelLeft[i].setOnTouchListener(mTouchListener);
      vowelLeftIdList[i] = resourceId;
    }

    vowelBottomIdList = new int[2];
    for (int i = 0; i < 2; i++) {
      resourceId = getResources().getIdentifier("vowel" + (i + 5), "id", getPackageName());
      img_vowelBottom[i] = (ImageView) findViewById(resourceId);
      img_vowelBottom[i].setOnTouchListener(mTouchListener);
      vowelBottomIdList[i] = resourceId;
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
    float xPixels;
    float yPixels;

    public ImageDrag(View v, int x, int y) {
      super(v);
      mView = v;
      mX = x;
      mY = y;
      mWidth = v.getWidth() * SCALING_FACTOR;
      mHeight = v.getHeight() * SCALING_FACTOR;
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

  private static final int SCALING_FACTOR = 2;

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
            frame_vowelLeft.setX(frame_vowelLeft.getX() + 30f);
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

            } else if (v.getId() == R.id.vowelLeft) {
              flag1 = true;
              for (int vowelLeft : vowelLeftIdList) {
                if (vowelLeft == view.getId()) {
                  frame_vowelLeft.removeAllViews();
                  frame_vowelBottom.removeAllViews();
                  flag2 = true;
                }
              }
              currentVowel = view.getContentDescription().charAt(0);

            } else if (v.getId() == R.id.vowelBottom) {
              flag1 = true;
              for (int vowelBottom : vowelBottomIdList) {
                if (vowelBottom == view.getId()) {
                  frame_vowelLeft.removeAllViews();
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

            if (frame_consonant.getChildCount() == 1 && (frame_vowelLeft.getChildCount() == 1
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
              frame_vowelLeft.setX(frame_vowelLeft.getX() - 30f);
              frame_vowelBottom.setY(frame_vowelBottom.getY() - 60f);
              frame_vowelBottom.setX(frame_vowelBottom.getX() + 30f);
              flagMoved = false;
            }

          }
          return true;
        case DragEvent.ACTION_DRAG_ENDED:
          if (flagMoved) {
            frame_consonant.setX(frame_consonant.getX() + 30f);
            frame_vowelLeft.setX(frame_vowelLeft.getX() - 30f);
            frame_vowelBottom.setY(frame_vowelBottom.getY() - 60f);
            frame_vowelBottom.setX(frame_vowelBottom.getX() + 30f);
            flagMoved = false;
          }

          ((View) (event.getLocalState())).setVisibility(View.VISIBLE);

          if (frame_consonant.getChildCount() == 1 && (frame_vowelLeft.getChildCount() == 1
              || frame_vowelBottom.getChildCount() == 1)) {
            waitTime();

            frame_consonant.removeAllViews();
            frame_vowelBottom.removeAllViews();
            frame_vowelLeft.removeAllViews();

            if (completeCnt != 10) {
              completeCnt++;
              int resourceId = getResources()
                  .getIdentifier("count" + completeCnt, "drawable", getPackageName());
              img_counting.setImageDrawable(getResources().getDrawable(resourceId));

            } else {
              completeCnt = 0;
              img_counting.setImageDrawable(getResources().getDrawable(R.drawable.count));
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
