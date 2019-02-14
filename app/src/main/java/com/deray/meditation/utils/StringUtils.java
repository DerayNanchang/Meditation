package com.deray.meditation.utils;

import android.text.format.DateUtils;

import java.util.Locale;

public class StringUtils {

    public static String formatTime(String pattern, long milli) {
        int m = (int) (milli / DateUtils.MINUTE_IN_MILLIS);
        int s = (int) ((milli / DateUtils.SECOND_IN_MILLIS) % 60);
        String mm = String.format(Locale.getDefault(), "%02d", m);
        String ss = String.format(Locale.getDefault(), "%02d", s);
        String replace = pattern.replace("mm", mm).replace("ss", ss);
        if (replace.contains("-")) {
            return "00:00";
        } else {

            return replace;
        }
    }
}
