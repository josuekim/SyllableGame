package com.h2kresearch.syllablegame;

import android.os.Bundle;
import com.h2kresearch.syllablegame.database.DatabaseAccess;
import com.h2kresearch.syllablegame.model.ConfigurationModel;

public class ResultTotalActivity extends ResultGraphActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_result_total);
//    setContentView(R.layout.activity_result_daily);

    // Open DB
    mDB = DatabaseAccess.getInstance(this);
    mDB.open();

    // Get User Email and Date
    ConfigurationModel conf = ConfigurationModel.getInstance();
    mEmail = conf.getEmail();

    // Total Achieve
    mAchieve = mDB.getTotalAchieve();

    // Total Achieve according to sound
    mAchieveSound = mDB.getTotalAchieveSound();

    // Wrong Answer according to sound
    mWrongSound = mDB.getTotalWrongSound();

    // Daily Exam
    mExam = mDB.getTotalExam();

    // Draw Graph
    DrawGraph();

    // Layout
    mTextView.setText("종합 성취도");
    mTextViewAchieveText.setText("종합 성취도 : ");
    mTextViewAchieve.setText(mAchieve+"%");

  }
}
