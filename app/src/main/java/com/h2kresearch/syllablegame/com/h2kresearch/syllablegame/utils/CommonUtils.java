package com.h2kresearch.syllablegame.com.h2kresearch.syllablegame.utils;

import java.util.Arrays;

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

}
