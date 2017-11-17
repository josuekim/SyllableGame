package com.h2kresearch.syllablegame;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.h2kresearch.syllablegame.TableImageView.SelectViewListener;
import java.util.ArrayList;
import java.util.Arrays;

public class ResizeActivity extends AppCompatActivity implements SelectViewListener {

  // Select List
  ArrayList<TableImageView> mSelect = new ArrayList<TableImageView>();

  // Layout
  LinearLayout mLinearLayout;
  TextView mLeftButton;
  TextView mRightButton;

  // TTS
  TextToSpeech mTTS;

  // Intent
  Intent mIntent;

  // Select Character
  String[] mChar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_resize);

    // Pre Intent
    Intent preIntent = getIntent();
    mChar = preIntent.getStringArrayExtra("select");

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
    mLeftButton = (TextView) findViewById(R.id.textViewL);
    mRightButton = (TextView) findViewById(R.id.textViewR);

    mRightButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        // 오늘의 학습
        mIntent = new Intent(ResizeActivity.this, SyllableGameActivity.class);

        mIntent.putExtra("select", mChar);
//          mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(mIntent);
      }
    });

    mLeftButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        // 전체 보기
        onBackPressed();
      }
    });

    // Table
    TableLayout tableLayout = (TableLayout) findViewById(R.id.TableLayout);

    char[] chRow = {' ', 'ㄱ', 'ㄴ', 'ㄷ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅅ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};
    char[] chCol = {' ', 'ㅏ', 'ㅑ', 'ㅓ', 'ㅕ', 'ㅗ', 'ㅛ', 'ㅜ', 'ㅠ', 'ㅡ', 'ㅣ'};
    int row = chRow.length;
    int col = chCol.length;
    boolean[] bRow = new boolean[chRow.length];
    boolean[] bCol = new boolean[chCol.length];
    int num = 1;

    bRow[0] = true;
    bCol[0] = true;

    for(int i=0; i<mChar.length; i++) {
      char[] deChar = characterDeCombination(mChar[i].charAt(0));

      for(int j=0; j<chRow.length; j++) {
        if(deChar[0] == chRow[j]) {
          bRow[j] = true;
        }
      }

      for(int j=0; j<chCol.length; j++) {
        if(deChar[1] == chCol[j]) {
          bCol[j] = true;
          num++;
        }
      }
    }

    // weight
    LinearLayout.LayoutParams hParams = (LinearLayout.LayoutParams)tableLayout.getLayoutParams();
    hParams.weight = num;

    for (int j = 0; j < row; j++) {

      // Check
      if (!bRow[j]) {
        continue;
      }

      // New Row
      TableRow tableRow = new TableRow(this);

      for (int i = 0; i < col; i++) {

        // Check
        if (!bCol[i]) {
          continue;
        }

        // New ImageView
        TableImageView imageView = new TableImageView(this);

        // Find ImageID
        String image1Name = "han" + (j * col * 2 + i * 2);
        String image2Name = "han" + (j * col * 2 + (i * 2) + 1);
        int image1ID = getResources().getIdentifier(image1Name, "drawable", getPackageName());
        int image2ID = getResources().getIdentifier(image2Name, "drawable", getPackageName());
        imageView.setNormalImageID(image1ID);
        imageView.setSelectImageID(image2ID);
        imageView.mRatio = 2;

        String str = characterCombination(chRow[j], chCol[i], ' ') + "";
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
  public void selectView(TableImageView view) {
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
  }

  public char characterCombination(char cho, char jung, char jong) {

    char ret_val;

    // Function for Character Combination
    final char[] CHO_SUNG = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ',
        'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};
    final char[] JUNG_SUNG = {'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ',
        'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'};
    final char[] JONG_SUNG = {' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ',
        'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

    int choIndex = Arrays.binarySearch(CHO_SUNG, cho);
    int jungIndex = Arrays.binarySearch(JUNG_SUNG, jung);
    int jongIndex = Arrays.binarySearch(JONG_SUNG, jong);

    ret_val = (char) (0xAC00 + ((choIndex * 21) + jungIndex) * 28 + jongIndex);
//    Log.d("Combination Output", ""+ret_val);

    return ret_val;
  }

  public char[] characterDeCombination(char text) {

    char[] ch = new char[3];

    // Function for Character Combination
    final char[] CHO_SUNG = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ',
        'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};
    final char[] JUNG_SUNG = {'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ',
        'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'};
    final char[] JONG_SUNG = {' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ',
        'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

    int choIndex = ((((text - 0xAC00) - (text - 0xAC00) % 28 ) ) / 28 ) / 21;
    int jungIndex = ((((text - 0xAC00) - (text - 0xAC00) % 28 ) ) / 28 ) % 21;
    int jongIndex = (text - 0xAC00) % 28;

    ch[0] = CHO_SUNG[choIndex];
    ch[1] = JUNG_SUNG[jungIndex];
    ch[2] = JONG_SUNG[jongIndex];

    return ch;
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    mTTS.shutdown();
  }
}
