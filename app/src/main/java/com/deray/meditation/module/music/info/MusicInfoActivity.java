package com.deray.meditation.module.music.info;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseActivity;
import com.deray.meditation.base.OnEasyItemListener;
import com.deray.meditation.cache.MusicCacheManager;
import com.deray.meditation.config.Constants;
import com.deray.meditation.databinding.ActivityMusicInfoBinding;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.manage.music.AudioPlayerManager;
import com.deray.meditation.module.music.children.me.children.local.children.single.adapter.SingleAdapter;
import com.deray.meditation.ui.dialog.BottomSheetDialogUtils;
import com.deray.meditation.utils.MusicUtils;
import com.deray.meditation.utils.RecyclerViewUtils;
import com.deray.meditation.utils.rx.RxBusManager;

import java.util.List;

public class MusicInfoActivity extends BaseActivity<ActivityMusicInfoBinding> {
    public static final int TAG_ALBUM = 2;
    private LinearLayoutManager manager;
    private SingleAdapter adapter;
    private BottomSheetDialog sheetDialog;

    @Override
    protected boolean isAddMusicLayout() {
        return true;
    }

    @Override
    protected int getLayoutContentViewID() {
        return R.layout.activity_music_info;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        sheetDialog = new BottomSheetDialog(this);
        setAlpha(view.includeFragmentSinger.llLoading, 1, 0);
        view.includeFragmentSinger.llLoading.setVisibility(View.GONE);
        Intent intent = getIntent();
        String name = intent.getStringExtra(Constants.Music.MUSIC_INFO_ACTIVITY_KEY_NAME);
        int tag = intent.getIntExtra(Constants.Music.MUSIC_INFO_ACTIVITY_KEY_TAG, 0);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        view.includeFragmentSinger.singleContext.setLayoutManager(manager);
        adapter = new SingleAdapter();
        List<Music> musics = MusicUtils.initSingerOrAlbumMusic(tag, name);
        if (musics.size() > 0) {
            view.tbLocalToolbar.setTitle(name);
        }
        MusicCacheManager.get().setNewQueueMusics(musics);
        view.includeFragmentSinger.includeItemTitle.tvMusicCount.setText("(共" + musics.size() + "首)");
        adapter.addData(musics);
        view.includeFragmentSinger.singleContext.setAdapter(adapter);
        initEvent();
        RxBusManager.get().getMusics(2,tag,name,"",adapter);
    }

    private void initEvent() {
        view.includeFragmentSinger.includeItemTitle.llLocate.setOnClickListener(this);

        adapter.setEasyItemListener(new OnEasyItemListener() {
            @Override
            public void onItemClick(int position) {
                MusicUtils.setOnClickMusic(position);
            }
        });

        adapter.setItemMenuClickLinstener(new SingleAdapter.onItemMenuClickListener() {
            @Override
            public void onItemMenuClick(View view, Music music, int position) {
                BottomSheetDialogUtils.show(MusicInfoActivity.this,sheetDialog,music,position);
            }
        });

        view.tbLocalToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
