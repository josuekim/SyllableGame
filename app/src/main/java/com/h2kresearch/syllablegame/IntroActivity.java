package com.h2kresearch.syllablegame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.h2kresearch.syllablegame.utils.MusicService;
import java.io.IOException;

public class IntroActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_intro);

    startLoading();
    startMusic();
  }

  private void startLoading() {
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(intent);
        finish();
      }
    }, 500);
  }

  private void startMusic() {
    Intent intent = new Intent(
        getApplicationContext(),//현재제어권자
        MusicService.class); // 이동할 컴포넌트
    startService(intent); // 서비스 시작
  }

  @Override
  public void onBackPressed() {
    //super.onBackPressed();
  }
}
