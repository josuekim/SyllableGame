package com.h2kresearch.syllablegame;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.h2kresearch.syllablegame.utils.MusicService;

public class ParentActivity extends AppCompatActivity {

  MusicService mService;
  Intent mIntent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_parent);

    mIntent = new Intent(
        getApplicationContext(), // 현재 제어권자
        MusicService.class); // 이동할 컴포넌트

    bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
    startService(mIntent); // 서비스 시작
  }

  private ServiceConnection mConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      MusicService.MusicServiceBinder binder = (MusicService.MusicServiceBinder) iBinder;
      mService = binder.getService();
      mService.registerCallback(mCallback);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
      mService = null;
    }
  };

  private MusicService.ICallback mCallback = new MusicService.ICallback() {
    public void recvData() {

    }
  };

  @Override
  protected void onPause() {
    super.onPause();

    if(mService != null) {
      mService.pause();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    if(mService != null) {
      mService.start();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    unbindService(mConnection);
  }
}
