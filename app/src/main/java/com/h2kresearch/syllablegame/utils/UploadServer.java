package com.h2kresearch.syllablegame.utils;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ishsrain on 2017. 12. 6..
 */

public class UploadServer extends AsyncTask {

  // URL
  String mURL;

  //ID
  String mID;
  String mPW;

  public UploadServer(String URL, String id, String pw) {
    mURL = URL;
    mID = id;
    mPW = pw;
  }

  @Override
  protected Object doInBackground(Object[] objects) {

    try {
      // Variables
      String crlf = "\r\n";
      String twoHyphens = "--";
      String boundary = "*****";

      //Setup the request
      HttpURLConnection conn = null;

      URL url = new URL(mURL);
      conn = (HttpURLConnection) url.openConnection();
      conn.setUseCaches(false);
      conn.setDoOutput(true);
      conn.setDoInput(true);

      conn.setRequestMethod("POST");
      conn.setRequestProperty("Connection", "Keep-Alive");
      conn.setRequestProperty("Cache-Control", "no-cache");
      conn.setRequestProperty("Content-Type",
          "multipart/form-data;boundary=" + boundary);

      DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

      // --------- String
      wr.writeBytes(crlf + twoHyphens + boundary + crlf);
      wr.writeBytes("Content-Disposition: form-data; name=\"u_email\""+ crlf);
      wr.writeBytes(crlf);
      wr.writeBytes(mID);
      // --------- String

      // Result File
      String attachmentName = "data";
      String filename = "statistics.db";
      String filepath = "/data/data/" + "com.h2kresearch.syllablegame" + "/databases/" + filename;

      // Time
      long now = System.currentTimeMillis();
      Date date = new Date(now);
      SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
      String time = sdfNow.format(date);

      String uploadFilename = mID + time + ".db";

      // ---------- File
      wr.writeBytes( crlf + twoHyphens + boundary + crlf);
      wr.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName
          + "\";filename=\"" + uploadFilename + "\"" + crlf);
      wr.writeBytes(crlf);

      // Read from FileInputStream and write to OutputStream
      if (filepath != null) {
        FileInputStream fileInputStream = new FileInputStream(filepath);
        int res = 1;
        byte[] buffer = new byte[100000];
        while (0 < (res = fileInputStream.read(buffer))) {
          wr.write(buffer, 0, res);
        }
      }
      // ---------- File

      // ---------- Finish
      wr.writeBytes(crlf + twoHyphens + boundary + twoHyphens + crlf);
      wr.flush();
      wr.close();
      // ---------- Finish

      // Response
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
      Log.e("Upload Result", data);

      // Disconnection
      conn.disconnect();
    } catch (MalformedURLException | ProtocolException exception) {
      exception.printStackTrace();
    } catch (IOException io) {
      io.printStackTrace();
    }
    return null;
  }
}
