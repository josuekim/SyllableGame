package com.h2kresearch.syllablegame;


import android.app.Dialog;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.h2kresearch.syllablegame.SyllableGameActivity.ImageDrag;
import com.h2kresearch.syllablegame.database.DatabaseAccess;
import com.h2kresearch.syllablegame.model.SyllableImageView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ResultGraphActivity extends BGMActivity implements OnClickListener {

  // Layout
  TextView mTextView;
  TextView mLeftButton;
  TextView mRightButton;
  TextView mTextViewAchieve;
  TextView mTextViewAchieveText;

  // Database
  DatabaseAccess mDB;

  String mEmail;

  int mAchieve;
  ArrayList<Map> mAchieveSound; // {Sound, Achieve}
  ArrayList<Map> mWrongSound; // {Sound, {Wrong Sound, Count}}
  ArrayList<Map> mExam; // {Cons, Vowl, Repeat, ConsOK, VowlOK, {Response}}

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
    int correct = 0;
    int count = 0;
    for(int i=0; i<mAchieveSound.size(); i++) {
      // Data According to Sound
      Map param = mAchieveSound.get(i);
      int sound = (int) param.get("syllable_code");

      if(sound == code) {
        correct = (int) param.get("correct_cnt");
        count = (int) param.get("exam_cnt");
        achieve = (float)correct/(float)count;
        break;
      }
    }
    TextView textViewAcheive = (TextView) layoutView.findViewById(R.id.textViewAcheive);
    textViewAcheive.setText(achieve*100+"%");
    TextView textViewNum = (TextView) layoutView.findViewById(R.id.textViewNum);
    textViewNum.setText(correct + "");
    TextView textViewNumTotal = (TextView) layoutView.findViewById(R.id.textViewNumTotal);
    textViewNumTotal.setText(count + "");

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
            int resource = CalResourceGray(wrongSound);

            ImageView imageView = new ImageView(this);
            imageView.setImageResource(resource);
            layout.addView(imageView);

            LinearLayout.LayoutParams wrongImageViewParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            wrongImageViewParams.width = LayoutParams.WRAP_CONTENT;
            wrongImageViewParams.height = LayoutParams.MATCH_PARENT;
            wrongImageViewParams.setMargins(5,5,5,5);
//            wrongImageViewParams.weight = 1;
            imageView.setAdjustViewBounds(true);
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
      ArrayList<Integer> responseList = (ArrayList) param.get("response_list");

      // Garbage Exam Deletion
      if(numCons == 0 || numVowel == 0) continue;

      if(sound1 == code || sound2 == code) {

        // Linear Layout
        LinearLayout examLayout = new LinearLayout(this);
        examLayout.setOrientation(LinearLayout.HORIZONTAL);
        layout2.addView(examLayout);
        LinearLayout.LayoutParams examLayoutParams = (LinearLayout.LayoutParams) examLayout.getLayoutParams();
        examLayoutParams.width = LayoutParams.MATCH_PARENT;
        examLayoutParams.height = 100;

        // Exam View
        ImageView examImageView = new ImageView(this);
        int examResource = CalResourceCombGray(sound1, sound2);
        examImageView.setImageResource(examResource);
        examLayout.addView(examImageView);
        LinearLayout.LayoutParams examImageViewParams = (LinearLayout.LayoutParams) examImageView.getLayoutParams();
        examImageViewParams.width = LayoutParams.WRAP_CONTENT;
        examImageViewParams.height = LayoutParams.MATCH_PARENT;
        examImageViewParams.setMargins(5,5,5,5);
//        examImageViewParams.weight = 1;
        examImageView.setAdjustViewBounds(true);

        // Text View
        TextView colonView = new TextView(this);
        colonView.setText(" : ");
        colonView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
        colonView.setTextColor(getResources().getColor(R.color.textColor));
        colonView.setGravity(Gravity.CENTER_VERTICAL);
        examLayout.addView(colonView);

        LinearLayout.LayoutParams colonViewParams = (LinearLayout.LayoutParams) colonView.getLayoutParams();
        colonViewParams.width = LayoutParams.WRAP_CONTENT;
        colonViewParams.height = LayoutParams.MATCH_PARENT;
        colonViewParams.setMargins(5,5,5,5);

        for(int j=0; j<responseList.size(); j++) {
          int responseCode = responseList.get(j);

          ImageView consImageView = new ImageView(this);

          int consResource = CalResourceGray(responseCode);
          if(sound1 == responseCode || sound2 == responseCode) {
            consResource = CalResourceRed(responseCode);
          }

          consImageView.setImageResource(consResource);
          examLayout.addView(consImageView);
          LinearLayout.LayoutParams consImageViewParams = (LinearLayout.LayoutParams) consImageView.getLayoutParams();
          consImageViewParams.width = LayoutParams.WRAP_CONTENT;
          consImageViewParams.height = LayoutParams.MATCH_PARENT;
          consImageViewParams.setMargins(5,5,5,5);
//        consImageViewParams.weight = 1;
          consImageView.setAdjustViewBounds(true);
        }

//        // Consonant
//        ImageView consImageView = new ImageView(this);
//        int consResource = CalResourceGray(sound1);
//        consImageView.setImageResource(consResource);
//        examLayout.addView(consImageView);
//        LinearLayout.LayoutParams consImageViewParams = (LinearLayout.LayoutParams) consImageView.getLayoutParams();
//        consImageViewParams.width = LayoutParams.WRAP_CONTENT;
//        consImageViewParams.height = LayoutParams.MATCH_PARENT;
//        consImageViewParams.setMargins(5,5,5,5);
////        consImageViewParams.weight = 1;
//        consImageView.setAdjustViewBounds(true);
//
//        // Vowel
//        ImageView vowelImageView = new ImageView(this);
//        int vowelResource = CalResourceGray(sound2);
//        vowelImageView.setImageResource(vowelResource);
//        examLayout.addView(vowelImageView);
//        LinearLayout.LayoutParams vowelImageViewParams = (LinearLayout.LayoutParams) vowelImageView.getLayoutParams();
//        vowelImageViewParams.width = LayoutParams.WRAP_CONTENT;
//        vowelImageViewParams.height = LayoutParams.MATCH_PARENT;
//        vowelImageViewParams.setMargins(5,5,5,5);
////        vowelImageViewParams.weight = 1;
//        vowelImageView.setAdjustViewBounds(true);

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
    params.setMargins(5, 0, 0, 0);

    // mAchieveSound Sort (1. Value, 2. Type)
    Collections.sort(mAchieveSound, SortByAchieve);

    // Graph
    int NumEntry = mAchieveSound.size();
    List<BarEntry> entries = new ArrayList<BarEntry>();
    for(int i=0; i<NumEntry; i++) {

      // Data According to Sound
      Map param = mAchieveSound.get(i);
      int sound = (int) param.get("syllable_code");
      int correct = (int) param.get("correct_cnt");
      int count = (int) param.get("exam_cnt");

      // Add Sound Image View
      SyllableImageView imageView = new SyllableImageView(this);
      imageView.code = sound;

      float achieve = 0.0f;
      int resource = CalResourceGray(sound);
      if(count != 0) {
        achieve = (float) correct / (float) count * 100.0f;
        resource = CalResourceRed(sound);
        imageView.setOnClickListener(this);
      }
      entries.add(new BarEntry((float)(i+1), achieve));

      // Set View Param
      imageView.setImageResource(resource);
      imageView.setAdjustViewBounds(true);
      layout.addView(imageView);
      params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
      params.width = 0;
      params.weight = 1;
      params.setMargins(5, 0, 0, 0);
    }

    for(int i=NumEntry; i<24; i++) {
      Space spaceEntry = new Space(this);
      layout.addView(spaceEntry);
      params = (LinearLayout.LayoutParams) spaceEntry.getLayoutParams();
      params.width = 0;
      params.weight = 1.0f;
      params.setMargins(5, 0, 0, 0);
    }

    // Space End
    Space space2 = new Space(this);
    layout.addView(space2);
    params = (LinearLayout.LayoutParams) space2.getLayoutParams();
    params.width = 0;
    params.weight = 0.5f;
    params.setMargins(5, 0, 0, 0);

    // Graph View Draw
    BarChart chart = (BarChart) findViewById(R.id.chart);
    BarDataSet dataSet = new BarDataSet(entries, "Label"); // add entries to dataset
    dataSet.setColor(getResources().getColor(R.color.titleBarColor));
    dataSet.setHighLightColor(getResources().getColor(R.color.highlightColor));
    dataSet.setHighLightAlpha(255);
//    dataSet.setValueTextColor(...); // styling, ...

    BarData barData = new BarData(dataSet);
    chart.setData(barData);
    chart.setScaleEnabled(false);
//    chart.invalidate(); // refresh

    YAxis yAxis = chart.getAxisLeft();
    yAxis.setAxisMinimum(0);
    yAxis.setAxisMaximum(100);

    YAxis yAxis2 = chart.getAxisRight();
    yAxis2.setDrawGridLines(false);
    yAxis2.setDrawAxisLine(true);
    yAxis2.setDrawLabels(false);

    XAxis xAxis = chart.getXAxis();
    xAxis.setAxisMinimum(0);
    xAxis.setAxisMaximum(24+1);
    xAxis.setDrawGridLines(false);
    xAxis.setDrawLabels(false);

    Legend legend = chart.getLegend();
    legend.setEnabled(false);

    Description description = chart.getDescription();
    description.setEnabled(false);
  }

  int CalResourceRed(int code) {

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

    String imageName = "han" + (y * 11 * 2 + x * 2 + 1);

    return getResources().getIdentifier(imageName, "drawable", getPackageName());
  }

  int CalResourceGray(int code) {

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

  int CalResourceCombRed(int cons, int vowel) {

    // Get Resource
    int x = vowel-14;
    int y = cons;

    String imageName = "han" + (y * 11 * 2 + x * 2 + 1);

    return getResources().getIdentifier(imageName, "drawable", getPackageName());
  }

  int CalResourceCombGray(int cons, int vowel) {

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
      float achieve1 = 0.0f;
      if(exam != 0 ) {
        achieve1 = (float)correct/(float)exam;
      }

      correct = (int)t1.get("correct_cnt");
      exam = (int)t1.get("exam_cnt");
      float achieve2 = 0.0f;
      if(exam != 0 ) {
        achieve2 = (float)correct/(float)exam;
      }

      return Float.compare(achieve2, achieve1);
    }
  };
}
