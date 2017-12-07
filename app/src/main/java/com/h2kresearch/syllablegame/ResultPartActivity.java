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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.h2kresearch.syllablegame.database.DatabaseAccess;
import com.h2kresearch.syllablegame.model.ConfigurationModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultPartActivity extends ParentActivity {

  // Database
  DatabaseAccess mDB;

  String mEmail;
  String mDate;

  ArrayList<Map> mAchieveSound; // {Sound, Achieve}
  ArrayList<Map> mAchieveSoundList; // {Sound, TotalAchieve, {Data, Achieve}}

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

    // Total Achieve according to sound
    mAchieveSound = mDB.getTotalAchieveSound(mEmail);

    // Achieve List according to sound
    mAchieveSoundList = mDB.getAchieveSoundList(mEmail);

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

      Legend lineLegend = lineChart.getLegend();
      lineLegend.setEnabled(false);

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
}
