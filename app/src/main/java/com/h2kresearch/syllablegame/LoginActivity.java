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
import com.h2kresearch.syllablegame.database.DatabaseAccess;
import com.h2kresearch.syllablegame.helper.DbOpenHelper;
import com.h2kresearch.syllablegame.model.ConfigurationModel;
import com.h2kresearch.syllablegame.utils.CommonUtils;

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

  // Database
  DatabaseAccess mDB;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    // Next Intent
    mMainIntent = new Intent(LoginActivity.this, MainActivity.class);

    // Auto Login Check
    mDB = DatabaseAccess.getInstance(this);
    mDB.open();
    String email = mDB.findAutoLoginUser();
    if(email != null && !email.equals("")) {

      // Assign Global User
      ConfigurationModel conf = ConfigurationModel.getInstance();
      conf.setEmail(email);

      // Login Skip
      startActivity(mMainIntent);
    }

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

    mLoginButton = (Button) findViewById(R.id.button);
    mLoginButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        // 로그인
        String url = "http://110.76.77.86:3000/androidLogin";
        String id = mID.getText().toString();
        String pw = mPW.getText().toString();

        // Login DB
        boolean loginResult = false;

        // Network
        String network = CommonUtils.getWhatKindOfNetwork(getApplicationContext());
        if(!network.equals(CommonUtils.NONE_STATE)){
          // Access Server
          LoginServer loginServer = new LoginServer(url, id, pw);
          loginServer.execute();

          // TBA
          loginResult = true;
        }

        // Re-Login Local DB
        if(!loginResult){
          // Access Local DB
          loginResult = mDB.login(id, pw);
        }

        // Login Success
        if(loginResult) {
          startActivity(mMainIntent);
        } else {
          Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 확인해 주세요.", Toast.LENGTH_LONG).show();
        }
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
