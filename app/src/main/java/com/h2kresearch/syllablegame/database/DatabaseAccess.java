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

  private DatabaseAccess(Context context) {
    this.openHelper = new DbOpenHelper(context);
  }

  public static DatabaseAccess getInstance(Context context) {
    if (instance == null) {
      instance = new DatabaseAccess(context);
    }
    return instance;
  }

  public void open() {
    this.database = openHelper.getWritableDatabase();
  }

  public void close() {
    if (database != null) {
      this.database.close();
    }
  }

  public List<Map> getList() {
    List<Map> list = new ArrayList<>();
    Map parameter = new HashMap();
    Cursor cursor = database.rawQuery("SELECT * FROM pure_hangul_syllable", null);
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      parameter.put("index", cursor.getInt(0));
      parameter.put("code", cursor.getString(1));
      list.add(parameter);
      cursor.moveToNext();
      Log.d("test", parameter.get("index") + " : " + parameter.get("code"));
    }
    cursor.close();
    return list;
  }

  public String findAutoLoginUser() {

    // Access DB
    Cursor cursor = database.rawQuery("SELECT * FROM hangul_user_info", null);
    cursor.moveToFirst();

    // Find "Y"
    while (!cursor.isAfterLast()) {
      if (cursor.getString(3).equals("Y")) {

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
    while (!cursor.isAfterLast()) {
      if (cursor.getString(1).equals(id)
          && cursor.getString(2).equals(pw)) {

        // Update DB
        int index = cursor.getInt(0);
        ContentValues cv = new ContentValues();
        cv.put("session", "Y");
        database.update("hangul_user_info", cv, "_id=" + index, null);

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
    while (!cursor.isAfterLast()) {
      if (cursor.getString(1).equals(id)) {

        // Update DB
        int index = cursor.getInt(0);
        ContentValues cv = new ContentValues();
        cv.put("session", "N");
        database.update("hangul_user_info", cv, "_id=" + index, null);

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

  public void insertAccessLog(String id) {
    Calendar calendar = Calendar.getInstance();

    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
    String formattedDate = df.format(calendar.getTime());
    mConf = ConfigurationModel.getInstance();
    mConf.setToday(formattedDate);

    Cursor cursor = database
        .query("hangul_access", new String[]{"email", "access_time"}, "email=? and access_time=?",
            new String[]{id, formattedDate}, null, null, null);
    if (cursor.getCount() < 1) {
      ContentValues cv = new ContentValues();
      cv.put("email", id);
      cv.put("access_time", formattedDate);
      long flag = database.insert("hangul_access", null, cv);
      Log.d("database", "database tracking : " + flag);
      cv.remove("access_time");
      cv.put("learning_date", formattedDate);
      database.insert("hangul_daily", null, cv);
    }
  }

  public int getDailyID(String email, String date) {

    // Return Value
    int dailyID = -1;

    // Access DB where email, date
    Cursor cursor = database
        .rawQuery("SELECT * FROM hangul_daily WHERE email ='" + email + "' and learning_date ='" + date +"'",
            null);
    cursor.moveToFirst();
    if (cursor.getCount() == 1) {
      dailyID = cursor.getInt(0);
    }
    cursor.close();

    return dailyID;
  }

  public int getDailyAchieve(int id) {

    // Return Value
    int dailyAchieve = -1;

    // Access DB where Daily ID
    Cursor cursor = database.rawQuery("SELECT * FROM hangul_daily WHERE _id ='" + id + "'", null);
    cursor.moveToFirst();
    if (cursor.getCount() == 1) {
      dailyAchieve = cursor.getInt(3);
    }
    cursor.close();

    return dailyAchieve;
  }

  public ArrayList<Map> getDailyAchieveSound(int id) {

    // Return Value
    ArrayList<Map> list = new ArrayList<Map>();

    // Access DB where Daily ID
    Cursor cursor = database
        .rawQuery("SELECT * FROM hangul_study_daily WHERE daily_id ='" + id + "'", null);
    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        // Param
        Map<String,Integer> param = new HashMap<String,Integer>();
        param.put("syllable_code", cursor.getInt(2));
        param.put("exam_cnt", cursor.getInt(3));
        param.put("correct_cnt", cursor.getInt(4));

        // Add to List
        list.add(param);

        // Move to Next
        cursor.moveToNext();
      }
    }
    cursor.close();

    return list;
  }

  public ArrayList<Map> getDailyWrongSound(int id) {

    // Return Value
    ArrayList<Map> list = new ArrayList<>();

    // Access DB where Daily ID
    Cursor cursor = database.rawQuery("SELECT * FROM hangul_study_daily WHERE daily_id ='" + id + "'", null);
    cursor.moveToFirst();
    if(cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        // Study Character
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("syllable_code", cursor.getInt(2));

        // Wrong Sound List
        ArrayList<Map> listParam = getDailyWrongList(cursor.getInt(0));
        param.put("wrong_list", listParam);

        // Add to List
        list.add(param);

        // Move to Next
        cursor.moveToNext();
      }
    }
    cursor.close();

    return list;
  }

  public ArrayList<Map> getDailyWrongList(int id) {

    // Return Value
    ArrayList<Map> list = new ArrayList<Map>();

    // Access DB where Study ID
    Cursor cursor = database
        .rawQuery("SELECT * FROM hangul_wrong_answer WHERE study_id ='" + id + "'", null);
    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        // Wrong Sound List
        Map<String,Integer> param = new HashMap<String,Integer>();
        param.put("wrong_code", cursor.getInt(2));
        param.put("wrong_cnt", cursor.getInt(3));

        // Add to List
        list.add(param);

        // Move to Next
        cursor.moveToNext();
      }
    }
    cursor.close();

    return list;
  }

  public ArrayList<Map> getDailyExam(int id) {

    // Return Value
    ArrayList<Map> list = new ArrayList<Map>();

    // Access DB where Daily ID
    Cursor cursor = database
        .rawQuery("SELECT * FROM hangul_exam_daily WHERE daily_id ='" + id + "'", null);
    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        // Exam List
        Map<String,Integer> param = new HashMap<String,Integer>();
        param.put("exam_consonant", cursor.getInt(2));
        param.put("exam_vowel", cursor.getInt(3));
        param.put("exam_repeat", cursor.getInt(4));
        param.put("exam_consonant_ok", cursor.getInt(5));
        param.put("exam_vowel_ok", cursor.getInt(6));

        // Add to List
        list.add(param);

        // Move to Next
        cursor.moveToNext();
      }
    }
    cursor.close();

    return list;
  }

  public int getTotalAchieve(String email) {

    // Return Value
    int totalAchieve = -1;

    // Access DB where email
    Cursor cursor = database.rawQuery("SELECT * FROM hangul_access WHERE email ='" + email + "'", null);
//    Cursor cursor = database.rawQuery("SELECT * FROM hangul_user_info WHERE _email ='" + email + "'", null);
    cursor.moveToFirst();
    if (cursor.getCount() > 1) {
      totalAchieve = cursor.getInt(5);
    }
    cursor.close();

    return totalAchieve;
  }

  public ArrayList<Map> getTotalAchieveSound(String email) {

    // Return Value
    ArrayList<Map> list = new ArrayList<Map>();

    // Access DB where email
    Cursor cursor = database
        .rawQuery("SELECT * FROM hangul_stat WHERE email ='" + email + "'", null);
    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        // Param
        Map<String,Integer> param = new HashMap<String,Integer>();
        param.put("syllable_code", cursor.getInt(2));
        param.put("exam_cnt", cursor.getInt(3));
        param.put("correct_cnt", cursor.getInt(4));

        // Add to List
        list.add(param);

        // Move to Next
        cursor.moveToNext();
      }
    }
    cursor.close();

    return list;
  }

  public ArrayList<Map> getTotalWrongSound(String email) {

    // Return Value
    ArrayList<Map> list = new ArrayList<>();

    // Access DB where email
    Cursor cursor = database.rawQuery("SELECT * FROM hangul_stat WHERE email ='" + email + "'", null);
    cursor.moveToFirst();
    if(cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        // Study Character
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("syllable_code", cursor.getInt(2));

        // Wrong Sound List
        ArrayList<Map> listParam = getTotalWrongList(cursor.getInt(0));
        param.put("wrong_list", listParam);

        // Add to List
        list.add(param);

        // Move to Next
        cursor.moveToNext();
      }
    }
    cursor.close();

    return list;
  }

  public ArrayList<Map> getTotalWrongList(int id) {

    // Return Value
    ArrayList<Map> list = new ArrayList<Map>();

    // Access DB where Stat ID
    Cursor cursor = database
        .rawQuery("SELECT * FROM hangul_wrong_stat WHERE stat_id ='" + id + "'", null);
    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        // Wrong Sound List
        Map<String,Integer> param = new HashMap<String,Integer>();
        param.put("wrong_code", cursor.getInt(2));
        param.put("wrong_cnt", cursor.getInt(3));

        // Add to List
        list.add(param);

        // Move to Next
        cursor.moveToNext();
      }
    }
    cursor.close();

    return list;
  }

  public ArrayList<Map> getTotalExam(String email) {

    // Return Value
    ArrayList<Map> list = new ArrayList<Map>();

    // Access DB where Daily ID
    Cursor cursor = database
        .rawQuery("SELECT * FROM hangul_daily WHERE email ='" + email + "'", null);
    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        int dailyID = cursor.getInt(0);

        Cursor cursor2 = database.rawQuery("SELECT * FROM hangul_exam_daily WHERE daily_id ='" + dailyID + "'", null);
        cursor2.moveToFirst();
        if (cursor2.getCount() > 0) {
          while (!cursor2.isAfterLast()) {

            // Exam List
            Map<String, Integer> param = new HashMap<String, Integer>();
            param.put("exam_consonant", cursor2.getInt(2));
            param.put("exam_vowel", cursor2.getInt(3));
            param.put("exam_repeat", cursor2.getInt(4));
            param.put("exam_consonant_ok", cursor2.getInt(5));
            param.put("exam_vowel_ok", cursor2.getInt(6));

            // Add to List
            list.add(param);

            cursor2.moveToNext();
          }
        }
        cursor2.close();


        // Move to Next
        cursor.moveToNext();
      }
    }
    cursor.close();

    return list;
  }

  public ArrayList<Map> getAchieveSoundList(String email) {

    // Return Value
    ArrayList<Map> list = new ArrayList<Map>();

    // Access DB where email
    Cursor cursor = database
        .rawQuery("SELECT * FROM hangul_daily WHERE email ='" + email + "'", null);
    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        // DailyID
        int dailyID = cursor.getInt(0);

        // Date
        String date = cursor.getString(2);

        Cursor cursor2 = database
            .rawQuery("SELECT * FROM hangul_study_daily WHERE daily_id ='" + dailyID + "'", null);
        cursor2.moveToFirst();
        if (cursor2.getCount() > 0) {
          while (!cursor2.isAfterLast()) {

            int syllableCode = cursor2.getInt(2);

            // Find Code
            boolean findCode = false;
            for(int i = 0; i<list.size(); i++) {

              Map param = list.get(i);
              // If Exist
              if(syllableCode == (int)param.get("syllable_code")) {
                findCode = true;

                // Add to Achieve List
                ArrayList achieveList = (ArrayList)param.get("achieve_list");

                Map<String, Object> param2 = new HashMap<String, Object>();

                param2.put("learning_date", date);
                param2.put("exam_cnt", cursor2.getInt(3));
                param2.put("correct_cnt", cursor2.getInt(4));

                achieveList.add(param2);

                break;
              }
            }

            // If Not Exist
            if(!findCode) {
              // Param
              Map<String, Object> param = new HashMap<String, Object>();
              param.put("syllable_code", cursor2.getInt(2));

              Cursor cursor3 = database
                  .rawQuery("SELECT * FROM hangul_stat WHERE syllable_code ='" + syllableCode + "'", null);
              cursor3.moveToFirst();

              param.put("exam_cnt", cursor3.getInt(3));
              param.put("correct_cnt", cursor3.getInt(4));

              // Achieve List
              ArrayList<Map> achieveList = new ArrayList<Map>();
              Map<String, Object> param2 = new HashMap<String, Object>();

              param2.put("learning_date", date);
              param2.put("exam_cnt", cursor2.getInt(3));
              param2.put("correct_cnt", cursor2.getInt(4));

              achieveList.add(param2);
              param.put("achieve_list", achieveList);

              // Add to List
              list.add(param);
            }

            cursor2.moveToNext();
          }
        }
        cursor2.close();


        // Move to Next
        cursor.moveToNext();
      }
    }
    cursor.close();

    return list;
  }
}
