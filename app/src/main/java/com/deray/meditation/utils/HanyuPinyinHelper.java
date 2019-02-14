package com.deray.meditation.utils;

import android.text.TextUtils;
import android.util.Log;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by Chris on 2018/3/8.
 */

public class HanyuPinyinHelper {

    /**
     * 将文字转为汉语拼音
     * @param ChineseLanguage 要转成拼音的中文
     */
    public static String toHanyuPinyin(String ChineseLanguage){
        char[] cl_chars = ChineseLanguage.trim().toCharArray();
        String hanyupinyin = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 输出拼音全部小写
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V) ;
        try {
            for (char cl_char : cl_chars) {
                if (String.valueOf(cl_char).matches("[\u4e00-\u9fa5]+")) {// 如果字符是中文,则将中文转为汉语拼音
                    hanyupinyin += PinyinHelper.toHanyuPinyinStringArray(cl_char, defaultFormat)[0];
                } else {// 如果字符不是中文,则不转换
                    hanyupinyin += cl_char;
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            Log.e(TAG,"字符不能转成汉语拼音");
        }
        return hanyupinyin;
    }

    private static final String TAG = "HanyuPinyinHelper";

    /**
     * 将文字转为汉语拼音
     * @param ChineseLanguage 要转成拼音的中文
     */
    public static String toHanyuPinyin2(String ChineseLanguage){
        char[] cl_chars = ChineseLanguage.trim().toCharArray();
        String hanyupinyin = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 输出拼音全部小写
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V) ;
        try {
            for (char cl_char : cl_chars) {
                if (String.valueOf(cl_char).matches("[\u4e00-\u9fa5]+")) {// 如果字符是中文,则将中文转为汉语拼音
                    hanyupinyin = PinyinHelper.toHanyuPinyinStringArray(cl_char, defaultFormat)[0] + "`";
                } else {// 如果字符不是中文,则不转换
                    hanyupinyin += cl_char;
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            Log.e(TAG,"字符不能转成汉语拼音");
        }
        return hanyupinyin;
    }

    // 获取大写 并且取出每个汉字的首字母
    public static String getFirstLettersUp(String ChineseLanguage){
        return getFirstLetters(ChineseLanguage ,HanyuPinyinCaseType.UPPERCASE);
    }
    // 获取小写 并且取出每个汉字的首字母
    public static String getFirstLettersLo(String ChineseLanguage){

        return getFirstLetters(ChineseLanguage ,HanyuPinyinCaseType.LOWERCASE);
    }

    public static String getFirstLetters(String ChineseLanguage,HanyuPinyinCaseType caseType) {
        char[] cl_chars = ChineseLanguage.trim().toCharArray();
        String hanyupinyin = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(caseType);// 输出拼音全部大写
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
        try {
            for (char cl_char : cl_chars) {
                String str = String.valueOf(cl_char);
                if (str.matches("[\u4e00-\u9fa5]+")) {// 如果字符是中文,则将中文转为汉语拼音,并取第一个字母
                    if (PinyinHelper.toHanyuPinyinStringArray(cl_char, defaultFormat) == null
                            || TextUtils.isEmpty(PinyinHelper.toHanyuPinyinStringArray(cl_char, defaultFormat)[0])){
                        continue;
                    }
                    hanyupinyin += PinyinHelper.toHanyuPinyinStringArray(cl_char, defaultFormat)[0].substring(0, 1);
                } else if (str.matches("[0-9]+")) {// 如果字符是数字,取数字
                    hanyupinyin += cl_char;
                } else if (str.matches("[a-zA-Z]+")) {// 如果字符是字母,取字母
                    hanyupinyin += cl_char;
                } else {// 否则不转换
                    hanyupinyin += cl_char;//如果是标点符号的话，带着
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            Log.e(TAG,"字符不能转成汉语拼音");
        }
        return hanyupinyin;
    }

    public static String getPinyinString(String ChineseLanguage){
        char[] cl_chars = ChineseLanguage.trim().toCharArray();
        String hanyupinyin = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 输出拼音全部大写
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
        try {
            for (char cl_char : cl_chars) {
                String str = String.valueOf(cl_char);
                if (str.matches("[\u4e00-\u9fa5]+")) {// 如果字符是中文,则将中文转为汉语拼音,并取第一个字母
                    // 因为有其他语种的歌曲，会解析不了拼音,直接跳过就好了

                        if (PinyinHelper.toHanyuPinyinStringArray(cl_char, defaultFormat) == null
                                || TextUtils.isEmpty(PinyinHelper.toHanyuPinyinStringArray(cl_char, defaultFormat)[0])){
                            continue;
                        }
                    hanyupinyin += PinyinHelper.toHanyuPinyinStringArray(
                            cl_char, defaultFormat)[0];
                } else if (str.matches("[0-9]+")) {// 如果字符是数字,取数字
                    hanyupinyin += cl_char;
                } else if (str.matches("[a-zA-Z]+")) {// 如果字符是字母,取字母

                    hanyupinyin += cl_char;
                } else {// 否则不转换
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
        return hanyupinyin;
    }
    /**
     * 取第一个汉字的第一个字符
     * @Title: getFirstLetter
     * @Description: TODO
     * @return String
     * @throws
     */
    public static String getFirstLetter(String ChineseLanguage){
        char[] cl_chars = ChineseLanguage.trim().toCharArray();
        String hanyupinyin = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);// 输出拼音全部大写
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
        try {
            String str = String.valueOf(cl_chars[0]);
            if (str.matches("[\u4e00-\u9fa5]+")) {// 如果字符是中文,则将中文转为汉语拼音,并取第一个字母
                hanyupinyin = PinyinHelper.toHanyuPinyinStringArray(
                        cl_chars[0], defaultFormat)[0].substring(0, 1);
            } else if (str.matches("[0-9]+")) {// 如果字符是数字,取数字
                hanyupinyin += cl_chars[0];
            } else if (str.matches("[a-zA-Z]+")) {// 如果字符是字母,取字母

                hanyupinyin += cl_chars[0];
            } else {// 否则不转换

            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            Log.e(TAG,"字符不能转成汉语拼音");
        }
        return hanyupinyin;
    }
}
