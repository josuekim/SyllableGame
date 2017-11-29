package com.h2kresearch.syllablegame.helper;

import android.content.Context;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by Josh on 2017. 11. 27..
 */

public class DbOpenHelper extends SQLiteAssetHelper{

  private static final String DATABASE_NAME = "statistics.db";
  private static final int DATABASE_VERSION = 1;

  public DbOpenHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

}
