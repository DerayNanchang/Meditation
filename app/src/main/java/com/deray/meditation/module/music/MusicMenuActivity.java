package com.deray.meditation.module.music;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseActivity;
import com.deray.meditation.base.OnEasyItemListener;
import com.deray.meditation.cache.MusicCacheManager;
import com.deray.meditation.config.Constants;
import com.deray.meditation.databinding.ActivityPlayListInfoBinding;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.db.bean.PlayList;
import com.deray.meditation.db.manager.playlist.PlayListManager;
import com.deray.meditation.manage.music.AudioPlayerManager;
import com.deray.meditation.module.music.children.me.children.local.children.single.adapter.SingleAdapter;
import com.deray.meditation.ui.dialog.BottomSheetDialogUtils;
import com.deray.meditation.ui.mannager.ScrollLinearLayoutManager;
import com.deray.meditation.ui.view.SlideScrollView;
import com.deray.meditation.utils.MusicUtils;
import com.deray.meditation.utils.RecyclerViewUtils;
import com.deray.meditation.utils.ScreenUtils;
import com.deray.meditation.utils.glide.GlideHelp;
import com.deray.meditation.utils.glide.GlideUtils;
import com.deray.meditation.utils.rx.RxBusManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/20.
 */

public class MusicMenuActivity extends BaseActivity<ActivityPlayListInfoBinding> {
    private String playListID;
    private List<Integer> colors = new ArrayList<>();
    private SingleAdapter adapter;
    private int scrollHeight;
    private int newRed;
    private int newGreen;
    private int newBlue;
    private BottomSheetDialog sheetDialog;
    private ScrollLinearLayoutManager manager;

    @Override
    protected boolean isAddMusicLayout() {
        return true;
    }

    @Override
    protected int getLayoutContentViewID() {
        return R.layout.activity_play_list_info;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        // 让头部View获取焦点 置顶
        sheetDialog = new BottomSheetDialog(this);
        view.rlRoot.setFocusableInTouchMode(true);
        view.rlRoot.requestFocus();
        view.includeFragmentSinger.llLoading.setVisibility(View.GONE);
        adapter = new SingleAdapter();
        Intent intent = getIntent();
        playListID = intent.getStringExtra(Constants.Music.MUSIC_MENU_ACTIVITY_KEY_MUSIC_LIST_INFO);
        initView();
        initEvent();
        initData();
        RxBusManager.get().getMusics(4,0,"",playListID,adapter);
    }

    private void initData() {
        PlayList playList = PlayListManager.get().findPlayListByName(playListID);
        view.tvView.setText(playListID);
        if (playList != null){
            List<Music> musics = playList.getMusics();
            Music playMusic = MusicCacheManager.get().getMusic();
            for (Music music : musics){
                if (playMusic != null){
                    if (!(music.getName().equals(playMusic.getName()) && music.getSinger().equals(playMusic.getSinger()))){
                        music.setIsPlay(false);
                    }else {
                        music.setIsPlay(true);
                    }
                }
            }
            view.includeFragmentSinger.includeItemTitle.tvMusicCount.setText("(共"+musics.size() +"首)");
            adapter.addData(musics);
            MusicCacheManager.get().setNewQueueMusics(musics);
            view.includeFragmentSinger.singleContext.setAdapter(adapter);
        }
    }

