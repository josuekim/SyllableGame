package com.h2kresearch.syllablegame;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LessonActivity extends AppCompatActivity {

  // Intent
  Intent mGameIntent;
  Intent mGameIntent2;
  Intent mResultIntent;

  // Layout
  TextView mLeftButton;
  TextView mRightButton;

  // Button
  Button mButton1;
  Button mButton2;
  Button mButton3;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lesson);

    // Next Intent
    mGameIntent = new Intent(LessonActivity.this, SyllableGameActivity.class);
    mGameIntent2 = new Intent(LessonActivity.this, SyllableGameActivity2.class);
    mResultIntent = new Intent(LessonActivity.this, ResultActivity.class);

    // Pre Intent
    Intent preIntent = getIntent();
    String[] select = preIntent.getStringArrayExtra("select");
    mGameIntent.putExtra("select", select);
    mGameIntent2.putExtra("select", select);

    // Layout
    mLeftButton = (TextView) findViewById(R.id.textViewL);
    mRightButton = (TextView) findViewById(R.id.textViewR);

    mLeftButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
       onBackPressed();
      }
    });

    mRightButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(mResultIntent);
      }
    });

    // Button
    mButton1 = (Button)findViewById(R.id.button1);
    mButton2 = (Button)findViewById(R.id.button2);
    mButton3 = (Button)findViewById(R.id.button3);

    mButton1.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {

        mButton2.setBackgroundResource(R.drawable.roundcorner);
        mButton3.setBackgroundResource(R.drawable.roundcorner);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
            startActivity(mGameIntent);
          }
        }, 1000);
      }
    });

    mButton2.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {

        mButton1.setBackgroundResource(R.drawable.roundcorner);
        mButton3.setBackgroundResource(R.drawable.roundcorner);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
            startActivity(mGameIntent2);
          }
        }, 1000);
      }
    });

    mButton3.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {

        mButton1.setBackgroundResource(R.drawable.roundcorner);
        mButton2.setBackgroundResource(R.drawable.roundcorner);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
            startActivity(mGameIntent);
          }
        }, 1000);
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();

    mButton1.setBackgroundResource(R.drawable.roundcorner_click);
    mButton2.setBackgroundResource(R.drawable.roundcorner_click);
    mButton3.setBackgroundResource(R.drawable.roundcorner_click);
  }
}
