package com.h2kresearch.syllablegame.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.h2kresearch.syllablegame.helper.DbOpenHelper;
import com.h2kresearch.syllablegame.model.ConfigurationModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Josh on 2017. 11. 28..
 */

public class DatabaseAccess {
  private SQLiteOpenHelper openHelper;
  private SQLiteDatabase database;
  private static DatabaseAccess instance;
  private ConfigurationModel mConf;

  private DatabaseAccess(Context context){
    this.openHelper = new DbOpenHelper(context);
  }

  public static DatabaseAccess getInstance(Context context){
    if(instance == null){
      instance = new DatabaseAccess(context);
    }
    return instance;
  }

  public void open(){
    this.database = openHelper.getWritableDatabase();
  }

  public void close(){
    if(database != null){
      this.database.close();
    }
  }

  public List<Map> getList(){
    List<Map> list = new ArrayList<>();
    Map parameter = new HashMap();
    Cursor cursor = database.rawQuery("SELECT * FROM pure_hangul_syllable", null);
    cursor.moveToFirst();
    while (!cursor.isAfterLast()){
      parameter.put("index",cursor.getInt(0));
      parameter.put("code",cursor.getString(1));
      list.add(parameter);
      cursor.moveToNext();
      Log.d("test",parameter.get("index")+" : " + parameter.get("code"));
    }
    cursor.close();
    return list;
  }

  public String findAutoLoginUser(){

    // Access DB
    Cursor cursor = database.rawQuery("SELECT * FROM hangul_user_info", null);
    cursor.moveToFirst();

    // Find "Y"
    while ( !cursor.isAfterLast() ){
      if(cursor.getString(3).equals("Y")) {

        // Return User Email
        return cursor.getString(1);
      }
      cursor.moveToNext();
    }
    cursor.close();

    // No one
    return null;
  }

  public boolean login(String id, String pw) {

    // Access DB
    Cursor cursor = database.rawQuery("SELECT * FROM hangul_user_info", null);
    cursor.moveToFirst();

    // Find User
    while ( !cursor.isAfterLast() ) {
        if(cursor.getString(1).equals(id)
            && cursor.getString(2).equals(pw)) {

          // Update DB
          int index = cursor.getInt(0);
          ContentValues cv = new ContentValues();
          cv.put("session", "Y");
          database.update("hangul_user_info", cv, "_id="+index, null);

          // Return true
          return true;
        }
        cursor.moveToNext();
    }
    cursor.close();

    // Wrong ID or PW
    return false;
  }

  public boolean logout(String id) {

    // Access DB
    Cursor cursor = database.rawQuery("SELECT * FROM hangul_user_info", null);
    cursor.moveToFirst();

    // Find User
    while ( !cursor.isAfterLast() ) {
      if(cursor.getString(1).equals(id)) {

        // Update DB
        int index = cursor.getInt(0);
        ContentValues cv = new ContentValues();
        cv.put("session", "N");
        database.update("hangul_user_info", cv, "_id="+index, null);

        // Return true
        return true;
      }
      cursor.moveToNext();
    }
    cursor.close();

    // Wrong ID or PW
    return false;
  }

  public long signup(String id, String pw) {

    // Insert DB
    ContentValues cv = new ContentValues();
    cv.put("email", id);
    cv.put("password", pw);
    cv.put("session", "Y");

    // return index
    return database.insert("hangul_user_info", null, cv);
  }

  public void insertAccessLog(String id){
    Calendar calendar = Calendar.getInstance();

    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
    String formattedDate = df.format(calendar.getTime());
    mConf = ConfigurationModel.getInstance();
    mConf.setToday(formattedDate);

    Cursor cursor = database.query("hangul_access",new String[]{"email","access_time"},"email=? and access_time=?", new String[]{id, formattedDate}, null, null, null);
    if(cursor.getCount()<1){
      ContentValues cv = new ContentValues();
      cv.put("email",id);
      cv.put("access_time", formattedDate);
      long flag = database.insert("hangul_access",null,cv);
      Log.d("database","database tracking : " + flag);
      cv.remove("access_time");
      cv.put("learning_date", formattedDate);
      database.insert("hangul_daily",null,cv);
    }
  }

}
