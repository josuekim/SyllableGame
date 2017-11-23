package com.h2kresearch.syllablegame;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

  // Layout
  TextView mLeftButton;
  TextView mRightButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);

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
        // 종료하기
        ActivityCompat.finishAffinity(ResultActivity.this);
      }
    });
  }
}
