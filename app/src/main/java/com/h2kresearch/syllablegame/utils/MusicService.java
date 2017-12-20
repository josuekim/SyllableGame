package com.h2kresearch.syllablegame.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.h2kresearch.syllablegame.R;

public class MusicService extends Service {

  public MediaPlayer mMP;
  int mPosition = 0;
  boolean mPause = false;
  float mVolume = 1.0f;

  public class MusicServiceBinder extends Binder {

    public MusicService getService() {
      return MusicService.this; //현재 서비스를 반환.
    }
  }

  private final IBinder mBinder = new MusicServiceBinder();

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    start();
    return mBinder;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    // Media Player
    mMP = MediaPlayer.create(this, R.raw.bg);
    mMP.setLooping(true); // 반복 재생
    mMP.setVolume(mVolume, mVolume);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);

    return START_NOT_STICKY;
  }

  public void start() {

    mPause = false;

    if (!mMP.isPlaying()) {
      // Play
      mMP.seekTo(mPosition);
      mMP.start();

      Thread fadeIn = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            while (!mPause) {
              mMP.setVolume(mVolume, mVolume);
              if (mVolume >= 1.0f) {
                break;
              } else {
                mVolume = mVolume + 0.1f;
              }
              Thread.sleep(100);
            }
          } catch (Exception e) {
          }
        }
      });
      fadeIn.start();
    }
  }

  public void pause() {

    mPause = true;

    if (mMP.isPlaying()) {

      Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {

          if (mPause) {
            Thread fadeout = new Thread(new Runnable() {
              @Override
              public void run() {
                try {
                  while (mPause) {
                    mMP.setVolume(mVolume, mVolume);
                    if (mVolume <= 0.0f) {
                      break;
                    } else {
                      mVolume = mVolume - 0.1f;
                    }
                    Thread.sleep(100);
                  }
                } catch (Exception e) {
                }
                // Pause
                try{
                  mPosition = mMP.getCurrentPosition();
                  mMP.pause();
                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
            });
            fadeout.start();
          }
        }
      }, 1000);

    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    // Stop
    mPause = false;
    mMP.stop();
    mMP.release();
    mMP = null;
  }

  //콜백 인터페이스 선언
  public interface ICallback {

    public void recvData(); //액티비티에서 선언한 콜백 함수.
  }

  private ICallback mCallback;

  //액티비티에서 콜백 함수를 등록하기 위함.
  public void registerCallback(ICallback cb) {
    mCallback = cb;
  }

  public static void MediaPlay(Context context, int resourceID) {
    MediaPlayer mp = MediaPlayer.create(context, resourceID);
    mp.setLooping(false);
    mp.setVolume(1.0f, 1.0f);
    mp.start();
    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      public void onCompletion(MediaPlayer mp) {
        try {
          mp.release();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
}
