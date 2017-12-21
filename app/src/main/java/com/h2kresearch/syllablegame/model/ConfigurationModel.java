package com.h2kresearch.syllablegame.model;

/**
 * Created by ishsrain on 2017. 11. 29..
 */

public class ConfigurationModel {

  private static ConfigurationModel instance;

  private String mEmail;
  private String mToday;
  private long mExamId;
  private int mConStudyId;
  private int mVowStudyId;
  private String mNickName;

  private ConfigurationModel() {}

  public static ConfigurationModel getInstance() {
    if(instance == null) {
      instance = new ConfigurationModel();
    }
    return instance;
  }

  public void setEmail(String emailStr) {
    this.mEmail = emailStr;
  }
  public String getEmail() {
    return mEmail;
  }

  public void setToday(String today){
    this.mToday = today;
  }
  public String getToday(){ return mToday; }

  public void setExamId(long id) {this.mExamId = id;}
  public long getExamId(){return mExamId;}

  public void setConStudyId(int id) {this.mConStudyId = id;}
  public int getConStudyId() {return mConStudyId;}

  public void setVowStudyId(int id) {this.mVowStudyId = id;}
  public int getVowStudyId() {return mVowStudyId;}

  public void setNickName(String nickName) {
    this.mNickName = nickName;
  }
  public String getNickName() {
    return mNickName;
  }
}
