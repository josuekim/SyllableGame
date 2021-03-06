package com.h2kresearch.syllablegame.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.h2kresearch.syllablegame.helper.DbOpenHelper;
import com.h2kresearch.syllablegame.model.ConfigurationModel;
import com.h2kresearch.syllablegame.utils.CommonUtils;
import com.h2kresearch.syllablegame.utils.CommonUtils.ConsonantType;
import com.h2kresearch.syllablegame.utils.CommonUtils.VowelType;
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

  /***
   * 자동 로그인 유저 찾기 function
   * @return 자동 로그인 flag 값
   */
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

  /***
   * 로그인 function
   * @param id
   * @param pw
   * @return 로그인 성공 여부 flag 값
   */
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

  public boolean loginForce(String id, String pw, String name) {

    // Login
    boolean loginResult = login(id, pw);

    // Force to Login
    if(!loginResult) {
      loginResult = true; // TBA
      signup(id, pw, name);
    }

    // Wrong ID or PW
    return loginResult;
  }

  /***
   * 로그아웃 function
   * @param id
   * @return 로그아웃 성공 여부 flag 값
   */
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

  /***
   * 회원가입 function
   * @param id
   * @param pw
   * @return db에 사용자정보 입력 성공 여부 flag 값
   */
  public long signup(String id, String pw, String name) {

    // Insert DB
    ContentValues cv = new ContentValues();
    cv.put("email", id);
    cv.put("password", pw);
    cv.put("nickname", name);
    cv.put("session", "N");

    // return index
    return database.insert("hangul_user_info", null, cv);
  }

  /***
   * 일별 접속로그 등록 function
   * @param id
   */
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
      //idle item
      database.insert("hangul_daily", null, cv);
    }
  }

  /***
   * 일별 학습 목록 저장 function
   * @param select 선택된 음절표 자음/모음 리스트
   */
  public void insertDailyStudy(String[] select) {
    ArrayList<String[]> wordList = CommonUtils.deCombinationList(select);
    Object[] dec_consonants = CommonUtils.removeDuplicateArray(wordList.get(0));
    Object[] dec_vowels = CommonUtils.removeDuplicateArray(wordList.get(1));

    Cursor cursor = null;

    for (ConsonantType ct : ConsonantType.values()) {
      for (int i = 0; i < dec_consonants.length; i++) {
        if (ct.getName().equals(dec_consonants[i].toString())) {
          Log.d("#####test#####", dec_consonants[i].toString());
          cursor = database.query("hangul_study_daily", new String[]{"_id"},
              "email=? and learning_date=? and syllable_code=?",
              new String[]{mConf.getEmail(), mConf.getToday(), String.valueOf(ct.ordinal() + 1)},
              null, null, null);
          if (cursor.getCount() < 1) {
            ContentValues cv = new ContentValues();
            cv.put("email", mConf.getEmail());
            cv.put("learning_date", mConf.getToday());
            cv.put("syllable_code", (ct.ordinal() + 1));
            database.insert("hangul_study_daily", null, cv);
          }
          cursor.close();
        }
      }
    }

    for (VowelType vt : VowelType.values()) {
      for (int i = 0; i < dec_vowels.length; i++) {
        if (vt.getVow().equals(dec_vowels[i].toString())) {
          Log.d("#####test#####", dec_vowels[i].toString());
          cursor = database.query("hangul_study_daily", new String[]{"_id"},
              "email=? and learning_date=? and syllable_code=?",
              new String[]{mConf.getEmail(), mConf.getToday(), String.valueOf(vt.ordinal() + 15)},
              null, null, null);
          if (cursor.getCount() < 1) {
            ContentValues cv = new ContentValues();
            cv.put("email", mConf.getEmail());
            cv.put("learning_date", mConf.getToday());
            cv.put("syllable_code", (vt.ordinal() + 15));
            database.insert("hangul_study_daily", null, cv);
          }
          cursor.close();
        }
      }
    }
  }

  /***
   * 일별 시험문제 저장 function
   * @param con 자음
   * @param vow 모음
   * @return 시험문제 id 값
   */
  public long insertDailyExam(int con, int vow) {
    Cursor cursor = null;

    ContentValues cv = new ContentValues();
    cv.put("email", mConf.getEmail());
    cv.put("learning_date", mConf.getToday());
    cv.put("exam_consonant", con);
    cv.put("exam_vowel", vow);
    long exam_id = database.insert("hangul_exam_daily", null, cv);
    Log.d("exam_id", "Exam ID is " + exam_id);

    mConf.setConStudyId(con); //자음 코드값 저장
    mConf.setVowStudyId(vow); //모음 코드값 저장

    return exam_id;

  }

  /***
   * 일별 시험문제에 대한 응답 저장 function
   * @param exam_id 시험문제 id
   * @param word 선택된 자음 or 모음 코드 값
   */
  public void insertExamResponse(long exam_id, int word) {
    ContentValues cv = new ContentValues();
    cv.put("exam_id", exam_id);
    cv.put("exam_response", word);
    database.insert("hangul_exam_response", null, cv);
  }

  /***
   * 일별 시험문제에 대한 오답 저장 function
   * @param syllable_code 정답 코드
   * @param word 틀린 자음 or 모음 코드 값
   * @return
   */
  public long insertWrongAnswer(int syllable_code, int word) {
    Cursor cursor = null;

    ContentValues cv = new ContentValues();
    cursor = database.query("hangul_wrong_answer", new String[]{"_id", "wrong_code_cnt"},
        "email=? and learning_date=? and syllable_code =? and wrong_code=?",
        new String[]{mConf.getEmail(), mConf.getToday(), String.valueOf(syllable_code),
            String.valueOf(word)}, null, null, null);
    if (cursor.getCount() == 1) {
      cursor.moveToFirst();
      cv.put("wrong_code_cnt", (cursor.getInt(1) + 1));
      database.update("hangul_wrong_answer", cv, "_id=" + cursor.getInt(0), null);
      cursor.close();
    } else {
      cv.put("email", mConf.getEmail());
      cv.put("learning_date", mConf.getToday());
      cv.put("syllable_code", syllable_code);
      cv.put("wrong_code", word);
      cv.put("wrong_code_cnt", 1);
      long result = database.insert("hangul_wrong_answer", null, cv);
      return result;
    }

    return -1;
  }

  /***
   * 시험문제 다시 듣기 횟수 저장 function
   * @param exam_id 시험문제 id
   */
  public void updateExamRepeat(long exam_id) {
    Cursor cursor = database.query("hangul_exam_daily", new String[]{"exam_repeat"}, "_id=?",
        new String[]{String.valueOf(exam_id)}, null, null, null);
    if (cursor.getCount() == 1) {
      cursor.moveToFirst();
      ContentValues cv = new ContentValues();
      cv.put("exam_repeat", (cursor.getInt(0) + 1));
      database.update("hangul_exam_daily", cv, "_id=" + exam_id, null);
    }
  }

  /***
   * 일별 시험문제 채점 function
   * @param exam_id 시험문제 id
   */
  public void updateExamCorrect(long exam_id) {
    int con_ok = 1;
    int vow_ok = 1;
    ContentValues cv = new ContentValues();
    Cursor cursor = database
        .query("hangul_exam_daily", new String[]{"exam_consonant", "exam_vowel"},
            "_id=?", new String[]{String.valueOf(exam_id)}, null, null, null);

    if (cursor.getCount() == 1) {
      cursor.moveToFirst();
      int con = cursor.getInt(0);
      int vow = cursor.getInt(1);
      int con_correct = 1;
      int vow_correct = 1;
      cursor.close();

      cursor = database.query("hangul_exam_response", new String[]{"_id"},
          "exam_id=? and exam_response !=? and exam_response < 15",
          new String[]{String.valueOf(exam_id), String.valueOf(con)}, null, null, null);
      if (cursor.getCount() >= 1) {
        con_ok = cursor.getCount() + 1;
        con_correct = 0;
        cursor.close();
      }
      cursor = database.query("hangul_study_daily", new String[]{"_id", "exam_cnt", "correct_cnt"},
          "email=? and learning_date=? and syllable_code=?",
          new String[]{mConf.getEmail(), mConf.getToday(), String.valueOf(con)}, null, null, null);
      if (cursor.getCount() == 1) {
        cursor.moveToFirst();
        ContentValues cv1 = new ContentValues();
        cv1.put("exam_cnt", cursor.getInt(1) + 1);
        cv1.put("correct_cnt", cursor.getInt(2) + con_correct);
        database.update("hangul_study_daily", cv1, "_id=" + cursor.getInt(0), null);
        cursor.close();
      }
      cursor = database.query("hangul_exam_response", new String[]{"_id"},
          "exam_id=? and exam_response !=? and exam_response > 14",
          new String[]{String.valueOf(exam_id), String.valueOf(vow)}, null, null, null);
      if (cursor.getCount() >= 1) {
        vow_ok = cursor.getCount() + 1;
        vow_correct = 0;
        cursor.close();
      }
      cursor = database.query("hangul_study_daily", new String[]{"_id", "exam_cnt", "correct_cnt"},
          "email=? and learning_date=? and syllable_code=?",
          new String[]{mConf.getEmail(), mConf.getToday(), String.valueOf(vow)}, null, null, null);
      if (cursor.getCount() == 1) {
        cursor.moveToFirst();
        ContentValues cv1 = new ContentValues();
        cv1.put("exam_cnt", cursor.getInt(1) + 1);
        cv1.put("correct_cnt", cursor.getInt(2) + vow_correct);
        database.update("hangul_study_daily", cv1, "_id=" + cursor.getInt(0), null);
        cursor.close();
      }

      cv.put("exam_consonant_ok", con_ok);
      cv.put("exam_vowel_ok", vow_ok);
      database.update("hangul_exam_daily", cv, "_id=" + exam_id, null);
    }

  }

  /***
   * 오늘의 학습 평균 계산 function
   * @return 오늘의 평균값
   */
  public int updateDailyAverage() {
    Cursor cursor = null;

    int total_exam_cnt = 0;
    int total_correct_cnt = 0;
    int result = 0;
    cursor = database
        .query("hangul_study_daily", new String[]{"exam_cnt", "correct_cnt"},
            "email=? and learning_date=?",
            new String[]{mConf.getEmail(), mConf.getToday()}, null, null, null);
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      total_exam_cnt += cursor.getInt(0);
      total_correct_cnt += cursor.getInt(1);
      cursor.moveToNext();
    }
    result = Math.round((total_correct_cnt * 1.f) * 100.f / (total_exam_cnt * 1.f));
    cursor.close();

    return result;
  }

  /***
   * 오늘 날짜 이전까지의 총 통계 저장 function
   */
  public void updateTotalStat() {
    Cursor cursor = database.query("hangul_update_stat", new String[]{"study_id"}, "email=?",
        new String[]{mConf.getEmail()}, null, null, null);
    if (cursor.getCount() == 1) {
      cursor.moveToFirst();
      if (cursor.getInt(0) == 0) {
        Cursor cursor1 = database.query("hangul_study_daily",
            new String[]{"email", "syllable_code", "sum(exam_cnt)", "sum(correct_cnt)"},
            "email = ? and learning_date < ?", new String[]{mConf.getEmail(), mConf.getToday()},
            "email, syllable_code", null,
            "syllable_code");
        cursor1.moveToFirst();
        while (!cursor1.isAfterLast()) {
          ContentValues cv = new ContentValues();
          cv.put("email", cursor1.getString(0));
          cv.put("syllable_code", cursor1.getInt(1));
          cv.put("exam_cnt", cursor1.getInt(2));
          cv.put("correct_cnt", cursor1.getInt(3));
          database.update("hangul_stat", cv, "email=? and syllable_code=?",
              new String[]{cursor1.getString(0), String.valueOf(cursor1.getInt(1))});
          cursor1.moveToNext();
        }
        cursor.close();
        cursor1.close();
      } else {
        Cursor cursor1 = database.query("hangul_study_daily",
            new String[]{"email", "syllable_code", "sum(exam_cnt)", "sum(correct_cnt)"},
            "email = ? and learning_date < ? and _id > ?",
            new String[]{mConf.getEmail(), mConf.getToday(), String.valueOf(cursor.getInt(0))},
            "email, syllable_code", null, "syllable_code");
        cursor1.moveToFirst();
        while (!cursor1.isAfterLast()) {
          Cursor cursor2 = database.query("hangul_stat", new String[]{"exam_cnt", "correct_cnt"},
              "email=? and syllable_code=?",
              new String[]{cursor1.getString(0), String.valueOf(cursor1.getInt(1))}, null, null,
              null);
          cursor2.moveToFirst();
          ContentValues cv = new ContentValues();
          cv.put("email", cursor1.getString(0));
          cv.put("syllable_code", cursor1.getInt(1));
          cv.put("exam_cnt", cursor1.getInt(2) + cursor2.getInt(0));
          cv.put("correct_cnt", cursor1.getInt(3) + cursor2.getInt(1));
          database.update("hangul_stat", cv, "email=? and syllable_code=?",
              new String[]{cursor1.getString(0), String.valueOf(cursor1.getInt(1))});
          cursor1.moveToNext();
          cursor2.close();
        }
        cursor.close();
        cursor1.close();
      }

      cursor = database
          .query("hangul_study_daily", new String[]{"max(_id)"}, "email=? and learning_date < ?",
              new String[]{mConf.getEmail(), mConf.getToday()}, null, null, null);
      if (cursor.getCount() > 0) {
        cursor.moveToFirst();
        ContentValues cv = new ContentValues();
        cv.put("study_id", cursor.getInt(0));
        database.update("hangul_update_stat", cv, "email=?", new String[]{mConf.getEmail()});
      }
      cursor.close();
    }
  }

  /***
   * 오늘 날짜 이전까지의 총 오답 통계 저장 function
   */
  public void updateTotalWrong() {
    Cursor cursor = database.query("hangul_update_stat", new String[]{"wrong_id"}, "email=?",
        new String[]{mConf.getEmail()}, null, null, null);
    if (cursor.getCount() == 1) {
      cursor.moveToFirst();
      if (cursor.getInt(0) == 0) {
        Cursor cursor1 = database.query("hangul_wrong_answer",
            new String[]{"email", "syllable_code", "wrong_code", "sum(wrong_code_cnt)"},
            "email = ? and learning_date < ?", new String[]{mConf.getEmail(), mConf.getToday()},
            "email, syllable_code, wrong_code", null,
            "syllable_code");
        cursor1.moveToFirst();
        while (!cursor1.isAfterLast()) {
          ContentValues cv = new ContentValues();
          cv.put("email", cursor1.getString(0));
          cv.put("syllable_code", cursor1.getInt(1));
          cv.put("wrong_code", cursor1.getInt(2));
          cv.put("wrong_code_cnt", cursor1.getInt(3));
          database.insert("hangul_wrong_stat", null, cv);
          cursor1.moveToNext();
        }
        cursor.close();
        cursor1.close();
      } else {
        Cursor cursor1 = database.query("hangul_wrong_answer",
            new String[]{"email", "syllable_code", "wrong_code", "sum(wrong_code_cnt)"},
            "email = ? and learning_date < ? and _id > ?",
            new String[]{mConf.getEmail(), mConf.getToday(), String.valueOf(cursor.getInt(0))},
            "email, syllable_code, wrong_code", null, "syllable_code");
        cursor1.moveToFirst();
        while (!cursor1.isAfterLast()) {
          Cursor cursor2 = database.query("hangul_wrong_stat", new String[]{"wrong_code_cnt"},
              "email=? and syllable_code=? and wrong_code=?",
              new String[]{cursor1.getString(0), String.valueOf(cursor1.getInt(1)),
                  String.valueOf(cursor1.getInt(2))}, null, null,
              null);
          int wrong_cnt = 0;
          if(cursor2.getCount() > 0){
            cursor2.moveToFirst();
            wrong_cnt = cursor2.getInt(0);
          }
          ContentValues cv = new ContentValues();
          cv.put("email", cursor1.getString(0));
          cv.put("syllable_code", cursor1.getInt(1));
          cv.put("wrong_code", cursor1.getInt(2));
          cv.put("wrong_code_cnt", cursor1.getInt(3) + wrong_cnt);
          database.update("hangul_wrong_stat", cv, "email=? and syllable_code=? and wrong_code=?",
              new String[]{cursor1.getString(0), String.valueOf(cursor1.getInt(1)),
                  String.valueOf(cursor1.getInt(2))});
          cursor1.moveToNext();
          cursor2.close();
        }
        cursor.close();
        cursor1.close();

      }

      cursor = database
          .query("hangul_wrong_answer", new String[]{"max(_id)"}, "email=? and learning_date < ?",
              new String[]{mConf.getEmail(), mConf.getToday()}, null, null, null);
      if (cursor.getCount() > 0) {
        cursor.moveToFirst();
        ContentValues cv = new ContentValues();
        cv.put("wrong_id", cursor.getInt(0));
        database.update("hangul_update_stat", cv, "email=?", new String[]{mConf.getEmail()});
      }
      cursor.close();
    }
  }

  /***
   * 총 통계 table 생성 function
   */
  public void createStatTable() {
    Cursor cursor = database
        .query("hangul_update_stat", null, "email = ?", new String[]{mConf.getEmail()}, null, null,
            null);
    if (cursor.getCount() != 1) {
      ContentValues cv = new ContentValues();
      cv.put("email", mConf.getEmail());
      database.insert("hangul_update_stat", null, cv);
      for (int i = 1; i <= 24; i++) {
        cv.put("syllable_code", i);
        database.insert("hangul_stat", null, cv);
      }
    }
    cursor.close();
  }

  /***
   * 오늘의 음소별 성취도 function
   * @return 음소별 성취도 ArrayList
   */
  public ArrayList<Map> getDailyAchieveSound() {

    // Return Value
    ArrayList<Map> list = new ArrayList<Map>();

    // Access DB where Email and Learning_date
    Cursor cursor = database
        .query("hangul_study_daily", null, "email=? and learning_date=?", new String[]{mConf.getEmail(), mConf.getToday()},
            null, null, null);
    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        // Param
        Map<String, Integer> param = new HashMap<String, Integer>();
        param.put("syllable_code", cursor.getInt(3));
        param.put("exam_cnt", cursor.getInt(4));
        param.put("correct_cnt", cursor.getInt(5));

        // Add to List
        list.add(param);

        // Move to Next
        cursor.moveToNext();
      }
    }
    cursor.close();

    return list;
  }

  /***
   * 오늘의 취약한 음소 function
   * @return 틀린 음소 리스트
   */
  public ArrayList<Map> getDailyWrongSound() {

    // Return Value
    ArrayList<Map> list = new ArrayList<>();

    // Access DB where Email and Learning_date
    Cursor cursor = database
        .query("hangul_study_daily", null, "email=? and learning_date=?", new String[]{mConf.getEmail(), mConf.getToday()},
            null, null, null);
    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        // Study Character
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("syllable_code", cursor.getInt(3));

        // Wrong Sound List
        ArrayList<Map> listParam = getDailyWrongList(cursor.getInt(3));
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

  /***
   * 틀린 음소 리스트 가져오기 function
   * @param code 자음 or 모음 코드
   * @return 틀린 음소 리스트
   */
  public ArrayList<Map> getDailyWrongList(int code) {

    // Return Value
    ArrayList<Map> list = new ArrayList<Map>();

    // Access DB where Study ID
    Cursor cursor = database
        .query("hangul_wrong_answer", null, "email=? and learning_date=? and syllable_code=?",
            new String[]{mConf.getEmail(), mConf.getToday(), String.valueOf(code)}, null, null,
            null);
    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        // Wrong Sound List
        Map<String, Integer> param = new HashMap<String, Integer>();
        param.put("wrong_code", cursor.getInt(4));
        param.put("wrong_code_cnt", cursor.getInt(5));

        // Add to List
        list.add(param);

        // Move to Next
        cursor.moveToNext();
      }
    }
    cursor.close();

    return list;
  }

  /***
   * 오늘의 시험정보 출력 function
   * @return 오늘의 시험정보 리스트
   */
  public ArrayList<Map> getDailyExam() {

    // Return Value
    ArrayList<Map> list = new ArrayList<Map>();

    // Access DB where Daily ID
    Cursor cursor = database
        .query("hangul_exam_daily", null, "email=? and learning_date=?", new String[]{mConf.getEmail(), mConf.getToday()}, null,
            null, null);
    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        // Exam List
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("exam_consonant", cursor.getInt(3));
        param.put("exam_vowel", cursor.getInt(4));
        param.put("exam_repeat", cursor.getInt(5));
        param.put("exam_consonant_ok", cursor.getInt(6));
        param.put("exam_vowel_ok", cursor.getInt(7));

        // Response List
        ArrayList<Integer> responseList = getExamResponseList(cursor.getInt(0));
        param.put("response_list", responseList);

        // Add to List
        list.add(param);

        // Move to Next
        cursor.moveToNext();
      }
    }
    cursor.close();

    return list;
  }

  /***
   * 오늘의 시험 응답 리스트 출력 function
   * @return 오늘의 시험 응답 리스트
   */
  public ArrayList<Integer> getExamResponseList(int id) {

    // Return Value
    ArrayList<Integer> list = new ArrayList<Integer>();

    // Access DB where Exam ID
    Cursor cursor = database
        .query("hangul_exam_response", null, "exam_id=?", new String[]{String.valueOf(id)}, null,
            null, null);
    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        // Add to List
        list.add(cursor.getInt(2));

        // Move to Next
        cursor.moveToNext();
      }
    }
    cursor.close();

    return list;
  }

  /***
   * 전체 성취도 평균 값 function
   * @return 성취도 평균값
   */
  public int getTotalAchieve() {

    // Return Value
    int totalAchieve = -1;

    int total_exam_cnt = 0;
    int total_correct_cnt = 0;

    Cursor cursor = database
        .query("hangul_study_daily", new String[]{"exam_cnt", "correct_cnt"},
            "email=? and learning_date=?",
            new String[]{mConf.getEmail(), mConf.getToday()}, null, null, null);
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      total_exam_cnt += cursor.getInt(0);
      total_correct_cnt += cursor.getInt(1);
      cursor.moveToNext();
    }

    cursor.close();

    cursor = database.query("hangul_stat", new String[]{"exam_cnt", "correct_cnt"}, "email=?", new String[]{mConf.getEmail()}, null,null,null);
    cursor.moveToFirst();
    while (!cursor.isAfterLast()){
      total_exam_cnt += cursor.getInt(0);
      total_correct_cnt += cursor.getInt(1);
      cursor.moveToNext();
    }
    cursor.close();
    totalAchieve = Math.round((total_correct_cnt * 1.f) * 100.f / (total_exam_cnt * 1.f));

    return totalAchieve;
  }

  /***
   * 전체 음소별 성취도 function
   * @return 음소별 성취도 리스트
   */
  public ArrayList<Map> getTotalAchieveSound() {

    // Return Value
    ArrayList<Map> list = new ArrayList<Map>();

    // Access DB where email
    Cursor cursor = database.rawQuery(
        "select email, syllable_code, sum(exam_cnt) as exam_cnt, sum(correct_cnt) as correct_cnt from( select email, syllable_code, exam_cnt, correct_cnt from hangul_stat where email = '"
            + mConf.getEmail()
            + "' union all select email, syllable_code, sum(exam_cnt) as exam_cnt, sum(correct_cnt) as correct_cnt from hangul_study_daily where email = '"
            + mConf.getEmail() + "'  and learning_date = '" + mConf.getToday()
            + "' group by email, syllable_code) where exam_cnt != 0 or correct_cnt != 0 group by email, syllable_code order by syllable_code",
        null);

    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        // Param
        Map<String, Integer> param = new HashMap<String, Integer>();
        param.put("syllable_code", cursor.getInt(1));
        param.put("exam_cnt", cursor.getInt(2));
        param.put("correct_cnt", cursor.getInt(3));

        // Add to List
        list.add(param);

        // Move to Next
        cursor.moveToNext();
      }
    }
    cursor.close();

    return list;
  }

  /***
   * 종합 취약한 음소 통계 function
   * @return 전체 취약한 음소 리스트
   */
  public ArrayList<Map> getTotalWrongSound() {

    // Return Value
    ArrayList<Map> list = new ArrayList<>();

    // Access DB where email
    Cursor cursor = database
        .rawQuery("SELECT * FROM hangul_stat WHERE email ='" + mConf.getEmail() + "'", null);


    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        // Study Character
        Map<String, Object> param = new HashMap<String, Object>();
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

  public ArrayList<Map> getTotalWrongList(int code) {

    // Return Value
    ArrayList<Map> list = new ArrayList<Map>();

    // Access DB where Email and Learning_date and code
    String sql = "select email, syllable_code, wrong_code, sum(wrong_code_cnt) as wrong_code_cnt "
        + "from ("
        + "select email, syllable_code, wrong_code, wrong_code_cnt "
        + "from hangul_wrong_stat where email = '"+mConf.getEmail()+"' and syllable_code = " + code
        + " union all "
        + "select email, syllable_code, wrong_code, sum(wrong_code_cnt)  as wrong_code_cnt "
        + "from hangul_wrong_answer "
        + "where email = '"+mConf.getEmail()+"' and learning_date = '"+mConf.getToday()+"' and syllable_code = " + code
        + " group by email, syllable_code, wrong_code "
        + ") group by email, syllable_code, wrong_code "
        + "order by syllable_code";
    Cursor cursor = database.rawQuery(sql,null);
    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        // Wrong Sound List
        Map<String, Integer> param = new HashMap<String, Integer>();
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

  public ArrayList<Map> getTotalExam() {

    // Return Value
    ArrayList<Map> list = new ArrayList<Map>();

    // Access DB where Daily ID
    Cursor cursor = database
        .rawQuery("SELECT * FROM hangul_daily WHERE email ='" + mConf.getEmail() + "'", null);
    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      while (!cursor.isAfterLast()) {

        String dailyDate  = cursor.getString(2);

        Cursor cursor2 = database
            .rawQuery("SELECT * FROM hangul_exam_daily WHERE learning_date ='" + dailyDate + "'", null);
        cursor2.moveToFirst();
        if (cursor2.getCount() > 0) {
          while (!cursor2.isAfterLast()) {

            // Exam List
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("exam_consonant", cursor2.getInt(3));
            param.put("exam_vowel", cursor2.getInt(4));
            param.put("exam_repeat", cursor2.getInt(5));
            param.put("exam_consonant_ok", cursor2.getInt(6));
            param.put("exam_vowel_ok", cursor2.getInt(7));

            ArrayList<Integer> responseList = getExamResponseList(cursor2.getInt(0));
            param.put("response_list", responseList);

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
        // Date
        String date = cursor.getString(2);

        Cursor cursor2 = database
            .rawQuery("SELECT * FROM hangul_study_daily WHERE learning_date ='" + date + "'", null);
        cursor2.moveToFirst();
        if (cursor2.getCount() > 0) {
          while (!cursor2.isAfterLast()) {

            int syllableCode = cursor2.getInt(3);

            // Find Code
            boolean findCode = false;
            for (int i = 0; i < list.size(); i++) {

              Map param = list.get(i);
              // If Exist
              if (syllableCode == (int) param.get("syllable_code")) {
                findCode = true;

                // Add to Achieve List
                ArrayList achieveList = (ArrayList) param.get("achieve_list");

                Map<String, Object> param2 = new HashMap<String, Object>();

                param2.put("learning_date", date);
                param2.put("exam_cnt", cursor2.getInt(4));
                param2.put("correct_cnt", cursor2.getInt(5));

                achieveList.add(param2);

                break;
              }
            }

            // If Not Exist
            if (!findCode) {
              // Param
              Map<String, Object> param = new HashMap<String, Object>();
              param.put("syllable_code", cursor2.getInt(3));

              Cursor cursor3 = database
                  .rawQuery("SELECT * FROM hangul_stat WHERE syllable_code ='" + syllableCode + "'",
                      null);
              cursor3.moveToFirst();
              if (cursor3.getCount() > 0) {
                param.put("exam_cnt", cursor3.getInt(3));
                param.put("correct_cnt", cursor3.getInt(4));
              }

              // Achieve List
              ArrayList<Map> achieveList = new ArrayList<Map>();
              Map<String, Object> param2 = new HashMap<String, Object>();

              param2.put("learning_date", date);
              param2.put("exam_cnt", cursor2.getInt(4));
              param2.put("correct_cnt", cursor2.getInt(5));

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

  public String getNickName(String id) {

    // Return Value
    String nickName = "";

    // Access DB
    Cursor cursor = database.rawQuery("SELECT * FROM hangul_user_info", null);
    cursor.moveToFirst();

    // Find User
    while (!cursor.isAfterLast()) {
      if (cursor.getString(1).equals(id)) {

        // Update DB
        nickName = cursor.getString(2);
        break;
      }
      cursor.moveToNext();
    }
    cursor.close();

    return nickName;
  }
}
