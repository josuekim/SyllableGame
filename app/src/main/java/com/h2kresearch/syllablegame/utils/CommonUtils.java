package com.h2kresearch.syllablegame.utils;

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

}
