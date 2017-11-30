package com.h2kresearch.syllablegame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.h2kresearch.syllablegame.database.DatabaseAccess;
import com.h2kresearch.syllablegame.model.ConfigurationModel;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.util.ArrayList;

public class ResultDailyActivity extends AppCompatActivity {

  // Database
  DatabaseAccess mDB;

  String mEmail;
  String mDate;

  int mDailyID;
  int mAchieve;
  ArrayList mAchieveSound; // {Sound, Achieve}
  ArrayList mWrongSound; // {Sound, {Wrong Sound, Count}}

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result_daily);

    // Open DB
    mDB = DatabaseAccess.getInstance(this);
    mDB.open();

    // Get User Email and Date
    ConfigurationModel conf = ConfigurationModel.getInstance();
    mEmail = conf.getEmail();
//    mDate = conf.getDate(); // TBA

    // Daily ID
    mDailyID = mDB.getDailyID(mEmail, mDate);

    // Daily Achieve
    mAchieve = mDB.getDailyAchieve(mDailyID);

    // Daily Achieve according to sound
    mAchieveSound = mDB.getDailyAchieveSound(mDailyID);

    // Wrong Answer according to sound
//    mWrongSound = get

    // Daily Exam

    GraphView graph = (GraphView) findViewById(R.id.graph);
    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
        new DataPoint(0, 1),
        new DataPoint(1, 5),
        new DataPoint(2, 3),
        new DataPoint(3, 2),
        new DataPoint(4, 6)
    });
    graph.addSeries(series);
  }
}
