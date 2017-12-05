package com.h2kresearch.syllablegame;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import com.h2kresearch.syllablegame.database.DatabaseAccess;
import com.h2kresearch.syllablegame.model.ConfigurationModel;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import java.util.ArrayList;
import java.util.Map;

public class ResultDailyActivity extends ParentActivity implements OnClickListener{

  // Database
  DatabaseAccess mDB;

  String mEmail;
  String mDate;

  int mDailyID;
  int mAchieve;
  ArrayList<Map> mAchieveSound; // {Sound, Achieve}
  ArrayList mWrongSound; // {Sound, {Wrong Sound, Count}}
  ArrayList mDailyExam; // {Cons, Vowl, Repeat, ConsOK, VowlOK}

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
//    mDate = conf.getToday();
    mDate = "2017/11/29";

    // Daily ID
    mDailyID = mDB.getDailyID(mEmail, mDate);

    // Daily (Total) Achieve
    mAchieve = mDB.getDailyAchieve(mDailyID);

    // Daily Achieve according to sound
    mAchieveSound = mDB.getDailyAchieveSound(mDailyID);

    // Wrong Answer according to sound
    mWrongSound = mDB.getWrongSound(mDailyID);

    // Daily Exam
    mDailyExam = mDB.getDailyExamSound(mDailyID);

    LinearLayout layout = (LinearLayout)findViewById(R.id.LinearLayoutSound);
    Space space = new Space(this);
    layout.addView(space);
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) space.getLayoutParams();
    params.width = 0;
    params.weight = 0.5f;

    DataPoint[] dataPoint = new DataPoint[mAchieveSound.size()];
    for(int i=0; i<mAchieveSound.size(); i++) {
      Map param = mAchieveSound.get(i);
      int correct = (int) param.get("correct_cnt");
      int count = (int) param.get("exam_cnt");
      double achieve = (double)correct/(double)count;
      dataPoint[i] = new DataPoint((double)i, achieve);

      // Add View
      ImageView imageView = new ImageView(this);
      imageView.setImageResource(R.drawable.han24);
      imageView.setAdjustViewBounds(true);
      imageView.setOnClickListener(this);
      layout.addView(imageView);
      params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
      params.width = 0;
      params.weight = 1;
      params.setMargins(10, 0, 0, 0);
    }

    Space space2 = new Space(this);
    layout.addView(space2);
    params = (LinearLayout.LayoutParams) space2.getLayoutParams();
    params.width = 0;
    params.weight = 0.5f;
//    int height = layout.getHeight();
//    RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) layout.getLayoutParams();
////    params1.setMargins(0,0,0, -100);

    GraphView graph = (GraphView) findViewById(R.id.graph);
    BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoint);
    graph.addSeries(series);

    graph.getViewport().setMinX(-1);
    graph.getViewport().setMaxX(mAchieveSound.size());
    graph.getViewport().setMinY(0);
    graph.getViewport().setMaxY(1);
    graph.getViewport().setXAxisBoundsManual(true);
    graph.getViewport().setYAxisBoundsManual(true);
    graph.getGridLabelRenderer().setGridColor(Color.rgb(50,50,50));
    graph.getGridLabelRenderer().getGridStyle().drawHorizontal();
    graph.getGridLabelRenderer().getGridStyle().drawVertical();
    series.setColor(Color.rgb(150,150,150));
    series.setSpacing(10);

//    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
//    staticLabelsFormatter.setHorizontalLabels(new String[] {"", "ㄱ", "ㄴ", "ㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅅ", "ㅇ", "ㅈ", ""});
//    staticLabelsFormatter.setVerticalLabels(new String[] {"low", "middle", "high"});
//    graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
    graph.getGridLabelRenderer().setNumHorizontalLabels(11);
//    graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.rgb(50,50,50));
graph.getGridLabelRenderer().setPadding(0);




//    NumberFormat nf = NumberFormat.getInstance();
//    nf.setMinimumFractionDigits(3);
//    nf.setMinimumIntegerDigits(2);
//
//    graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));
  }

  @Override
  public void onClick(View view) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    LayoutInflater inflater = getLayoutInflater();
    View layoutView = inflater.inflate(R.layout.layout_result_dialog, null);
    builder.setView(layoutView);
    Dialog dialog = builder.create();

    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    lp.copyFrom(dialog.getWindow().getAttributes());
//    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
    lp.width = 800;
    lp.height = 800;

    dialog.show();
    Window window = dialog.getWindow();
    window.setAttributes(lp);
//    builder.show();
  }
}
