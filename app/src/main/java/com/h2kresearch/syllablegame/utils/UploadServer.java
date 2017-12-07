package com.h2kresearch.syllablegame.utils;

import android.os.AsyncTask;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by ishsrain on 2017. 12. 6..
 */

public class UploadServer extends AsyncTask {

  // URL
  String mURL;

  public UploadServer(String URL) {
    mURL = URL;
  }

  @Override
  protected Object doInBackground(Object[] objects) {
    try {
      // Variables
      String attachmentName = "data";
      String crlf = "\r\n";
      String twoHyphens = "--";
      String boundary = "*****";

      //Setup the request
      HttpURLConnection httpUrlConnection = null;

      URL url = new URL(mURL);
      httpUrlConnection = (HttpURLConnection) url.openConnection();
      httpUrlConnection.setUseCaches(false);
      httpUrlConnection.setDoOutput(true);
      httpUrlConnection.setDoInput(true);

      httpUrlConnection.setRequestMethod("POST");
      httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
      httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
      httpUrlConnection.setRequestProperty("Content-Type",
          "multipart/form-data;boundary=" + boundary);

      DataOutputStream wr = new DataOutputStream(httpUrlConnection.getOutputStream());

      // Result File
      String filePath = "";
      String filetag = "_";
      String filename = "result.txt";
      String filepath = filePath + "/" + filename;
      String uploadFilename = filetag + filename;

      // Start content wrapper
      wr.writeBytes(twoHyphens + boundary + crlf);
      wr.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName
          + "\";filename=\"" + uploadFilename + "\"" + crlf);
      wr.writeBytes(crlf);

      // Read from FileInputStream and write to OutputStream
      if (filepath != null) {
        FileInputStream fileInputStream = new FileInputStream(filepath);
        int res = 1;
        byte[] buffer = new byte[10000];
        while (0 < (res = fileInputStream.read(buffer))) {
          wr.write(buffer, 0, res);
        }
      }
      wr.writeBytes(crlf);

      // Test 1
      for (int i = 0; i < 3; i++) {

        filename = "q1_" + (i + 1) + ".mp4";
        filepath = filePath + "/" + filename;
        uploadFilename = filetag + filename;

        wr.writeBytes(twoHyphens + boundary + crlf);
        wr.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName
            + "\";filename=\"" + uploadFilename + "\"" + crlf);
        wr.writeBytes(crlf);

        // Read from FileInputStream and write to OutputStream
        if (filepath != null) {
          FileInputStream fileInputStream = new FileInputStream(filepath);
          int res = 1;
          byte[] buffer = new byte[10000];
          while (0 < (res = fileInputStream.read(buffer))) {
            wr.write(buffer, 0, res);
          }
        }
        wr.writeBytes(crlf);
      }

      // Finish content wrapper
      wr.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
      wr.flush();
      wr.close();

      // Response
      InputStream responseStream = new BufferedInputStream(
          httpUrlConnection.getInputStream());
      BufferedReader responseStreamReader = new BufferedReader(
          new InputStreamReader(responseStream));
      String line = "";
      StringBuilder stringBuilder = new StringBuilder();
      while ((line = responseStreamReader.readLine()) != null) {
        stringBuilder.append(line).append("\n");
      }
      responseStreamReader.close();
      String response = stringBuilder.toString();
      int returnCode = httpUrlConnection.getResponseCode();

      // Disconnection
      httpUrlConnection.disconnect();
    } catch (MalformedURLException | ProtocolException exception) {
      exception.printStackTrace();
    } catch (IOException io) {
      io.printStackTrace();
    }
    return null;
  }
}
