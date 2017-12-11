package com.h2kresearch.syllablegame;


import android.app.Dialog;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Space;
import android.widget.TextView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.h2kresearch.syllablegame.database.DatabaseAccess;
import com.h2kresearch.syllablegame.model.SyllableImageView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ResultGraphActivity extends ParentActivity implements OnClickListener {

  // Layout
  TextView mTextView;
  TextView mLeftButton;
  TextView mRightButton;
  TextView mTextViewAchieve;
  TextView mTextViewAchieveText;

  // Database
  DatabaseAccess mDB;

  String mEmail;
  String mDate;

  int mDailyID;
  int mAchieve;
  ArrayList<Map> mAchieveSound; // {Sound, Achieve}
  ArrayList<Map> mWrongSound; // {Sound, {Wrong Sound, Count}}
  ArrayList<Map> mExam; // {Cons, Vowl, Repeat, ConsOK, VowlOK}

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result_graph);

    // Layout
    mTextView = (TextView) findViewById(R.id.textView);
    mLeftButton = (TextView) findViewById(R.id.textViewL);
    mRightButton = (TextView) findViewById(R.id.textViewR);
    mTextViewAchieve = findViewById(R.id.TextViewAcheive);
    mTextViewAchieveText = findViewById(R.id.TextViewAcheiveText);

    mLeftButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        onBackPressed();
      }
    });
//    mRightButton.setOnClickListener(new OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        // 종료하기
//        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//        builder.setTitle("알림");
//        builder.setMessage("앱을 종료하시겠습니까?");
//        builder.setPositiveButton("예",
//            new DialogInterface.OnClickListener() {
//              public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(
//                    getApplicationContext(),//현재제어권자
//                    MusicService.class); // 이동할 컴포넌트
//                stopService(intent); // 서비스 시작
//                ActivityCompat.finishAffinity(ResultGraphActivity.this);
//              }
//            });
//        builder.setNegativeButton("아니오",
//            new DialogInterface.OnClickListener() {
//              public void onClick(DialogInterface dialog, int which) {
////            Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();
//              }
//            });
//        builder.show();
//      }
//    });
  }

  @Override
  public void onClick(View view) {

    // Builder
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    // Layout
    LayoutInflater inflater = getLayoutInflater();
    View layoutView = inflater.inflate(R.layout.layout_result_dialog, null);
    builder.setView(layoutView);

    // Dialog Create
    Dialog dialog = builder.create();

    // Dialog Layout
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    lp.copyFrom(dialog.getWindow().getAttributes());
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
    lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//    lp.width = 800;
//    lp.height = 800;

    // Image
    ImageView imageViewSound = (ImageView) layoutView.findViewById(R.id.imageViewSound);
    imageViewSound.setImageDrawable(((SyllableImageView)view).getDrawable());
    int code = ((SyllableImageView)view).code;

    // Achieve
    float achieve = 0;
    int count = 0;
    for(int i=0; i<mAchieveSound.size(); i++) {
      // Data According to Sound
      Map param = mAchieveSound.get(i);
      int sound = (int) param.get("syllable_code");

      if(sound == code) {
        int correct = (int) param.get("correct_cnt");
        count = (int) param.get("exam_cnt");
        achieve = (float)correct/(float)count;
        break;
      }
    }
    TextView textViewAcheive = (TextView) layoutView.findViewById(R.id.textViewAcheive);
    textViewAcheive.setText(achieve*100+"%");
    TextView textViewNum = (TextView) layoutView.findViewById(R.id.textViewNum);
    textViewNum.setText("현재까지 " + count + "번 공부했습니다.");

    // Wrong Answer
    LinearLayout layout = (LinearLayout) layoutView.findViewById(R.id.LinearLayoutWrong);
    for(int i=0; i<mWrongSound.size(); i++) {
      // Data According to Sound
      Map param = mWrongSound.get(i);
      int sound = (int) param.get("syllable_code");

      if(sound == code) {
        ArrayList<Map> wrongList = (ArrayList) param.get("wrong_list");
        if(wrongList != null) {
          for (int j = 0; j < wrongList.size(); j++) {
            if (j == 3)
              break;
            Map param2 = wrongList.get(j);
            int wrongSound = (int) param2.get("wrong_code");
            int resource = CalResource(wrongSound);

            ImageView imageView = new ImageView(this);
            imageView.setImageResource(resource);
            layout.addView(imageView);
          }
          break;
        }
      }
    }

    // Exam
    LinearLayout layout2 = (LinearLayout) layoutView.findViewById(R.id.LinearLayoutExam);
    for(int i=0; i<mExam.size(); i++) {

      // Data According to Sound
      Map param = mExam.get(i);
      int sound1 = (int) param.get("exam_consonant");
      int sound2 = (int) param.get("exam_vowel");
      int numCons = (int) param.get("exam_consonant_ok");
      int numVowel = (int) param.get("exam_vowel_ok");

      if(sound1 == code || sound2 == code) {

        // Linear Layout
        LinearLayout examLayout = new LinearLayout(this);
        examLayout.setOrientation(LinearLayout.HORIZONTAL);
        layout2.addView(examLayout);
        LinearLayout.LayoutParams examLayoutParams = (LinearLayout.LayoutParams) examLayout.getLayoutParams();
        examLayoutParams.width = LayoutParams.MATCH_PARENT;
        examLayoutParams.height = LayoutParams.WRAP_CONTENT;

        // Exam View
        ImageView examImageView = new ImageView(this);
        int examResource = CalResourceComb(sound1, sound2);
        examImageView.setImageResource(examResource);
        examLayout.addView(examImageView);
        LinearLayout.LayoutParams examImageViewParams = (LinearLayout.LayoutParams) examImageView.getLayoutParams();
        examImageViewParams.width = 0;
        examImageViewParams.height = LayoutParams.WRAP_CONTENT;
        examImageViewParams.weight = 1;
        examImageView.setAdjustViewBounds(true);

        // Consonant
        ImageView consImageView = new ImageView(this);
        int consResource = CalResource(sound1);
        consImageView.setImageResource(consResource);
        examLayout.addView(consImageView);
        LinearLayout.LayoutParams consImageViewParams = (LinearLayout.LayoutParams) consImageView.getLayoutParams();
        consImageViewParams.width = 0;
        consImageViewParams.height = LayoutParams.WRAP_CONTENT;
        consImageViewParams.weight = 1;
        consImageView.setAdjustViewBounds(true);

        // Vowel
        ImageView vowelImageView = new ImageView(this);
        int vowelResource = CalResource(sound2);
        vowelImageView.setImageResource(vowelResource);
        examLayout.addView(vowelImageView);
        LinearLayout.LayoutParams vowelImageViewParams = (LinearLayout.LayoutParams) vowelImageView.getLayoutParams();
        vowelImageViewParams.width = 0;
        vowelImageViewParams.height = LayoutParams.WRAP_CONTENT;
        vowelImageViewParams.weight = 1;
        vowelImageView.setAdjustViewBounds(true);

//        TextView textView = new TextView(this);
//        String exam = "모음은 " + numVowel + "번만에, 자음은 " + numCons + "번만에 맞췄습니다.";
//        textView.setText(exam);
//        layout2.addView(textView);
      }
    }

    // Dialog Show
    dialog.getWindow().setAttributes(lp);
    dialog.show();
//    builder.show();
  }

  void DrawGraph() {
    // Layout
    LinearLayout layout = (LinearLayout)findViewById(R.id.LinearLayoutSound);

    // Space Start
    Space space = new Space(this);
    layout.addView(space);
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) space.getLayoutParams();
    params.width = 0;
    params.weight = 0.5f;

    // mAchieveSound Sort (1. Value, 2. Type)
    Collections.sort(mAchieveSound, SortByAchieve);

    // Graph
    List<BarEntry> entries = new ArrayList<BarEntry>();
    for(int i=0; i<mAchieveSound.size(); i++) {

      // Data According to Sound
      Map param = mAchieveSound.get(i);
      int sound = (int) param.get("syllable_code");
      int correct = (int) param.get("correct_cnt");
      int count = (int) param.get("exam_cnt");
      float achieve = (float)correct/(float)count;
      entries.add(new BarEntry((float)(i+1), achieve));

      // Add Sound Image View
      SyllableImageView imageView = new SyllableImageView(this);
      imageView.code = sound;

      int resource = CalResource(sound);
//      int resource = R.drawable.han24;

      // Set View Param
      imageView.setImageResource(resource);
      imageView.setAdjustViewBounds(true);
      imageView.setOnClickListener(this);
      layout.addView(imageView);
      params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
      params.width = 0;
      params.weight = 1;
      params.setMargins(10, 0, 0, 0);
    }

    // Space End
    Space space2 = new Space(this);
    layout.addView(space2);
    params = (LinearLayout.LayoutParams) space2.getLayoutParams();
    params.width = 0;
    params.weight = 0.5f;

    // Graph View Draw
    BarChart chart = (BarChart) findViewById(R.id.chart);
    BarDataSet dataSet = new BarDataSet(entries, "Label"); // add entries to dataset
    dataSet.setColor(Color.parseColor("#424C58"));
