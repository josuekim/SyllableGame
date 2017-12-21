package com.h2kresearch.syllablegame;

import android.content.ContentValues;
import android.os.Bundle;
import com.h2kresearch.syllablegame.database.DatabaseAccess;
import com.h2kresearch.syllablegame.model.ConfigurationModel;
import java.util.ArrayList;
import java.util.Map;

public class ResultDailyActivity extends ResultGraphActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_result_daily);

    // Open DB
    mDB = DatabaseAccess.getInstance(this);
    mDB.open();

    // Nick Name
    ConfigurationModel conf = ConfigurationModel.getInstance();
    mNickName = conf.getNickName();
    mToday = conf.getToday();

    // Daily (Total) Achieve
    mAchieve = mDB.updateDailyAverage();

    // Daily Achieve according to sound
    mAchieveSound = mDB.getDailyAchieveSound();

    // Wrong Answer according to sound
    mWrongSound = mDB.getDailyWrongSound();

    // Daily Exam
    mExam = mDB.getDailyExam();

    // DrawGraph
    DrawGraph();

    // Layout
    mTextView.setText("오늘의 결과");
    mTextViewAchieveText.setText(mNickName + " 학생의 " + mToday + " 종합 성취도는 ");
    mTextViewAchieve.setText(mAchieve+"%");
  }
}
