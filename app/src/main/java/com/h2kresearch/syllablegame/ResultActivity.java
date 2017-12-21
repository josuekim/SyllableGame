package com.h2kresearch.syllablegame;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.h2kresearch.syllablegame.database.DatabaseAccess;
import com.h2kresearch.syllablegame.model.ConfigurationModel;
import com.h2kresearch.syllablegame.utils.MusicService;

public class ResultActivity extends BGMActivity
    implements OnClickListener {

  // Intent
  Intent mIntent;
//  Intent mIntent2;
  Intent mIntent3;
  Intent mLoginIntent;

  // Layout
  TextView mLeftButton;
  TextView mRightButton;

  // Button
  Button mButton1;
  //  Button mButton2;
  Button mButton3;

  // Bottom Menu
  TextView mRecommendButton;
  TextView mServiceCenterButton;
  TextView mLogoutButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);

    // Next Intent
    mIntent = new Intent(ResultActivity.this, ResultDailyActivity.class);
    mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//    mIntent2 = new Intent(ResultActivity.this, ResultPartActivity.class);
//    mIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    mIntent3 = new Intent(ResultActivity.this, ResultTotalActivity.class);
    mIntent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    mLoginIntent = new Intent(ResultActivity.this, LoginActivity.class);
    mLoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

    // Layout
    mLeftButton = (TextView) findViewById(R.id.textViewL);
    mRightButton = (TextView) findViewById(R.id.textViewR);

    mLeftButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        onBackPressed();
      }
    });
    mRightButton.setOnClickListener(this);

    DatabaseAccess db = DatabaseAccess.getInstance(this);
    db.updateTotalStat();
    db.updateTotalWrong();

    // Button
    mButton1 = (Button) findViewById(R.id.button1);
//    mButton2 = (Button)findViewById(R.id.button2);
    mButton3 = (Button) findViewById(R.id.button3);

    mButton1.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {

//        mButton2.setBackgroundResource(R.drawable.roundcorner);
        mButton3.setBackgroundResource(R.drawable.roundcorner);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
            startActivity(mIntent);
          }
        }, 500);
      }
    });

//    mButton2.setOnClickListener(new OnClickListener() {
//      @Override
//      public void onClick(View view) {
//
////        mButton1.setBackgroundResource(R.drawable.roundcorner);
////        mButton3.setBackgroundResource(R.drawable.roundcorner);
//
//        Toast.makeText(getApplicationContext(), "추후 업데이트 될 예정입니다.", Toast.LENGTH_LONG).show();
////        Handler handler = new Handler();
////        handler.postDelayed(new Runnable() {
////          @Override
////          public void run() {
////            startActivity(mIntent2);
////          }
////        }, 500);
//      }
//    });

    mButton3.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {

        mButton1.setBackgroundResource(R.drawable.roundcorner);
//        mButton2.setBackgroundResource(R.drawable.roundcorner);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
            startActivity(mIntent3);
          }
        }, 500);
      }
    });

    mRecommendButton = (TextView) findViewById(R.id.textView1);
    mServiceCenterButton = (TextView) findViewById(R.id.textView2);
    mLogoutButton = (TextView) findViewById(R.id.textView3);

    mRecommendButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // Share
        Intent i=new Intent(Intent.ACTION_SEND);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setType("text/plain");
//        i.putExtra(Intent.EXTRA_SUBJECT, "title");
        i.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_recommend));
//        i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"webmaster@website.com"});
//        i.putExtra(Intent.EXTRA_CC, new String[]{"webmaster1@website.com", "webmaster2@website.com"});
//        i.putExtra(Intent.EXTRA_BCC, new String[]{"webmaster@website.com"});
        startActivity(Intent.createChooser(i, getString(R.string.app_recommend_title)));

        // Gallery Pick
//        Intent i = new Intent(Intent.ACTION_PICK);
//        i.setType(Media.CONTENT_TYPE);
//        startActivityForResult(i, 1);
      }
    });

    mServiceCenterButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_plus_home)));
        startActivity(intent);
      }
    });

    mLogoutButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // DB Update
        DatabaseAccess mDb = DatabaseAccess.getInstance(getApplicationContext());
        mDb.open();

        ConfigurationModel mConf = ConfigurationModel.getInstance();
        mDb.logout(mConf.getEmail());

        // Logout
        startActivity(mLoginIntent);
      }
    });
  }

  @Override
  public void onClick(View view) {
    // 종료하기
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("알림");
    builder.setMessage("앱을 종료하시겠습니까?");
    builder.setPositiveButton("예",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
              @Override
              public void run() {

                Intent intent = new Intent(
                    getApplicationContext(),//현재제어권자
                    MusicService.class); // 이동할 컴포넌트
                stopService(intent); // 서비스 시작
                ActivityCompat.finishAffinity(ResultActivity.this);

              }
            }, 1000);
          }
        });
    builder.setNegativeButton("아니오",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
//            Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();
          }
        });
    builder.show();
  }

  @Override
  protected void onResume() {
    super.onResume();

    mButton1.setBackgroundResource(R.drawable.roundcorner_click);
//    mButton2.setBackgroundResource(R.drawable.roundcorner_click);
    mButton3.setBackgroundResource(R.drawable.roundcorner_click);
  }
}