    private void initEvent() {
        view.includeFragmentSinger.includeItemTitle.llLocate.setOnClickListener(this);

        adapter.setEasyItemListener(new OnEasyItemListener() {
            @Override
            public void onItemClick(int position) {
                MusicUtils.setOnClickMusic(position);
            }
        });

        view.tbBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter.setItemMenuClickLinstener(new SingleAdapter.onItemMenuClickListener() {
            @Override
            public void onItemMenuClick(View view, Music music, int position) {
                BottomSheetDialogUtils.show(MusicMenuActivity.this,sheetDialog,music,position);
            }
        });

        // 滑动事件
        view.sslView.setOnScrollChangedListener(new SlideScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int x, int y, int oldx, int oldy) {
                // 滑动间距  所占的高度比例
                float ratio = (float)Math.min(Math.max(y, 0), scrollHeight)/scrollHeight;
                // 设置 alpha 滑动数据
                // 滑动比例 0-1 而 alpha 要从 隐藏到显示 0 - 255
                float alpha = ratio * 255;
                int argb = Color.argb((int)alpha, newRed, newGreen, newBlue);
                view.tbBar.setBackgroundColor(argb);
                // 当滑动 滑动高度的三分之一的时候 就改变 title
                if (y > scrollHeight / 3){
                    view.tbBar.setTitle(playListID);
                }else {
                    view.tbBar.setTitle("歌单");
                }

            }
        });
    }

    private void initView() {
        initToolbar();
        initRecyclerView();
        initPalette();
    }

    private void initPalette() {
        // 获取 歌单背景图 Bitmap
        PlayList playList = PlayListManager.get().findPlayListByName(playListID);
        String icon = playList.getIcon();
        GlideUtils.baseMusicState(view.ivIcon,icon,R.mipmap.icon_default);
        GlideHelp help = new GlideHelp();
        help.asBitMap(view.ivIcon, icon, new GlideHelp.OnAsBitmap() {
            @Override
            public void asBitmap(Bitmap bitmap) {
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@NonNull Palette palette) {
                        if (palette != null) {
                            Palette.Swatch vibrant = palette.getVibrantSwatch();//有活力的
                            Palette.Swatch vibrantDark = palette.getDarkVibrantSwatch();//有活力的，暗色
                            Palette.Swatch vibrantLight = palette.getLightVibrantSwatch();//有活力的，亮色
                            Palette.Swatch muted = palette.getMutedSwatch();//柔和的
                            Palette.Swatch mutedDark = palette.getDarkMutedSwatch();//柔和的，暗色
                            Palette.Swatch mutedLight = palette.getLightMutedSwatch();//柔和的,亮色
                            // 渐变颜色 从 vibrantLight（活力亮色） red（增幅 50 ） green（增幅 50 ） blue
                            int baseColor = 0;
                            if (vibrantLight != null){
                                baseColor = vibrantLight.getRgb();
                            }else if (vibrant != null){
                                baseColor = vibrant.getRgb();
                            }else if (mutedLight != null){
                                baseColor = mutedLight.getRgb();
                            }else if (muted != null){
                                baseColor = muted.getRgb();
                            }
                            int red = (baseColor & 0xff0000) >> 16;
                            int green = (baseColor & 0x00ff00) >> 8;
                            int blue = (baseColor & 0x0000ff);
                            // BaseColor
                            newRed = red > 200 ? 200 : red;
                            newGreen = green > 200 ? 200 : green;
                            newBlue = blue > 200 ? 200 : blue;


                            int increaseRed = red + 80 > 200 ? 200 : red + 80;
                            int increaseGreen = green + 80 > 200 ? 200 : green + 80;
                            int increaseBlue = blue + 80 > 200 ? 200 : blue + 80;

                            baseColor = Color.rgb(newRed, newGreen, newBlue);

                            int increaseColor = Color.rgb(increaseRed, increaseGreen, increaseBlue);
                            //int increaseColor = muted.getRgb();
                            colors.clear();
                            colors.add(increaseColor);
                            colors.add(baseColor);
                            show();
                        }
                    }
                });
            }
        });
    }

    private void show() {

        if (colors != null && colors.size() > 0) {
            int[] color = new int[]{colors.get(0), colors.get(colors.size() - 1)};
            /*Drawable drawable = ScrimUtil.makeCubicGradientScrimDrawable(color[0], 8, Gravity.TOP);
            view.rlRoot.setBackground(drawable);*/
            GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, color);
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                view.rlRoot.setBackgroundDrawable(bg);
            } else {
                view.rlRoot.setBackground(bg);
            }
        }
    }

    private void initToolbar() {
        // 让 toolbar 顶掉状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        // 1 .测量 toolbar 真实高度 及 上半部布局高度，
        view.tbBar.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.rlRoot.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 获取状态栏高度
        int barHeight = ScreenUtils.getStatusBarHeight();
        // 添加高度
        ViewGroup.LayoutParams rootViewParams = view.rlRoot.getLayoutParams();
        rootViewParams.height += barHeight;
        ViewGroup.LayoutParams toolbarParams = view.tbBar.getLayoutParams();
        toolbarParams.height += barHeight;
        // 设置 两者 View padding 高度
        view.tbBar.setPadding(
                view.tbBar.getPaddingLeft(),
                view.tbBar.getPaddingTop() + barHeight,
                view.tbBar.getPaddingRight(),
                view.tbBar.getPaddingBottom());

        view.rlRoot.setPadding(
                view.rlRoot.getPaddingLeft(),
                view.rlRoot.getPaddingTop() + barHeight,
                view.rlRoot.getPaddingRight(),
                view.rlRoot.getPaddingBottom());

        // 获取滑动高度  = 父布局高度 - Toolbar 高度
        scrollHeight = rootViewParams.height - toolbarParams.height;
    }

    private void initRecyclerView() {
        manager = new ScrollLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // 禁止 recycleView 滑动 防止与 ScrollView 冲突
        manager.setScrollEnabled(false);
        view.includeFragmentSinger.singleContext.setLayoutManager(manager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_locate:
                int position = AudioPlayerManager.get().getQueuePosition();
                if (position < 0) {
                    return;
                }
                RecyclerViewUtils.MoveToPosition(manager, position);
                break;
        }
    }
}
