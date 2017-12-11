package com.h2kresearch.syllablegame;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.h2kresearch.syllablegame.database.DatabaseAccess;
import com.h2kresearch.syllablegame.utils.CommonUtils;
import com.h2kresearch.syllablegame.utils.LoginServer;
import java.util.concurrent.TimeUnit;

public class SignActivity extends BGMActivity {

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
        if (editable.length() > 0 && CommonUtils.validateEmail(editable.toString())) {
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
        if (editable.length() > 0 && CommonUtils.validatePassword(editable.toString())) {
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

    mPW2.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void afterTextChanged(Editable editable) {
        String pw = mPW.getText().toString();
        String pw2 = editable.toString();
        if (editable.length() > 0 && pw.equals(pw2)) {
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
    mMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    mSignButton = (Button) findViewById(R.id.button);
    mSignButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {

        String id = mID.getText().toString();
        String pw = mPW.getText().toString();

        // Sign Result
        String signResult = "";

        // Check Network State
        String network = CommonUtils.getWhatKindOfNetwork(getApplicationContext());
        if (!network.equals(CommonUtils.NONE_STATE)) {

          // Login
          String signURL = "http://ec2-13-125-80-58.ap-northeast-2.compute.amazonaws.com:3000/androidSignup";
          LoginServer loginServer = new LoginServer(signURL, id, pw);
          try {
            signResult = (String) loginServer.execute().get(3, TimeUnit.SECONDS);
          } catch (Exception e) {
            e.printStackTrace();
          }

          if (signResult.equals("0")) {

            // Local DB
            DatabaseAccess db = DatabaseAccess.getInstance(getApplicationContext());
            db.open();

            if (db.signup(id, pw) >= 0) {
              // Next Intent
              startActivity(mMainIntent);
            } else {
              Toast.makeText(getApplicationContext(), "Local DB Check!", Toast.LENGTH_LONG).show();
            }

          } else if (signResult.equals("1")) {
            Toast.makeText(getApplicationContext(), "이미 등록된 계정입니다.", Toast.LENGTH_LONG).show();
          } else if (signResult.equals("2")) {
            Toast.makeText(getApplicationContext(), "Database Addition Failed.", Toast.LENGTH_LONG)
                .show();
          } else if (signResult.equals("3")) {
            Toast.makeText(getApplicationContext(), "Database Search Failed.", Toast.LENGTH_LONG)
                .show();
          }
        } else {
          Toast.makeText(getApplicationContext(), "인터넷에 연결하세요.", Toast.LENGTH_LONG).show();
        }
      }
    });
  }
}
