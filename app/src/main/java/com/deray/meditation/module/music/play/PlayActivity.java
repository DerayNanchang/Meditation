package com.deray.meditation.module.music.play;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.SeekBar;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseActivity;
import com.deray.meditation.cache.MusicCacheManager;
import com.deray.meditation.cache.base.CacheManager;
import com.deray.meditation.config.Constants;
import com.deray.meditation.databinding.ActivityPlayBinding;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.db.manager.music.MusicManager;
import com.deray.meditation.db.manager.playlist.PlayListManager;
import com.deray.meditation.manage.music.AudioPlayerManager;
import com.deray.meditation.module.music.Lyric;
import com.deray.meditation.service.music.TimingService;
import com.deray.meditation.ui.anim.AnimaUtils;
import com.deray.meditation.ui.dialog.TimePickerDialog;
import com.deray.meditation.ui.view.FixedSpeedScroller;
import com.deray.meditation.ui.view.LyricView;
import com.deray.meditation.ui.view.RotateMusicDiskView;
import com.deray.meditation.utils.DensityUtil;
import com.deray.meditation.utils.MusicUtils;
import com.deray.meditation.utils.ServiceUtils;
import com.deray.meditation.utils.StringUtils;
import com.deray.meditation.utils.log.Toast;
import com.deray.meditation.utils.rx.RxBusManager;
import com.deray.meditation.utils.rx.bean.PlayMusic;
import com.zhouwei.blurlibrary.EasyBlur;

import java.util.ArrayList;
import java.util.List;

/**
 * author: ${USER}
 * Blog: https://www.jianshu.com/u/a3534a2292e8
 * Date: ${DATE}
 * Description
 */
