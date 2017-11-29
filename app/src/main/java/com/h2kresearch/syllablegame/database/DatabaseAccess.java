package com.h2kresearch.syllablegame.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.h2kresearch.syllablegame.helper.DbOpenHelper;
import java.util.ArrayList;
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

}
