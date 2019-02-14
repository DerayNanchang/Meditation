package com.deray.meditation.utils;

import android.os.Handler;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ThreadUtils {
    /**
     * 子线程执行task
     */
    public static void runInThread(Runnable task) {
        new Thread(task).start();
    }

    /**
     * 创建一个主线程中handler
     */
    public static Handler mHandler = new Handler();

    /**
     * UI线程执行task
     */
    public static void runInUIThread(Runnable task) {
        mHandler.post(task);
    }

    private static Map<String, Timer> mTimerMap = new HashMap<>();

    public static void period(String key, long period, TimerTask task) {
        if (!mTimerMap.containsKey(key)) {
            Timer timer = new Timer();
            mTimerMap.put(key, timer);
            timer.schedule(task, 0, period);
        } else {
            Logger.d("Timer is exist");
        }
    }

    public static void cancel(String key) {
        Timer timer = mTimerMap.get(key);
        if (timer != null) {
            mTimerMap.remove(key);
            timer.cancel();
            timer.purge();
        }
    }
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
