package com.deray.meditation.module.music.children.me.children.recent;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseActivity;
import com.deray.meditation.base.OnEasyItemListener;
import com.deray.meditation.cache.MusicCacheManager;
import com.deray.meditation.databinding.ActivityRecentMusicBinding;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.manage.music.AudioPlayerManager;
import com.deray.meditation.module.music.children.me.children.local.children.single.adapter.SingleAdapter;
import com.deray.meditation.utils.MusicUtils;
import com.deray.meditation.utils.RecyclerViewUtils;
import com.deray.meditation.utils.rx.RxBusManager;

import java.util.List;

public class RecentMusicActivity extends BaseActivity<ActivityRecentMusicBinding> {

    private SingleAdapter adapter;
    private List<Music> musics;
    private LinearLayoutManager manager;

    @Override
    protected boolean isAddMusicLayout() {
        return true;
    }

    @Override
    protected int getLayoutContentViewID() {
        return R.layout.activity_recent_music;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        setAlpha(view.includeFragmentSinger.llLoading,1,0);
        view.includeFragmentSinger.llLoading.setVisibility(View.GONE);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        view.includeFragmentSinger.singleContext.setLayoutManager(manager);
        adapter = new SingleAdapter();
        musics = MusicUtils.initHistory();
        view.includeFragmentSinger.includeItemTitle.llSingleMusicItemMenu.setVisibility(View.GONE);
        adapter.addData(musics);
        if (musics != null) {
            view.includeFragmentSinger.includeItemTitle.tvMusicCount.setText("(共" + musics.size() + "首)");
            view.includeFragmentSinger.singleContext.setAdapter(adapter);
        }
        initEvent();
        RxBusManager.get().getMusics(3,0,"","",adapter);
    }

    private void initEvent() {
        adapter.setEasyItemListener(new OnEasyItemListener() {
            @Override
            public void onItemClick(int position) {
                MusicUtils.setOnClickMusic(position);
            }
        });
        view.tbLocalToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        view.tvClear.setOnClickListener(this);
        view.includeFragmentSinger.includeItemTitle.llLocate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_clear:
                adapter.clear();
                musics.clear();
                MusicCacheManager.get().clearHistory();
                break;
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