//    dataSet.setValueTextColor(...); // styling, ...

    BarData barData = new BarData(dataSet);
    chart.setData(barData);
//    chart.invalidate(); // refresh

    YAxis yAxis = chart.getAxisLeft();
    yAxis.setAxisMinimum(0);
    yAxis.setAxisMaximum(1);

    YAxis yAxis2 = chart.getAxisRight();
    yAxis2.setDrawGridLines(false);
    yAxis2.setDrawAxisLine(true);
    yAxis2.setDrawLabels(false);

    XAxis xAxis = chart.getXAxis();
    xAxis.setAxisMinimum(0);
    xAxis.setAxisMaximum(10);
    xAxis.setDrawGridLines(false);
    xAxis.setDrawLabels(false);

    Legend legend = chart.getLegend();
    legend.setEnabled(false);
  }

  int CalResource(int code) {

    // Get Resource
    int x = 0;
    int y = 0;

    if(code < 15) {
      x = 0;
      y = code;
    } else {
      x = code - 14;
      y = 0;
    }

    String imageName = "han" + (y * 11 * 2 + x * 2);

    return getResources().getIdentifier(imageName, "drawable", getPackageName());
  }

  int CalResourceComb(int cons, int vowel) {

    // Get Resource
    int x = vowel-14;
    int y = cons;

    String imageName = "han" + (y * 11 * 2 + x * 2);

    return getResources().getIdentifier(imageName, "drawable", getPackageName());
  }

  private final static Comparator<Map> SortByAchieve = new Comparator<Map>() {
    @Override
    public int compare(Map map, Map t1) {

      int correct = (int)map.get("correct_cnt");
      int exam = (int)map.get("exam_cnt");
      float achieve1 = (float)correct/(float)exam;

      correct = (int)t1.get("correct_cnt");
      exam = (int)t1.get("exam_cnt");
      float achieve2 = (float)correct/(float)exam;

      return Float.compare(achieve2, achieve1);
    }
  };
}
