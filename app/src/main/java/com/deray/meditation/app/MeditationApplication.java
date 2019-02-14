package com.deray.meditation.app;

import android.app.Application;

import com.bumptech.glide.request.target.ViewTarget;
import com.deray.meditation.R;
import com.deray.meditation.cache.base.CacheManager;
import com.deray.meditation.cache.base.SP;
import com.deray.meditation.db.manager.base.DBManager;
import com.deray.meditation.db.manager.music.MusicManager;
import com.deray.meditation.utils.ScreenUtils;
import com.deray.meditation.utils.log.Toast;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class MeditationApplication extends Application {

    private static MeditationApplication context;

    @Override
    public void onCreate() {
        init();
        super.onCreate();
        ViewTarget.setTagId(R.id.glide_tag);
    }

    public static MeditationApplication get(){
        return context;
    }

    /**
     *  初始化 信息
     */
    private void init() {
        context = this;

        // Log 日志初始化
        Logger.addLogAdapter(new AndroidLogAdapter());
        Toast.get().init(this);

        // 缓存初始化
        SP.get().init(this);        // SP 初始化
        CacheManager.init(this);    // BitMap 初始化

        // Manager 初始化
        DBManager.get().init(this);
        MusicManager.get().init(this);
        ScreenUtils.init(this);

    }
}
