package com.deray.meditation.module.main.view;


import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothHeadset;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseActivity;
import com.deray.meditation.config.Constants;
import com.deray.meditation.databinding.ActivityMainBinding;
import com.deray.meditation.manage.music.AudioPlayerManager;
import com.deray.meditation.module.main.adapter.HomePageAdapter;
import com.deray.meditation.module.music.MusicFragment;
import com.deray.meditation.module.read.ReadFragment;
import com.deray.meditation.module.video.VideoFragment;
import com.deray.meditation.receiver.HeadSetReceiver;
import com.deray.meditation.receiver.OnHeadSetListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private HeadSetReceiver mReceiver;

    @Override
    protected boolean isAddMusicLayout() {
        return true;
    }

    @Override
    protected int getLayoutContentViewID() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {

        initViewPage();
        initReceiver();
        initEvent();
    }

    private void initReceiver() {
        mReceiver = new HeadSetReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initViewPage() {


        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MusicFragment());
        fragments.add(new ReadFragment());
        fragments.add(new VideoFragment());
        HomePageAdapter adapter = new HomePageAdapter(getSupportFragmentManager(), fragments);
        view.vpHomePage.setAdapter(adapter);
        view.vpHomePage.setOffscreenPageLimit(2);

        // android 8.0 状态栏适配
        initBuild_O();
    }

    public void initBuild_O() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = Constants.Config.NOTIFICATION_CHANNELID_MUSIC_CONTROLLER;
            String channelName = "music controller";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    private void initEvent() {

        view.ivMusic.setOnClickListener(this);
        view.ivVideo.setOnClickListener(this);
        view.ivRead.setOnClickListener(this);
        mReceiver.setOnHeadSetListener(new OnHeadSetListener() {
            @Override
            public void onBluetoothConnected() {
                AudioPlayerManager.get().headSetStart();
            }

            @Override
            public void onBluetoothDisconnected() {
                AudioPlayerManager.get().headSetPause();
            }

            @Override
            public void onHeadsetConnected() {
                AudioPlayerManager.get().headSetStart();
            }

            @Override
            public void onHeadsetDisconnected() {
                AudioPlayerManager.get().headSetPause();
            }
        });

        // viewPage 的滑动事件
        view.vpHomePage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        selectMusic();
                        break;
                    case 1:
                        selectVideo();
                        break;
                    case 2:
                        selectRead();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    public void selectMusic(){
        view.ivMusic.setImageResource(R.drawable.svg_home_music_1);
        view.ivVideo.setImageResource(R.drawable.svg_home_video_0);
        view.ivRead.setImageResource(R.drawable.svg_home_read_0);
        view.vpHomePage.setCurrentItem(0);
    }

    public void selectVideo(){
        view.ivMusic.setImageResource(R.drawable.svg_home_music_0);
        view.ivVideo.setImageResource(R.drawable.svg_home_video_1);
        view.ivRead.setImageResource(R.drawable.svg_home_read_0);
        view.vpHomePage.setCurrentItem(1);
    }
    public void selectRead(){
        view.ivMusic.setImageResource(R.drawable.svg_home_music_0);
        view.ivVideo.setImageResource(R.drawable.svg_home_video_0);
        view.ivRead.setImageResource(R.drawable.svg_home_read_1);
        view.vpHomePage.setCurrentItem(2);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.iv_music:
            selectMusic();
            break;
            case R.id.iv_video:
                selectVideo();
                break;
            case R.id.iv_read:
                selectRead();
                break;
        }
    }
}
