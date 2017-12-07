package com.h2kresearch.syllablegame;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.h2kresearch.syllablegame.database.DatabaseAccess;
import com.h2kresearch.syllablegame.model.ConfigurationModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultPartActivity extends ParentActivity {

  // Database
  DatabaseAccess mDB;

  String mEmail;
  String mDate;

  ArrayList<Map> mAchieveSoundList; // {Sound, TotalAchieve, {Date, Achieve}}
  ArrayList<Map> mAchieveSoundPartList; // {SoundPart, TotalAchieve, {Date, Achieve}}

  // Layout
  TextView mTextView;
  TextView mLeftButton;
  TextView mRightButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result_part);

    // Open DB
    mDB = DatabaseAccess.getInstance(this);
    mDB.open();

    // Get User Email and Date
    ConfigurationModel conf = ConfigurationModel.getInstance();
    mEmail = conf.getEmail();
//    mDate = conf.getToday();
//    mDate = "2017/11/29";

    // Achieve List according to sound
    mAchieveSoundList = mDB.getAchieveSoundList(mEmail);
    //mAchieveSoundPartList = CalAchieveSoundPartList(mAchieveSoundList);

    // Draw Graph
    DrawGraph();

    // Layout
    mTextView = (TextView) findViewById(R.id.textView);
    mLeftButton = (TextView) findViewById(R.id.textViewL);
    mRightButton = (TextView) findViewById(R.id.textViewR);

    mTextView.setText("단원별 성취도");
    mLeftButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        onBackPressed();
      }
    });
//    mRightButton.setOnClickListener(this);

  }

  void DrawGraph() {

    LinearLayout linearLayoutGraph = (LinearLayout) findViewById(R.id.LinearLayoutGraph);

//    for(int i=0; i<mAchieveSoundList.size(); i++) {
    for(int i=0; i<4; i++) {

      LinearLayout linearLayout = new LinearLayout(this);
      linearLayout.setOrientation(LinearLayout.HORIZONTAL);

      // Sound
//      int sound = (int) param.get("syllable_code");

      // Pie Graph
      PieChart pieChart = new PieChart(this);

      // Data
      List<PieEntry> pieEntries = new ArrayList<PieEntry>();

      // Total Achieve
      Map param = mAchieveSoundList.get(i);
      int totalCorrect = (int) param.get("correct_cnt");
      int totalCount = (int) param.get("exam_cnt");
      float totalAchieve = (float)totalCorrect/(float)totalCount;
      pieEntries.add(new PieEntry(totalAchieve));
      pieEntries.add(new PieEntry(1-totalAchieve));

      // Pie Graph Draw
      PieDataSet pieDataSet = new PieDataSet(pieEntries, "Label"); // add entries to dataset
      pieDataSet.setColors(new int[] {Color.RED,Color.BLUE});

      PieData pieData = new PieData(pieDataSet);
      pieChart.setData(pieData);

      Legend pieLegend = pieChart.getLegend();
      pieLegend.setEnabled(false);

      // Line Graph
      LineChart lineChart = new LineChart(this);

      // Data
      List<Entry> lineEntries = new ArrayList<Entry>();

      // Daily Achieve
      ArrayList<Map> achieveList = (ArrayList) param.get("achieve_list");

      for(int j=0; j<achieveList.size(); j++) {

        // Daily
        Map param2 = achieveList.get(j);

        String date = (String) param2.get("learning_date");
        int correct = (int) param2.get("correct_cnt");
        int count = (int) param2.get("exam_cnt");
        float achieve = (float)correct/(float)count;

        lineEntries.add(new Entry(j+1, achieve));
      }

      // Line Graph Draw
      LineDataSet lineDataSet = new LineDataSet(lineEntries, "Label");

      LineData lineData = new LineData(lineDataSet);
      lineChart.setData(lineData);

      YAxis yAxis = lineChart.getAxisLeft();
      yAxis.setAxisMinimum(0);
      yAxis.setAxisMaximum(1);

      YAxis yAxis2 = lineChart.getAxisRight();
      yAxis2.setDrawGridLines(false);
      yAxis2.setDrawAxisLine(true);
      yAxis2.setDrawLabels(false);

      XAxis xAxis = lineChart.getXAxis();
      xAxis.setAxisMinimum(0);
      xAxis.setAxisMaximum(10);
      xAxis.setDrawGridLines(false);
      xAxis.setDrawLabels(false);

      Legend legend = lineChart.getLegend();
      legend.setEnabled(false);

      // Add View
      linearLayout.addView(pieChart);
      linearLayout.addView(lineChart);

      LinearLayout.LayoutParams pieParams = (LinearLayout.LayoutParams)pieChart.getLayoutParams();
      LinearLayout.LayoutParams lineParams = (LinearLayout.LayoutParams)lineChart.getLayoutParams();

      pieParams.width = 0;
      pieParams.height = LayoutParams.MATCH_PARENT;
      pieParams.weight = 1;

      lineParams.width = 0;
      lineParams.height = LayoutParams.MATCH_PARENT;
      lineParams.weight = 2;

      linearLayoutGraph.addView(linearLayout);
      LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)linearLayout.getLayoutParams();
      layoutParams.width = LayoutParams.MATCH_PARENT;
      layoutParams.height = 500;
      layoutParams.weight = 1;
    }
  }

  ArrayList<Map> CalAchieveSoundPartList(ArrayList<Map> list) {
    ArrayList<Map> partList = new ArrayList<Map>();

    Map<String, Object> part1 = new HashMap<String, Object>();
    Map<String, Object> part2 = new HashMap<String, Object>();
    Map<String, Object> part3 = new HashMap<String, Object>();

    part1.put("syllable_code", 1.0f);
    part1.put("achieve", 0.0f);
    part1.put("number", 0.0f);
    part2.put("syllable_code", 2.0f);
    part2.put("achieve", 0.0f);
    part2.put("number", 0.0f);
    part3.put("syllable_code", 3.0f);
    part3.put("achieve", 0.0f);
    part3.put("number", 0.0f);

    partList.add(part1);
    partList.add(part2);
    partList.add(part3);

    for(int i=0; i<list.size(); i++) {

      int sound = (int) list.get(i).get("syllable_code");
      int correct = (int) list.get(i).get("correct_cnt");
      int count = (int) list.get(i).get("exam_cnt");
      float soundAchieve = (float)correct/(float)count;

      if(sound<15) { // Part 1
        float number = (float)part1.get("number");
        float achieve = (float)part1.get("achieve");
        part1.put("achieve", (achieve*number + soundAchieve) / (number + 1.0f));
        part1.put("number", number + 1.0f);

        ArrayList<Map> achieveList = (ArrayList) part1.get("achieve_list");

      } else {
        if(sound%2 != 0) {// Part2

        } else { // Part 3

        }
      }

    }

    return partList;
  }
}
