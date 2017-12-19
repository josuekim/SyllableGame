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
import java.util.concurrent.TimeUnit;

public class LoginActivity extends BGMActivity {

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

  // Find PW
  // Sign up
  Intent mFindIntent;
  TextView mFindIDButton;
  TextView mFindPWButton;

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
    if (email != null && !email.equals("")) {
      // Assign Global User
      mConf.setEmail(email);

      // Login Skip
      startActivity(mMainIntent);
    } else {
      // Tutorial
      Intent intent = new Intent(getBaseContext(), TutorialActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
      startActivity(intent);
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

        String id = mID.getText().toString();
        String pw = mPW.getText().toString();

        // Network
        String network = CommonUtils.getWhatKindOfNetwork(getApplicationContext());
        if (!network.equals(CommonUtils.NONE_STATE)) {

          // Login Result
          String loginResult = "";
          try {
            // Login
            String loginURL = "http://ec2-13-125-80-58.ap-northeast-2.compute.amazonaws.com:3000/androidLogin";
            LoginServer loginServer = new LoginServer(loginURL, id, pw);
            loginResult = (String) loginServer.execute().get(3, TimeUnit.SECONDS);
          } catch (Exception e) {
            e.printStackTrace();
          }

          // Login Result
          if (loginResult.equals("0")) { // Login Success

            // File Upload
            String uploadURL = "http://ec2-13-125-80-58.ap-northeast-2.compute.amazonaws.com:3000/androidDBUpload";
            UploadServer uploadServer = new UploadServer(uploadURL, id, pw);
            uploadServer.execute();

            // Force to Login Local DB
            mDB.loginForce(id,pw);

            // Main Start
            mConf.setEmail(id);
            startActivity(mMainIntent);

          } else if (loginResult.equals("1")){
            Toast.makeText(getApplicationContext(), "등록되지 않은 계정입니다.", Toast.LENGTH_LONG).show();
          } else if (loginResult.equals("2")){
            Toast.makeText(getApplicationContext(), "비밀번호를 확인해 주세요.", Toast.LENGTH_LONG).show();
          } else if (loginResult.equals("3")){
            Toast.makeText(getApplicationContext(), "Database Search Failed", Toast.LENGTH_LONG).show();
          }

        } else {
          // Login Local DB
          boolean loginLocalResult = mDB.login(id, pw);

          // Login Result
          if (loginLocalResult == true) { // Login Success
            mConf.setEmail(id);
            startActivity(mMainIntent);
          } else { // Login Fail
            Toast.makeText(getApplicationContext(), "인터넷에 연결하세요.", Toast.LENGTH_LONG).show();
          }
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

    mFindIntent = new Intent(LoginActivity.this, FindPWActivity.class);
    mFindIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    mFindIDButton = (TextView) findViewById(R.id.textView1);
    mFindIDButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getApplicationContext(), "추후 업데이트 될 예정입니다.", Toast.LENGTH_LONG).show();
      }
    });
    mFindPWButton = (TextView) findViewById(R.id.textView2);
    mFindPWButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(mFindIntent);
      }
    });
  }
}
