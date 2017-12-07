package com.h2kresearch.syllablegame;

import android.os.Bundle;
import com.h2kresearch.syllablegame.database.DatabaseAccess;
import com.h2kresearch.syllablegame.model.ConfigurationModel;

public class ResultDailyActivity extends ResultGraphActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_result_daily);

    // Open DB
    mDB = DatabaseAccess.getInstance(this);
    mDB.open();

    // Get User Email and Date
    ConfigurationModel conf = ConfigurationModel.getInstance();
    mEmail = conf.getEmail();
//    mDate = conf.getToday();
    mDate = "2017/11/29";

    // Daily ID
    mDailyID = mDB.getDailyID(mEmail, mDate);

    // Daily (Total) Achieve
    mAchieve = mDB.getDailyAchieve(mDailyID);

    // Daily Achieve according to sound
    mAchieveSound = mDB.getDailyAchieveSound(mDailyID);

    // Wrong Answer according to sound
    mWrongSound = mDB.getDailyWrongSound(mDailyID);

    // Daily Exam
    mExam = mDB.getDailyExam(mDailyID);

    // DrawGraph
    DrawGraph();

    // Layout
    mTextView.setText("오늘의 결과");
    mTextViewAchieveText.setText("오늘의 종합 성취도 : ");
    mTextViewAchieve.setText(mAchieve+"%");
  }
}