public class PlayActivity extends BaseActivity<ActivityPlayBinding> {
    private static SeekBar seekBar;
    private boolean left = false;
    private boolean right = false;
    int lastValue = -1;
    private boolean isScrolling = false;
    /**
     * 实时更新进度条
     */
    @SuppressLint("HandlerLeak")
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 获取实时进度
            if (seekBar != null) {
                Bundle bundle = msg.getData();
                int progress = bundle.getInt(Constants.Music.PLAY_ACTIVITY_KEY_MUSIC_PROGRESS);
                int duration = bundle.getInt(Constants.Music.PLAY_ACTIVITY_KEY_MUSIC_DURATION);
                seekBar.setMax(duration);
                seekBar.setProgress(progress);
            }
        }
    };
    private String playMode;
    private PlayViewPageAdapter pageAdapter;
    private int position;
    private List<Music> musics;
    private List<Music> pictureList;
    private long lastMillis;
    private long millis;
    private AudioPlayerManager manager;
    private boolean isFast = true;
    private AlertDialog dialog;

    private boolean isLyric = false;


    @Override
    protected boolean isAddMusicLayout() {
        return false;
    }

    @Override
    protected int getLayoutContentViewID() {
        return R.layout.activity_play;
    }


    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        manager = AudioPlayerManager.get();
        millis = System.currentTimeMillis();
        lastMillis = millis;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        seekBar = view.sbPlayMusicBar;
        musics = MusicCacheManager.get().getQueueMusics();
        position = getPosition();
        initToolbar();
        initData();
        initViewPage();
        initEvent();
    }

    public int getPosition() {
        position = MusicCacheManager.get().getQueueMusicPosition();
        position += 1;
        return position;
    }

    private void initToolbar() {
        int height = DensityUtil.getStatusBarHeight(getApplicationContext());
        view.tbPlayBar.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.tbPlayBar.getLayoutParams();
        ViewGroup.LayoutParams params = view.tbPlayBar.getLayoutParams();
        params.height = view.tbPlayBar.getMeasuredHeight() + height;
        view.tbPlayBar.setLayoutParams(params);
        view.tbPlayBar.setPadding(
                view.tbPlayBar.getPaddingLeft(),
                view.tbPlayBar.getPaddingTop() + height,
                view.tbPlayBar.getPaddingRight(),
                view.tbPlayBar.getPaddingBottom());


        boolean crontabState = MusicCacheManager.get().getServiceCrontab();
        if (crontabState) {
            view.ivCrontabs.setVisibility(View.VISIBLE);
        } else {
            view.ivCrontabs.setVisibility(View.GONE);
        }
    }

    private void initViewPage() {
        if (musics != null && musics.size() > 0) {
            Music music = musics.get(0);
            Music firstMusic = new Music();
            firstMusic.setAlbumID(music.getAlbumID());
            firstMusic.setMusicID(music.getMusicID());

            FixedSpeedScroller scroller = new FixedSpeedScroller(this);
            scroller.setmDuration(10000);
            scroller.initViewPagerScroll(view.vpPlayCenter);//这个是设置切换过渡时间为2秒

            Music music2 = musics.get(musics.size() - 1);
            Music lastMusic = new Music();
            lastMusic.setAlbumID(music2.getAlbumID());
            lastMusic.setMusicID(music2.getMusicID());


            pictureList = new ArrayList<>();
            pictureList.addAll(musics);
            pictureList.add(0, lastMusic);
            pictureList.add(pictureList.size(), firstMusic);
        }
        // 选中保存的那个页面
        if (pictureList != null && pictureList.size() > 0) {
            pageAdapter = new PlayViewPageAdapter(pictureList, this);


            view.vpPlayCenter.setAdapter(pageAdapter);
            view.vpPlayCenter.setCurrentItem(position);
            PlayMusic music = MusicCacheManager.get().getCachePlayMusic();
            if (music != null) {
                if (music.isPlayStatus()) {
                    pageAdapter.setState(PlayViewPageAdapter.START);
                } else {
                    pageAdapter.setState(PlayViewPageAdapter.STOP);
                }
            } else {
                pageAdapter.setState(PlayViewPageAdapter.STOP);
            }
            // 设置初始化背景图片, 公用一个 list
            setBGP(pictureList, position);
        }
    }


    private void setBGP(List<Music> musics, int position) {
        String key = MusicCacheManager.get().setKey(musics.get(position).getMusicID());
        Bitmap bitmap = CacheManager.get(key);
        if (bitmap == null) {
            bitmap = MusicUtils.getPlayBitmap(this, musics.get(position).getMusicID(), musics.get(position).getAlbumID());
            CacheManager.put(MusicCacheManager.get().setKey(musics.get(position).getMusicID()), bitmap);
        }
        Bitmap blur = EasyBlur.with(this)
                .bitmap(bitmap)
                .scale(4)
                .radius(10)
                .blur();
        view.ivBgp.setImageBitmap(blur);
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(3000);    //深浅动画持续时间
        animation.setFillAfter(true);   //动画结束时保持结束的画面
        view.ivBgp.setAnimation(AnimaUtils.alpha(0.0f, 1.0f));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initData() {
        PlayMusic music = MusicCacheManager.get().getCachePlayMusic();
        playMode = MusicCacheManager.get().getPlayMode();
        AudioPlayerManager.get().setPlayMode(playMode);
        if (music != null) {
            view.tbPlayBar.setTitle(music.getMusicName());
            view.tbPlayBar.setSubtitle(music.getSinger());
            view.tvPlayDuration.setText(music.getDurationStr());
            seekBar.setMax(music.getDuration());
            if (music.isPlayStatus()) {
                view.ivPlay.setImageResource(R.drawable.svg_play_3);
            } else {
                view.ivPlay.setImageResource(R.drawable.svg_pause_3);
            }

            if (playMode != null) {
                switch (playMode) {
                    case Constants.Manager.AUDIO_PLAY_MANAGER_ORDER:
                        view.ivPlayMode.setImageResource(R.drawable.svg_order);
                        break;
                    case Constants.Manager.AUDIO_PLAY_MANAGER_RANDOM:
                        view.ivPlayMode.setImageResource(R.drawable.svg_random);
                        break;
                    case Constants.Manager.AUDIO_PLAY_MANAGER_CIRCULATION:
                        view.ivPlayMode.setImageResource(R.drawable.svg_circulation);
                        break;
                    default:
                        view.ivPlayMode.setImageResource(R.drawable.svg_order);
                }
            }
            view.lyric.setDuration(manager.getDuration());
            //getMusicInfo(music.getMusicName());
        }
        getLyric();
    }

    private void getLyric() {
        Lyric lyric = MusicCacheManager.get().getLyric();
        view.lyric.setLyric(lyric);
    }


    private void initEvent() {

        /*view.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.setSeekTo(13677);
            }
        });*/

        pageAdapter.setOnItemListener(new PlayViewPageAdapter.OnItemListener() {
            @Override
            public void onClick() {
                centerClick();
            }
        });

        view.lyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerClick();
            }
        });

        // viewPage 滑动
        view.vpPlayCenter.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // 上一曲 下一曲的逻辑
                if (isScrolling) {
                    pageAdapter.setState(PlayViewPageAdapter.STOP);
                    if (lastValue > positionOffsetPixels) {
                        // 递减 右滑动 上一曲 并且要滑动成功
                        right = true;
                        left = false;

                    } else if (lastValue < positionOffsetPixels) {
                        // 递增 左滑动 下一曲
                        left = true;
                        right = false;
                    } else {
                        WindowManager windowManager = getWindowManager();
                        int height = windowManager.getDefaultDisplay().getHeight();
                        if (lastValue < height / 2) {
                            // 说明右滑
                            right = true;
                        } else if (lastValue > height / 2) {
                            // 说明左滑
                            left = true;
                        } else {
                            left = right = false;
                        }
                    }
                } else {
                    if (!isFast) {
                        pageAdapter.setState(PlayViewPageAdapter.START);
                    } else {
                        isFast = false;
                    }
                }
                lastValue = positionOffsetPixels;

                // viewPage 无线循环 逻辑
                if (positionOffset == 0) {//只有当图片完全滑入屏幕时才跳转 不然就露馅儿了
                    if (position == pictureList.size() - 1) {
                        view.vpPlayCenter.setCurrentItem(1, false); //取消自带的动画效果
                    }
                    if (position == 0) {
                        view.vpPlayCenter.setCurrentItem(pictureList.size() - 2, false);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                PlayActivity.this.position = position;
                setBGP(pictureList, position);
                // 有效滑动
                if (left) {
                    // 左滑 下一曲
                    millis = System.currentTimeMillis();
                    if (millis - lastMillis > 500) {
                        manager.setController(true);
                        manager.next();
                        lastMillis = millis;
                    }
                } else if (right) {
                    // 右滑 上一曲
                    millis = System.currentTimeMillis();
                    if (millis - lastMillis > 500) {
                        manager.setController(true);
                        manager.pre();
                        lastMillis = millis;
                    }
                }
                // 无效滑动
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (pageAdapter == null) {
                    return;
                }
                if (state == 1) {
                    isScrolling = true;
                } else {
                    isScrolling = false;
                }
                RotateMusicDiskView rotateView = pageAdapter.getRotateView();
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        rotateView.startAnimotion();
                        break;
                    // 按下中
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        rotateView.stopAnimotion();
                        break;
                    // 滑动了 View
                    case ViewPager.SCROLL_STATE_SETTLING:
                        rotateView.stopAnimotion();
                        break;
                }
            }
        });
        manager.setOnFinish(new AudioPlayerManager.OnFinish() {
            @Override
            public void onFinish() {
                // 下一曲
                millis = System.currentTimeMillis();
                if (millis - lastMillis > 500) {
                    next(false);
                    lastMillis = millis;
                }
            }
        });

        view.sbPlayMusicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                view.tvPlayProgress.setText(StringUtils.formatTime("mm:ss", progress));

                // 刷新歌词
                view.lyric.updateProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                manager.setSeekTo(seekBar.getProgress());
            }
        });

        // 更改音乐进度
        view.lyric.setOnUpdateProgressListener(new LyricView.UpdateProgress() {
            @Override
            public void onUpdateProcess(int progress) {
                manager.setSeekTo(progress);
            }
        });

        view.tbPlayBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 播放模式的点击时间内
        view.ivPlayMode.setOnClickListener(this);

        // 上一曲的点击事件
        view.ivPre.setOnClickListener(this);

        // 下一曲的点击事件
        view.ivNext.setOnClickListener(this);

        //view.rlPlay.setOnClickListener(this);

        view.ivPlay.setOnClickListener(this);

        view.ivLike.setOnClickListener(this);

        view.ivCrontabs.setOnClickListener(this);

        view.ivMenu.setOnClickListener(this);

        RxBusManager.get().getPlayMusicInfo(view.tbPlayBar, view.ivPlay, view.tvPlayDuration);
        // 耳机插拔
        RxBusManager.get().getMusicPlayState(view.ivPlay, pageAdapter);

        AudioPlayerManager.get().setOnLyricCallBackListener(new AudioPlayerManager.OnLyricCallBack() {
            @Override
            public void onCallback() {
                getLyric();
            }
        });

        //RxBusManager.get().getLyric(view.lyric);
        /*AudioPlayerManager.get().setOnLyricCallbackListener(new LyricCallback() {
            @Override
            public void onBack(Lyric lyric) {
                System.out.println(JSON.toJSON(lyric));

            }
        });*/
    }

    private void centerClick() {
        /*if (isLyric) {
            view.lyric.setVisibility(View.GONE);
            view.rlViewPage.setVisibility(View.VISIBLE);
            isLyric = false;
        } else {
            view.lyric.setVisibility(View.VISIBLE);
            view.rlViewPage.setVisibility(View.GONE);
            isLyric = true;
        }*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play_mode:
                manager.updatePlayModeState();
                switch (manager.getPlayMode()) {
                    case Constants.Manager.AUDIO_PLAY_MANAGER_ORDER:
                        view.ivPlayMode.setImageResource(R.drawable.svg_order);
                        break;
                    case Constants.Manager.AUDIO_PLAY_MANAGER_RANDOM:
                        view.ivPlayMode.setImageResource(R.drawable.svg_random);
                        break;
                    case Constants.Manager.AUDIO_PLAY_MANAGER_CIRCULATION:
                        view.ivPlayMode.setImageResource(R.drawable.svg_circulation);
                        break;
                }
                break;
            case R.id.iv_pre: {
                pre(true);
            }

            break;
            case R.id.iv_next: {
                next(true);
            }


            break;

            case R.id.iv_play:
                if (manager.isPlay()) {
                    view.ivPlay.setImageResource(R.drawable.svg_pause_3);
                    pageAdapter.setState(PlayViewPageAdapter.STOP);
                } else {
                    view.ivPlay.setImageResource(R.drawable.svg_play_3);
                    pageAdapter.setState(PlayViewPageAdapter.START);
                }
                manager.setController(true);
                manager.playPause();

                break;

            case R.id.ivLike:
                // 是否为喜欢的
                Music music = MusicCacheManager.get().getMusic();
                if (music != null) {
                    boolean isLike = music.getIsLike();
                    if (isLike) {
                        music.setIsLike(false);
                        view.ivLike.setImageResource(R.drawable.svg_like);
                    } else {
                        music.setIsLike(true);
                        view.ivLike.setImageResource(R.drawable.svg_like_2);
                    }
                    musics.set(position, music);
                    MusicManager.get().updateMusic(music);
                    MusicCacheManager.get().updateMusicState(music);
                }
                break;

            case R.id.ivCrontabs:
                final AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
                builder.setTitle("定时停止播放");
                //    指定下拉列表的显示数据
                final String[] item = {"不开启", "10分钟", "20分钟", "30分钟", "45分钟", "60分钟", "自定义"};
                //    设置一个下拉的列表选择项
                builder.setSingleChoiceItems(item, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        if (ServiceUtils.isServiceExisted(getApplication(), Constants.Music.PLAY_ACTIVITY_TIMING_SERVICE)) {
                            // 若是没有关闭服务先关闭服务
                            stopService(new Intent(getApplicationContext(), TimingService.class));
                        }
                        switch (item[which]) {
                            case "不开启":
                                dialog.dismiss();
                            case "10分钟":
                                // 开启定时任务
                                startTimingService(10);
                                break;
                            case "20分钟":
                                startTimingService(20);
                                break;
                            case "30分钟":
                                startTimingService(30);
                                break;
                            case "45分钟":
                                startTimingService(45);
                                break;
                            case "60分钟":
                                startTimingService(60);
                                break;
                            case "自定义":
                                TimePickerDialog mTimePickerDialog = new TimePickerDialog(PlayActivity.this);
                                mTimePickerDialog.showTimePickerDialog();
                                mTimePickerDialog.setOnTimePickerDialogInterface(new TimePickerDialog.TimePickerDialogInterface() {
                                    @Override
                                    public void positiveListener(int year, int month, int day, int hour, int minute) {
                                        long ms = ((long) hour * 60 + minute) * 60 * 1000;
                                        Intent intent = new Intent(PlayActivity.this, TimingService.class);
                                        intent.putExtra(Constants.Music.PLAY_ACTIVITY_KEY_TIME, ms);
                                        PlayActivity.this.startService(intent);
                                        Toast.get().show(+hour * 60 + minute + "分钟后将自动暂停播放歌曲 ");
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void negativeListener() {
                                        dialog.dismiss();
                                    }
                                });
                                break;
                        }
                    }
                });
                dialog = builder.show();
                break;

            case R.id.iv_menu:
                // 收藏到歌单
            {
                final Music playMusic = MusicCacheManager.get().getMusic();
                PlayListManager.get().addMenuPlay(this, playMusic);
            }

            break;
        }

    }

    private void startTimingService(int time) {
        Intent i = new Intent(PlayActivity.this, TimingService.class);
        i.putExtra(Constants.Music.PLAY_ACTIVITY_KEY_TIME, time * 60 * 1000);
        PlayActivity.this.startService(i);
        dialog.dismiss();
    }

    private void pre(boolean controller) {
        manager.setController(controller);
        manager.pre();
        view.vpPlayCenter.setCurrentItem(getPosition());
        setBGP(pictureList, getPosition());
    }

    private void next(boolean controller) {
        manager.setController(controller);
        manager.next();
        view.vpPlayCenter.setCurrentItem(getPosition());
        setBGP(pictureList, getPosition());
    }
}
