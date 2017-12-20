package com.h2kresearch.syllablegame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.github.mikephil.charting.data.PieData;
import com.h2kresearch.syllablegame.utils.CommonUtils;
import com.h2kresearch.syllablegame.utils.LoginServer;
import java.util.concurrent.TimeUnit;

public class FindPWActivity extends AppCompatActivity {

  // Layout
  EditText mEditText1;
  EditText mEditText2;
  EditText mEditText3;
  Button mButton;

  // Flag
  boolean mTemporalPWComplete;
  boolean mPWComplete;

  // Mode
  Boolean mTemporal = true;
  String mEmail = "";

  // Intent
  Intent mLoginIntent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_find_pw);

    // Next Intent
    mLoginIntent = new Intent(FindPWActivity.this, LoginActivity.class);
    mLoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

    // Layout
    mEditText1 = (EditText) findViewById(R.id.editText1);
    mEditText2 = (EditText) findViewById(R.id.editText2);
    mEditText3 = (EditText) findViewById(R.id.editText3);
    mButton = (Button) findViewById(R.id.button);
    mButton.setEnabled(false);

    // Text Watcher
    mEditText1.addTextChangedListener(mEmailWatcher);

    // Click Listener
    mButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {

        // Network
        String network = CommonUtils.getWhatKindOfNetwork(getApplicationContext());
        if (!network.equals(CommonUtils.NONE_STATE)) {

          if (mTemporal) { // 임시 비밀번호 발급

            // Get Email
            mEmail = mEditText2.getText().toString();

            // Temporal PW Server
            int serverResult = 0;
//            String temporalPWURL = "http://ec2-13-125-80-58.ap-northeast-2.compute.amazonaws.com:3000/androidSignup";
//            LoginServer loginServer = new LoginServer(temporalPWURL, mEmail, "");
//            serverResult = (String) loginServer.execute().get(3, TimeUnit.SECONDS);

            // IF Success
            if (serverResult == 0) {

              Toast.makeText(getApplicationContext(), "임시 비밀번호 발급을 위한 인증 메일이 발송 되었습니다.", Toast.LENGTH_LONG).show();

              // Button Toggle
              mTemporal = false;
              mEditText1.setText("");
              mEditText1.setHint("임시 비밀번호");
              mEditText1.addTextChangedListener(mTemporalPWWatcher);
              mEditText1.setVisibility(View.VISIBLE);
              mEditText1.requestFocus();

              mEditText2.addTextChangedListener(mNewPWWatcher);
              mEditText2.setVisibility(View.VISIBLE);
              mEditText3.addTextChangedListener(mNewPWWatcher);
              mEditText3.setVisibility(View.VISIBLE);
              mButton.setText("비밀번호 설정");

            } else {
              // Error
              Toast.makeText(getApplicationContext(), "Result: "+serverResult, Toast.LENGTH_LONG).show();
            }

          } else { // 새로운 비밀번호 설정

            // Get New PW
            String temporalPW = mEditText1.getText().toString();
            String pw = mEditText2.getText().toString();

            // New PW Server
            int serverResult = 0;
//            String temporalPWURL = "http://ec2-13-125-80-58.ap-northeast-2.compute.amazonaws.com:3000/androidSignup";
//            LoginServer loginServer = new LoginServer(temporalPWURL, temporalPW, pw);
//            serverResult = (String) loginServer.execute().get(3, TimeUnit.SECONDS);

            if(serverResult == 0) {

              Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_LONG).show();

              // Intent
              startActivity(mLoginIntent);

            } else {
              // Error
              Toast.makeText(getApplicationContext(), "Result: "+serverResult, Toast.LENGTH_LONG).show();
            }

          }
        } else {
          // Network Connection Error
          Toast.makeText(getApplicationContext(), "인터넷에 연결하세요.", Toast.LENGTH_LONG).show();
        }
      }
    });

  }

  TextWatcher mEmailWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      if (s.length() > 0 && CommonUtils.validateEmail(s.toString())) {
        mButton.setEnabled(true);
        mButton.setBackgroundResource(R.drawable.roundcorner_click);
      } else {
        mButton.setEnabled(false);
        mButton.setBackgroundResource(R.drawable.roundcorner);
      }
    }
  };

  TextWatcher mTemporalPWWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

      mTemporalPWComplete = false;

      if (s.length() > 0) {
        mTemporalPWComplete = true;
      }

      if (mTemporalPWComplete && mPWComplete) {
        mButton.setEnabled(true);
        mButton.setBackgroundResource(R.drawable.roundcorner_click);
      } else {
        mButton.setEnabled(false);
        mButton.setBackgroundResource(R.drawable.roundcorner);
      }
    }
  };

  TextWatcher mNewPWWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

      mPWComplete = false;

      if (s.length() > 0 && CommonUtils.validatePassword(s.toString())) {
        String pw1 = mEditText2.getText().toString();
        String pw2 = mEditText3.getText().toString();

        if(pw1.equals(pw2)) {
          mPWComplete = true;
        }
      }

      if (mTemporalPWComplete && mPWComplete) {
        mButton.setEnabled(true);
        mButton.setBackgroundResource(R.drawable.roundcorner_click);
      } else {
        mButton.setEnabled(false);
        mButton.setBackgroundResource(R.drawable.roundcorner);
      }
    }
  };
}
