package com.h2kresearch.syllablegame.utils;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.h2kresearch.syllablegame.R;

/**
 * Created by ishsrain on 2017. 11. 30..
 */

public class MusicService extends Service {

  public MediaPlayer mMP;

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    // 서비스에서 가장 먼저 호출됨(최초에 한번만)
    Log.d("test", "서비스의 onCreate");
    mMP = MediaPlayer.create(this, R.raw.bg);
    mMP.setLooping(false); // 반복재생
  }
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    // 서비스가 호출될 때마다 실행
    Log.d("test", "서비스의 onStartCommand");
    mMP.start(); // 노래 시작
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    // 서비스가 종료될 때 실행
    mMP.stop(); // 음악 종료
    Log.d("test", "서비스의 onDestroy");
  }
}
