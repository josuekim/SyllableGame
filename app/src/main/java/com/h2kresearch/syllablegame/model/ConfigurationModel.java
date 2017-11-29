package com.h2kresearch.syllablegame.model;

/**
 * Created by ishsrain on 2017. 11. 29..
 */

public class ConfigurationModel {

  private static ConfigurationModel instance;

  private String mEmail;

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
}