package com.h2kresearch.syllablegame;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

public class IntroActivity extends BGMActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_intro);

    startLoading();
  }

  private void startLoading() {
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {

        // Main
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(intent);
        finish();

      }
    }, 2000);
  }

  @Override
  public void onBackPressed() {
    //super.onBackPressed();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }
}
