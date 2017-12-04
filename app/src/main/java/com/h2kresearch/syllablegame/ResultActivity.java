package com.h2kresearch.syllablegame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.h2kresearch.syllablegame.utils.MusicService;

public class ResultActivity extends ParentActivity
  implements OnClickListener{

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
    mRightButton.setOnClickListener(this);
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
            Intent intent = new Intent(
                getApplicationContext(),//현재제어권자
                MusicService.class); // 이동할 컴포넌트
            stopService(intent); // 서비스 시작
            ActivityCompat.finishAffinity(ResultActivity.this);
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
}
