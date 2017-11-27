package com.h2kresearch.syllablegame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignActivity extends AppCompatActivity {

  // EditText
  EditText mID;
  EditText mPW;
  EditText mPW2;
  boolean mIDComplete;
  boolean mPWComplete;
  boolean mPW2Complete;

  // Button
  Intent mMainIntent;
  Button mSignButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign);

    mID = (EditText) findViewById(R.id.editText1);
    mPW = (EditText) findViewById(R.id.editText2);
    mPW2 = (EditText) findViewById(R.id.editText3);

    mID.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void afterTextChanged(Editable editable) {
        if (editable.length() > 0) {
          mIDComplete = true;
        } else {
          mIDComplete = false;
        }

        if (mIDComplete && mPWComplete && mPW2Complete) {
          mSignButton.setEnabled(true);
          mSignButton.setBackgroundResource(R.drawable.roundcorner_click);
        } else {
          mSignButton.setEnabled(false);
          mSignButton.setBackgroundResource(R.drawable.roundcorner);
        }
      }
    });

    mPW.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void afterTextChanged(Editable editable) {
        if (editable.length() > 0) {
          mPWComplete = true;
        } else {
          mPWComplete = false;
        }

        if (mIDComplete && mPWComplete && mPW2Complete) {
          mSignButton.setEnabled(true);
          mSignButton.setBackgroundResource(R.drawable.roundcorner_click);
        } else {
          mSignButton.setEnabled(false);
          mSignButton.setBackgroundResource(R.drawable.roundcorner);
        }
      }
    });

    mPW.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void afterTextChanged(Editable editable) {
        if (editable.length() > 0) {
          mPW2Complete = true;
        } else {
          mPW2Complete = false;
        }

        if (mIDComplete && mPWComplete && mPW2Complete) {
          mSignButton.setEnabled(true);
          mSignButton.setBackgroundResource(R.drawable.roundcorner_click);
        } else {
          mSignButton.setEnabled(false);
          mSignButton.setBackgroundResource(R.drawable.roundcorner);
        }
      }
    });

    mMainIntent = new Intent(SignActivity.this, MainActivity.class);
    mSignButton = (Button) findViewById(R.id.button);
    mSignButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {

        // 회원가입 & 로그인
        String url = "http://110.76.77.86:3000/androidSignup";
        String id = mID.getText().toString();
        String pw = mPW.getText().toString();
        String pw2 = mPW2.getText().toString();

        if (pw == pw2) {
          LoginServer loginServer = new LoginServer(url, id, pw);
          loginServer.execute();
          startActivity(mMainIntent);
        } else {
          Toast.makeText(getApplicationContext(), "비밀번호가 서로 다릅니다.", Toast.LENGTH_LONG).show();
        }
      }
    });

  }
}
