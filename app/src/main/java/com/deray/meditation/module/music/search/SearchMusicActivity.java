package com.deray.meditation.module.music.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.view.View;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseActivity;
import com.deray.meditation.databinding.ActivitySearchMusicBinding;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.db.manager.music.MusicManager;
import com.deray.meditation.module.music.children.me.children.local.children.single.adapter.SingleAdapter;
import com.ui.utils.views.SearchView;

import java.util.ArrayList;
import java.util.List;

public class SearchMusicActivity extends BaseActivity<ActivitySearchMusicBinding> {

    private List<Music> musics;
    private SingleAdapter adapter;

    @Override
    protected boolean isAddMusicLayout() {
        return true;
    }

    @Override
    protected int getLayoutContentViewID() {
        return R.layout.activity_search_music;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        adapter = new SingleAdapter();
        view.svView.setIconVisibility(false);
        view.svView.setHint("搜索本地歌曲");
        final LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        view.rvView.setLayoutManager(manager);
        musics = new ArrayList<>();
        view.svView.setOnChangeListener(new SearchView.OnChangeListener() {
            @Override
            public void onChangeText(Editable editable) {
                if (editable.length() > 0){
                    musics = MusicManager.get().findMusicsBySearchs(editable.toString());
                }else {
                    adapter.clear();
                }
                adapter.addData(musics);
                view.rvView.setAdapter(adapter);
            }
        });

        view.tbSearchTitle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {

    }
}
