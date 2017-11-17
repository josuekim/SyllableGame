package com.h2kresearch.syllablegame;

import android.content.Intent;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.h2kresearch.syllablegame.TableImageView.SelectViewListener;
import com.h2kresearch.syllablegame.com.h2kresearch.syllablegame.utils.CommonUtils;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
    implements SelectViewListener {

  // Select List
  ArrayList<TableImageView> mSelect = new ArrayList<TableImageView>();

  // Layout
  LinearLayout mLinearLayout;
  TextView mTextView;
  TextView mLeftButton;
  TextView mRightButton;

  // Mode Change
  boolean mSelectMode = false;

  // TTS
  TextToSpeech mTTS;

  // Intent
  Intent mIntent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // TTS
    mTTS = new TextToSpeech(this, new OnInitListener() {
      @Override
      public void onInit(int i) {
        mTTS.setPitch(1.0f);
        mTTS.setSpeechRate(0.8f);
      }
    });

    // LinearLayout (Background)
    mLinearLayout = (LinearLayout) findViewById(R.id.LinearLayout);

    // TextView
    mTextView = (TextView) findViewById(R.id.textView);
    mLeftButton = (TextView) findViewById(R.id.textViewL);
    mRightButton = (TextView) findViewById(R.id.textViewR);

    mRightButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mSelectMode) {
          // 완료
          mIntent = new Intent(MainActivity.this, ResizeActivity.class);

          // Selection
          String[] str = new String[mSelect.size()];
          for (int i = 0; i < mSelect.size(); i++) {
            str[i] = mSelect.get(i).mStr;
          }
          mIntent.putExtra("select", str);
//          mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
          startActivity(mIntent);
        } else {
          // 선택
          mTextView.setText("음절 선택");
          mLinearLayout.setBackgroundColor(Color.parseColor("#FCE4EC"));
          mSelectMode = true;
          mRightButton.setText("완료");
          mLeftButton.setText("취소");
        }
      }
    });

    mLeftButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mSelectMode) {
          // 취소
          cancelAllView();

          mTextView.setText("똑똑한 음절표");
          mLinearLayout.setBackgroundColor(Color.parseColor("#E0F2F1"));
          mSelectMode = false;
          mRightButton.setText("선택");
          mLeftButton.setText("이전");
        } else {
          // 이전
        }
      }
    });

    // Table
    TableLayout tableLayout = (TableLayout) findViewById(R.id.TableLayout);

    char[] chRow = {' ', 'ㄱ', 'ㄴ', 'ㄷ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅅ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};
    char[] chCol = {' ', 'ㅏ', 'ㅑ', 'ㅓ', 'ㅕ', 'ㅗ', 'ㅛ', 'ㅜ', 'ㅠ', 'ㅡ', 'ㅣ'};
    int row = chRow.length;
    int col = chCol.length;

    for (int j = 0; j < row; j++) {

      // New Row
      TableRow tableRow = new TableRow(this);

      for (int i = 0; i < col; i++) {

        // New ImageView
        TableImageView imageView = new TableImageView(this);

        // Find ImageID
        String image1Name = "han" + (j * col * 2 + i * 2);
        String image2Name = "han" + (j * col * 2 + (i * 2) + 1);
        int image1ID = getResources().getIdentifier(image1Name, "drawable", getPackageName());
        int image2ID = getResources().getIdentifier(image2Name, "drawable", getPackageName());
        imageView.setNormalImageID(image1ID);
        imageView.setSelectImageID(image2ID);

        String str = CommonUtils.characterCombination(chRow[j], chCol[i], ' ') + "";
        imageView.setChar(chRow[j], chCol[i], ' ');
        imageView.setString(str);

        // Set Image
        imageView.setImage(image1ID);

        // Add ImageView
        tableRow.addView(imageView);

        TableRow.LayoutParams params = (TableRow.LayoutParams) imageView.getLayoutParams();
        params.width = 0;
        params.weight = 1;
        imageView.setAdjustViewBounds(true);
        imageView.setPadding(6, 6, 6, 6);
      }

      // Add Row
      tableLayout.addView(tableRow);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mTTS.shutdown();
  }

  void cancelAllView() {
    if (!mSelect.isEmpty()) {
      for (int i = 0; i < mSelect.size(); i++) {
        mSelect.get(i).cancelImage();
      }
      mSelect.clear();
    }
  }


  public void selectView(TableImageView view) {

    // For Study Mode
    if (!mSelectMode) {

      boolean exist = false;

      // Select This Image
      view.selectImage();

      // Cancel Other Image
      if (!mSelect.isEmpty()) {
        for (int i = 0; i < mSelect.size(); i++) {
          if (mSelect.get(i).getNormalImageID() != view.getNormalImageID()) {
            mSelect.get(i).cancelImage();
            mSelect.remove(i);
            i--;
          } else {
            // Exist
            exist = true;

            // Sound at the second time
            mTTS.speak(view.mStr, TextToSpeech.QUEUE_FLUSH, null);
          }
        }
      }

      // Not Exist
      if (!exist) {
        mSelect.add(view);
      }

      // For Select Mode
    } else {

      boolean select = view.mSelect;

      // Select
      if (select) {

        boolean exist = false;

        if (!mSelect.isEmpty()) {
          for (int i = 0; i < mSelect.size(); i++) {
            if (mSelect.get(i).getNormalImageID() == view.getNormalImageID()) {
              exist = true;
            }
          }
        }

        // Not Exist
        if (!exist) {
          mSelect.add(view);
        }

        // Cancel
      } else {

        if (!mSelect.isEmpty()) {
          for (int i = 0; i < mSelect.size(); i++) {
            if (mSelect.get(i).getNormalImageID() == view.getNormalImageID()) {
              mSelect.remove(i);
            }
          }
        }

      }
    }
  }
}
