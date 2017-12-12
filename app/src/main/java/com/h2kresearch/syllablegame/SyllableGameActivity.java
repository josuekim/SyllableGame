package com.h2kresearch.syllablegame;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.h2kresearch.syllablegame.utils.CommonUtils;
import com.h2kresearch.syllablegame.utils.CommonUtils.ConsonantType;
import com.h2kresearch.syllablegame.utils.CommonUtils.VowelType;
import java.util.ArrayList;

public class SyllableGameActivity extends AppCompatActivity {

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

  float mPuzzleGap = 30.0f;
  int mPuzzleWidth = 240;
  int mPuzzleHeight = 300;

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

    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0);
    layoutParams.weight = 1;
    layoutParams.setMargins(0,10,0,10);

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
          resourceId = getResources()
              .getIdentifier("consonant" + (ct.ordinal() + 1), "drawable", getPackageName());
          img_consonant[i].setImageResource(resourceId);
          consonantList.addView(img_consonant[i]);

          //          LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) xPixels,
//              LayoutParams.WRAP_CONTENT);
//          layoutParams.gravity = Gravity.TOP;
          img_consonant[i].setLayoutParams(layoutParams);
          img_consonant[i].setAdjustViewBounds(true);
//          img_consonant[i].setPadding(0, 10, 5, 0);

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
            img_vowelRight[countRight] = new ImageView(this);
            resourceId = getResources()
                .getIdentifier("vowel" + (vt.ordinal() + 1), "drawable", getPackageName());
            img_vowelRight[countRight].setImageResource(resourceId);
            vowelList.addView(img_vowelRight[countRight]);

//            xPixels = TypedValue
//                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96,
//                    getResources().getDisplayMetrics());
            //            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) xPixels,
//                LayoutParams.WRAP_CONTENT);
//            layoutParams.gravity = Gravity.TOP;
            img_vowelRight[countRight].setLayoutParams(layoutParams);
            img_vowelRight[countRight].setAdjustViewBounds(true);
//            img_vowelRight[countRight].setPadding(0, 10, 10, 0);

            img_vowelRight[countRight].setOnTouchListener(mTouchListener);
            img_vowelRight[countRight].setId(i + 200);
            vowelRightIdList[countRight] = img_vowelRight[countRight].getId();
            img_vowelRight[countRight].setContentDescription(dec_vowels[i].toString());
            countRight++;
          } else if (vt.getSide().equals("B")) {
            img_vowelBottom[countBottom] = new ImageView(this);
            resourceId = getResources()
                .getIdentifier("vowel" + (vt.ordinal() + 1), "drawable", getPackageName());
            img_vowelBottom[countBottom].setImageResource(resourceId);
            vowelList.addView(img_vowelBottom[countBottom]);

            //            xPixels = TypedValue
//                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 77,
//                    getResources().getDisplayMetrics());
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) xPixels,
//                LayoutParams.WRAP_CONTENT);
//            layoutParams.gravity = Gravity.TOP;
            img_vowelBottom[countBottom].setLayoutParams(layoutParams);
            img_vowelBottom[countBottom].setAdjustViewBounds(true);
//            img_vowelBottom[countBottom].setPadding(0, 10, 10, 0);

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

  View.OnLongClickListener mLongClickListener = new View.OnLongClickListener(){

    @Override
    public boolean onLongClick(View v) {

      ClipData clipData = ClipData.newPlainText("", "");
      v.startDrag(clipData, new ImageDrag(v, (int) v.getX(), (int) v.getY()), v, 0);
      v.setVisibility(View.GONE);

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
//              char completeWord = CommonUtils
//                  .characterCombination(currentConsonant, currentVowel, ' ');
//
//              tts.setPitch(1f);
//              tts.setSpeechRate(0.8f);
//              tts.speak(String.valueOf(completeWord), TextToSpeech.QUEUE_FLUSH, null);

              Toast.makeText(SyllableGameActivity.this, "우와 멋진데~", Toast.LENGTH_SHORT).show();

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
