package com.h2kresearch.syllablegame;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LessonActivity extends AppCompatActivity {

  // Intent
  Intent mGameIntent;
  Intent mGameIntent2;
  Intent mGameIntent3;

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
    mGameIntent3 = new Intent(LessonActivity.this, SyllableGameActivity3.class);

    // Pre Intent
    Intent preIntent = getIntent();
    String[] select = preIntent.getStringArrayExtra("select");
    mGameIntent.putExtra("select", select);
    mGameIntent2.putExtra("select", select);
    mGameIntent3.putExtra("select", select);

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
            startActivity(mGameIntent3);
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
