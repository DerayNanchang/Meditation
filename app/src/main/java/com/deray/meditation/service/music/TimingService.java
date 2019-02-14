package com.deray.meditation.service.music;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.deray.meditation.config.Constants;
import com.deray.meditation.receiver.AlarmReceiver;

public class TimingService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 长时间定时任务  android 官方推荐 AlarmManager
        long time = intent.getLongExtra(Constants.Music.PLAY_ACTIVITY_KEY_TIME,0);
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + time;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }
}
