package com.deray.meditation.utils.log;

import com.orhanobut.logger.Logger;

public class LogUtils {
    private static final boolean isLog = true;

    public static void d(String clazz,String msg) {
        if (isLog) {
            Logger.d(clazz + " : " +msg);
        }
    }
}
