package com.h2kresearch.syllablegame.utils;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginServer extends AsyncTask {

  // URL
  String mURL;

  //ID
  String mID;
  String mPW;

  public LoginServer(String URL, String id, String pw) {
    mURL = URL;
    mID = id;
    mPW = pw;
  }

  @Override
  protected Object doInBackground(Object[] objects) {

    // 인풋 파라메터값 생성
    String param = "u_id=" + mID + "&u_pw=" + mPW + "";
    try {
      // 서버연결
      URL url = new URL(mURL);
//      URL url = new URL(
//          "http://110.76.77.86:3000/androidSignup");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      conn.setRequestMethod("POST");
      conn.setDoInput(true);
      conn.setConnectTimeout(3000);
      conn.connect();

      // 안드로이드 -> 서버 파라메터값 전달
      OutputStream outs = conn.getOutputStream();
      outs.write(param.getBytes("UTF-8"));
      outs.flush();
      outs.close();

      // 서버 -> 안드로이드 파라메터값 전달
      InputStream is = null;
      BufferedReader in = null;
      String data = "";

      is = conn.getInputStream();
      in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
      String line = null;
      StringBuffer buff = new StringBuffer();
      while ((line = in.readLine()) != null) {
        buff.append(line + "\n");
      }
      data = buff.toString().trim();
      Log.e("RECV DATA", data);
      //E/RECV DATA: No registered account


    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}
