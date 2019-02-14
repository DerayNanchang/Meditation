package com.deray.meditation.base;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deray.meditation.R;
import com.deray.meditation.cache.MusicCacheManager;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.manage.ServiceManager;
import com.deray.meditation.manage.music.AudioPlayerManager;
import com.deray.meditation.module.music.play.PlayActivity;
import com.deray.meditation.service.music.MusicService;
import com.deray.meditation.ui.view.CircleProgressBarView;
import com.deray.meditation.utils.glide.GlideUtils;
import com.deray.meditation.utils.rx.RxBusManager;
import com.deray.meditation.utils.rx.bean.PlayMusic;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * Created by Deray on 2017/9/1.
 */

/**
 * 不是所有的Activity 都会用到 MVP 的设计模式所以单独抽出一个 base 类来给它们使用
 *
 * @param <XV> 放弃 RxAppCompatActivity
 */
public abstract class BaseActivity<XV extends ViewDataBinding> extends AppCompatActivity implements View.OnClickListener {

    protected XV view;
    //private ImageView ivPlay;
    private ImageView ivMenu;
    private TextView tvName;
    private TextView tvSinger;
    private ImageView ivIcon;
    private CircleProgressBarView cirView;
    private Disposable subscribe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (!ServiceManager.get().isServiceExisted(getApplicationContext(),ServiceManager.ServiceNames.MUSIC_SERVICE)){
            startService(new Intent(this,MusicService.class));
        }

        if (AudioPlayerManager.get().getPlayer() == null){
            if (!ServiceManager.get().isServiceExisted(getApplicationContext(),ServiceManager.ServiceNames.MUSIC_SERVICE)){
                startService(new Intent(this,MusicService.class));
            }else {
                stopService(new Intent(this,MusicService.class));
                startService(new Intent(this,MusicService.class));
            }
        }
        // 加载布局
        view = DataBindingUtil.inflate(LayoutInflater.from(this), getLayoutContentViewID(), null, false);
        if (isAddMusicLayout()){

            View bottom = LayoutInflater.from(this).inflate(R.layout.item_view_music_base, null, false);
            bottom.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            RelativeLayout relativeLayout = new RelativeLayout(this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            initController(bottom);
            relativeLayout.addView(view.getRoot());
            relativeLayout.addView(bottom,lp);
            setContentView(relativeLayout);
        }else {
            setContentView(view.getRoot());
        }
        // 不要在 base 里面初始化建立太多的抽象方法 会导致子类实现类太多阅读性不好,一个 init 足够了
        init(savedInstanceState);

        initPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("CheckResult")
    private void initPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        // 读写权限
        subscribe = rxPermissions.requestEach(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(@NonNull Permission permission) {
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (subscribe != null && !subscribe.isDisposed()){
            subscribe.dispose();
        }
    }

    private void initController(View bottom) {


        LinearLayout llRoot = bottom.findViewById(R.id.ll_local_controller_root);
        tvName = bottom.findViewById(R.id.tv_local_controller_music_name);
        tvSinger = bottom.findViewById(R.id.tv_local_controller_music_singer);
        ivIcon = bottom.findViewById(R.id.iv_local_controller_music_icon);
        //ivPlay = bottom.findViewById(R.id.iv_local_controller_music_play);
        cirView = bottom.findViewById(R.id.cpbView);
        ivMenu = bottom.findViewById(R.id.iv_local_controller_music_menu);
        initEvent(llRoot);
        initCacheData();
        updateState();
    }

    private void initCacheData() {

        PlayMusic music = MusicCacheManager.get().getCachePlayMusic();
        if (music != null){
            tvName.setText(music.getMusicName());
            tvSinger.setText(music.getSinger());
            if (music.isPlayStatus()){
                cirView.setPlay(true);
            }else {
                cirView.setPlay(false);
            }
            GlideUtils.baseMusicState(getApplicationContext(),ivIcon, music.getIcon(),R.mipmap.icon_default);
        }
    }

    private void initEvent(LinearLayout llRoot) {
        llRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Music> musics = MusicCacheManager.get().getQueueMusics();
                if (musics != null && musics.size() > 0){
                    startActivity(new Intent(BaseActivity.this,PlayActivity.class));
                }
            }
        });
        /*ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 共享元素转场动画
                ActivityOptionsCompat optionsCompat  = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        BaseActivity.this,ivIcon,"icon");

                Intent intent = new Intent(BaseActivity.this, PlayActivity.class);
                startActivity(intent,optionsCompat.toBundle());
            }
        });*/

        cirView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AudioPlayerManager.get().isPlay()){
                    cirView.setPlay(false);
                }else {
                    cirView.setPlay(true);
                }
                AudioPlayerManager.get().setController(true);
                AudioPlayerManager.get().playPause();
            }
        });
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void updateState() {
        // 点击 Event
        RxBusManager.get().getPlayMusicProgress2(cirView);
        RxBusManager.get().getPlayMusicInfo(getApplicationContext(),tvName,tvSinger,ivIcon);

        // 耳机插拔
        RxBusManager.get().getMusicPlayState(cirView);
    }

    protected abstract boolean isAddMusicLayout();

    protected abstract int getLayoutContentViewID();

    protected abstract void init(@Nullable Bundle savedInstanceState);

    protected void slideTransitionAnimation(int duratiion, Activity startActivity,Class clazz){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            slide.setDuration(duratiion);
            getWindow().setExitTransition(slide);
            getWindow().setEnterTransition(slide);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(startActivity);
            Intent intent = new Intent(startActivity,clazz);
            startActivity(intent,optionsCompat.toBundle());
        }else {
            startActivity(new Intent(startActivity,clazz));
        }
    }

    protected void slideTransitionAnimation(Activity startActivity,Class clazz){
        slideTransitionAnimation(200,startActivity,clazz);
    }

    protected void explodeTransitionAnimation(int duratiion, Activity startActivity,Class clazz){
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Explode explode = new Explode();
            explode.setDuration(duratiion);
            getWindow().setExitTransition(explode);
            getWindow().setEnterTransition(explode);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(startActivity);
            Intent intent = new Intent(startActivity,clazz);
            startActivity(intent,optionsCompat.toBundle());
        }else {
            startActivity(new Intent(startActivity,clazz));
        }*/

        startActivity(new Intent(startActivity,clazz));
    }

    protected void explodeTransitionAnimation(Activity startActivity,Class clazz){
        explodeTransitionAnimation(500,startActivity,clazz);
    }

    protected void fadeTransitionAnimation(int duratiion, Activity startActivity,Class clazz){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(duratiion);
            getWindow().setExitTransition(fade);
            getWindow().setEnterTransition(fade);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(startActivity);
            Intent intent = new Intent(startActivity,clazz);
            startActivity(intent,optionsCompat.toBundle());
        }else {
            startActivity(new Intent(startActivity,clazz));
        }
    }

    protected void fadeTransitionAnimation(Activity startActivity,Class clazz){
        fadeTransitionAnimation(300,startActivity,clazz);
    }

    protected void setAlpha(View view, float... values) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", values);
        animator.setDuration(200);
        animator.start();
    }

}
