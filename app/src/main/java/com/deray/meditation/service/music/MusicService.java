package com.deray.meditation.service.music;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.deray.meditation.manage.music.AudioPlayerManager;

public class MusicService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //  音乐播放器管理器
        AudioPlayerManager.get().init(this);
        //  MediaSession 管理器
        //MediaSessionManager.get().init(this);
        return START_REDELIVER_INTENT;
    }


    @Override
    public void onDestroy() {
        AudioPlayerManager.get().onDestroy();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
