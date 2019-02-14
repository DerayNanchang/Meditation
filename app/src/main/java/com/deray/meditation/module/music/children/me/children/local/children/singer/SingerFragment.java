package com.deray.meditation.module.music.children.me.children.local.children.singer;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseFragment;
import com.deray.meditation.base.OnEasyItemListener;
import com.deray.meditation.config.Constants;
import com.deray.meditation.databinding.FragmentSingleBinding;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.db.manager.music.MusicManager;
import com.deray.meditation.module.music.children.me.children.local.children.singer.adapter.SingerAdapter;
import com.deray.meditation.module.music.children.me.children.local.children.singer.bean.Singer;
import com.deray.meditation.module.music.info.MusicInfoActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SingerFragment extends BaseFragment<FragmentSingleBinding> {

    private Set<List<Music>> rawSingers;
    private SingerAdapter adapter;
    private List<Singer> singers;

    @Override
    protected int getLayoutContentViewID() {
        return R.layout.fragment_single;
    }

    @Override
    protected void init() {
        setAlpha(view.llLoading,1,0);
        view.llLoading.setVisibility(View.GONE);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        view.singleContext.setLayoutManager(manager);
        initData();

    }

    private void initData() {
        rawSingers = MusicManager.get().getSingers();
        adapter = new SingerAdapter();
        initTitle();
        initSinger();
        initEvent();
    }

    private void initEvent() {
        adapter.setEasyItemListener(new OnEasyItemListener() {
            @Override
            public void onItemClick(int position) {
                String singer = singers.get(position).getSinger();
                Intent intent = new Intent(getActivity(),MusicInfoActivity.class);
                intent.putExtra(Constants.Music.MUSIC_INFO_ACTIVITY_KEY_NAME,singer);
                intent.putExtra(Constants.Music.MUSIC_INFO_ACTIVITY_KEY_TAG,Constants.Music.MUSIC_INFO_ACTIVITY_KEY_TAG_SINGER);
                startActivity(intent);
            }
        });
    }

    private void initTitle() {
        view.includeItemTitle.tvItemTitleName.setText("全部歌手");
        if (rawSingers != null) {
            view.includeItemTitle.tvMusicCount.setText("(共" + rawSingers.size() + "位)");
        }
        view.includeItemTitle.llRightRoot.setVisibility(View.GONE);
    }

    private void initSinger() {

        singers = new ArrayList<>();
        if (rawSingers != null) {
            for (List<Music> rawSinger : rawSingers) {
                Singer singer = new Singer();
                for (int i = 0; i < rawSinger.size(); i++) {
                    singer.setName(rawSinger.get(i).getName());
                    singer.setSinger(rawSinger.get(i).getSinger());
                    singer.setIcon(rawSinger.get(i).getMusicIcon() + "");
                    singer.setCont(i + 1);
                }
                singers.add(singer);
            }
        }
        adapter.addData(singers);
        view.singleContext.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

    }
}
