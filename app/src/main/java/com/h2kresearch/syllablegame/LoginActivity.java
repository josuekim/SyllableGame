package com.h2kresearch.syllablegame;

import android.content.Intent;
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
import com.h2kresearch.syllablegame.model.ConfigurationModel;
import com.h2kresearch.syllablegame.utils.CommonUtils;
import com.h2kresearch.syllablegame.utils.LoginServer;
import com.h2kresearch.syllablegame.utils.UploadServer;

public class LoginActivity extends ParentActivity {

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

  ConfigurationModel mConf;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    // Next Intent
    mMainIntent = new Intent(LoginActivity.this, MainActivity.class);
    mMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

    mConf = ConfigurationModel.getInstance();

    // Auto Login Check
    mDB = DatabaseAccess.getInstance(this);
    mDB.open();
    String email = mDB.findAutoLoginUser();
    if(email != null && !email.equals("")) {

      // Assign Global User
      mConf.setEmail(email);

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
        String url = "http://ec2-13-125-80-58.ap-northeast-2.compute.amazonaws.com:3000/androidLogin";
        String uploadUrl = "http://ec2-13-125-80-58.ap-northeast-2.compute.amazonaws.com:3000/androidLogin";
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
          loginResult = false;

          // Access Upload Server
//          UploadServer uploadServer = new UploadServer(uploadUrl);
//          uploadServer.execute();
        }

        // Re-Login Local DB
        if(!loginResult){
          // Access Local DB
          loginResult = mDB.login(id, pw);
          mConf.setEmail(id);
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
    mSignIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    mSignButton = (TextView) findViewById(R.id.textView3);
    mSignButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(mSignIntent);
      }
    });
  }
}
