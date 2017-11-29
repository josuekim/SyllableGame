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
import com.h2kresearch.syllablegame.com.h2kresearch.syllablegame.utils.CommonUtils;

public class LoginActivity extends AppCompatActivity {

  // EditText
  EditText mID;
  EditText mPW;
  boolean mIDComplete;
  boolean mPWComplete;

  // Button
  Intent mMainIntent;
  Button mLoginButton;

  // Sign up
  Intent mSignIntent;
  TextView mSignButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    mID = (EditText) findViewById(R.id.editText1);
    mPW = (EditText) findViewById(R.id.editText2);

    mID.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void afterTextChanged(Editable editable) {
        if (editable.length() > 0 && CommonUtils.validateEmail(editable.toString())) {
          mIDComplete = true;
        } else {
          mIDComplete = false;
        }

        if (mIDComplete && mPWComplete) {
          mLoginButton.setEnabled(true);
          mLoginButton.setBackgroundResource(R.drawable.roundcorner_click);
        } else {
          mLoginButton.setEnabled(false);
          mLoginButton.setBackgroundResource(R.drawable.roundcorner);
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
        if (editable.length() > 0 && CommonUtils.validatePassword(editable.toString())) {
          mPWComplete = true;
        } else {
          mPWComplete = false;
        }

        if (mIDComplete && mPWComplete) {
          mLoginButton.setEnabled(true);
          mLoginButton.setBackgroundResource(R.drawable.roundcorner_click);
        } else {
          mLoginButton.setEnabled(false);
          mLoginButton.setBackgroundResource(R.drawable.roundcorner);
        }
      }
    });

    mMainIntent = new Intent(LoginActivity.this, MainActivity.class);
    mLoginButton = (Button) findViewById(R.id.button);
    mLoginButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        // 로그인
        String url = "http://110.76.77.86:3000/androidLogin";
        String id = mID.getText().toString();
        String pw = mPW.getText().toString();

        LoginServer loginServer = new LoginServer(url, id, pw);
        loginServer.execute();

        startActivity(mMainIntent);
      }
    });

    mSignIntent = new Intent(LoginActivity.this, SignActivity.class);
    mSignButton = (TextView) findViewById(R.id.textView3);
    mSignButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(mSignIntent);
      }
    });
  }
}
