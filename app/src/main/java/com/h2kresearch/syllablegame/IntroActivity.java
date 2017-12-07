package com.h2kresearch.syllablegame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.h2kresearch.syllablegame.utils.MusicService;
import java.io.IOException;

public class IntroActivity extends ParentActivity {

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

        // Gallery Pick
//        Intent i = new Intent(Intent.ACTION_PICK);
//        i.setType(Media.CONTENT_TYPE);
//        startActivityForResult(i, 1);

        // Share
//        Intent i=new Intent(Intent.ACTION_SEND);
//        i.addCategory(Intent.CATEGORY_DEFAULT);
//        i.setType("text/plain");
//        i.putExtra(Intent.EXTRA_SUBJECT, "EXTRA_SUBJECT");
//        i.putExtra(Intent.EXTRA_TEXT, "테스트 스트링");
//        i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"webmaster@website.com"});
//        i.putExtra(Intent.EXTRA_CC, new String[]{"webmaster1@website.com", "webmaster2@website.com"});
//        i.putExtra(Intent.EXTRA_BCC, new String[]{"webmaster@website.com"});
//        startActivity(Intent.createChooser(i, "How do you want to send message?"));

      }
    }, 500);
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
