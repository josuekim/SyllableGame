package com.h2kresearch.syllablegame.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Josh on 2017. 11. 17..
 */

public class CommonUtils {

  final static char[] ChoSung = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ',
      'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};
  final static char[] JungSung = {'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ',
      'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'};
  final static char[] JongSung = {' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ',
      'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

  public enum ConsonantType {
    CONSONANT1("ㄱ"), CONSONANT2("ㄴ"), CONSONANT3("ㄷ"), CONSONANT4("ㄹ"), CONSONANT5("ㅁ"), CONSONANT6(
        "ㅂ"),
    CONSONANT7("ㅅ"), CONSONANT8("ㅇ"), CONSONANT9("ㅈ"), CONSONANT10("ㅊ"), CONSONANT11(
        "ㅋ"), CONSONANT12("ㅌ"),
    CONSONANT13("ㅍ"), CONSONANT14("ㅎ");

    String con;

    ConsonantType(String con) {
      this.con = con;
    }

    public String getName() {
      return con;
    }
  }

  public enum VowelType {
    VOWEL1("ㅏ", "R"), VOWEL2("ㅑ", "R"), VOWEL3("ㅓ", "R"), VOWEL4("ㅕ", "R"), VOWEL5("ㅗ", "B"),
    VOWEL6("ㅛ", "B"), VOWEL7("ㅜ", "B"), VOWEL8("ㅠ", "B"), VOWEL9("ㅡ", "B"), VOWEL10("ㅣ", "R");

    String vow;
    String side;

    VowelType(String vow, String side) {
      this.vow = vow;
      this.side = side;
    }

    public String getVow() {
      return vow;
    }

    public String getSide() {
      return side;
    }
  }

  /***
   * 초성, 중성, 종성 결합하는 function
   * @param ch1 초성
   * @param ch2 중성
   * @param ch3 종성
   * @return 결합된 단어
   */
  public static char characterCombination(char ch1, char ch2, char ch3) {
    char ret_val;

    int a = Arrays.binarySearch(ChoSung, ch1);
    int b = Arrays.binarySearch(JungSung, ch2);
    int c = Arrays.binarySearch(JongSung, ch3);

    ret_val = (char) (0xAC00 + ((a * 21) + b) * 28 + c);
    return ret_val;
  }

  /***
   * 단어 초성, 중성, 종성으로 분해하는 function
   * @param text
   * @return 분해된 자음/모음 배열
   */
  public static char[] characterDeCombination(char text) {

    char[] ch = new char[3];

    int choIndex = ((((text - 0xAC00) - (text - 0xAC00) % 28 ) ) / 28 ) / 21;
    int jungIndex = ((((text - 0xAC00) - (text - 0xAC00) % 28 ) ) / 28 ) % 21;
    int jongIndex = (text - 0xAC00) % 28;

    ch[0] = ChoSung[choIndex];
    ch[1] = JungSung[jungIndex];
    ch[2] = JongSung[jongIndex];

    return ch;
  }

  /***
   * 중복 음소 제거 function
   * @param array
   * @return 중복 제거한 선택 된 음소 List
   */
  public static Object[] removeDuplicateArray(String[] array){
    Object[] removeArray=null;

    TreeSet ts=new TreeSet();

    for(int i=0; i<array.length; i++){
      ts.add(array[i]);
    }

    removeArray= ts.toArray();

    return removeArray;
  }

  /***
   * 자음 모음 분리 후 각 각의 List에 저장하는 function
   * @param select
   * @return List
   */
  public static ArrayList<String[]> deCombinationList(String[] select){

    char[] tempWord;
    String resultConsonants = "";
    String resultVowels = "";

    for(String word : select){
      tempWord = CommonUtils.characterDeCombination(word.charAt(0));
      resultConsonants += String.valueOf(tempWord[0]) + "|";
      resultVowels += String.valueOf(tempWord[1]) + "|";
    }

    String[] consonantsArray = resultConsonants.split("\\|");
    String[] vowelsArray = resultVowels.split("\\|");
    ArrayList<String[]> resultList = new ArrayList<String[]>();
    resultList.add(consonantsArray);
    resultList.add(vowelsArray);

    return resultList;
  }


  // 이메일주소 정규식
  public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern
      .compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

  /***
   * 이메일 주소 유효성 체크
   * @param emailStr
   * @return 유효성
   */
  public static boolean validateEmail(String emailStr) {
    Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
    return matcher.find();
  }

  //비밀번호 정규식
  public static final Pattern VALID_PASSWOLD_REGEX_ALPHA_NUM = Pattern.compile(
      "^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$"); // 4자리 ~ 16자리까지 가능

  /***
   * 비밀번호 유효성 체크
   * @param pwStr
   * @return 유효성
   */
  public static boolean validatePassword(String pwStr) {
    Matcher matcher = VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(pwStr);
    return matcher.matches();
  }

  public static final String WIFI_STATE = "WIFI";
  public static final String MOBILE_STATE = "MOBILE";
  public static final String NONE_STATE = "NONE";

  /***
   * 네트워크 종류
   * @param context
   * @return 네트워크 종류
   */
  public static String getWhatKindOfNetwork(Context context){
    ConnectivityManager cm = (ConnectivityManager)     context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    if (activeNetwork != null) {
      if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
        return WIFI_STATE;
      } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
        return MOBILE_STATE;
      }
    }
    return NONE_STATE;
  }

}
