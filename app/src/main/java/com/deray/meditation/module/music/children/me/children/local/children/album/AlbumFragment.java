package com.deray.meditation.module.music.children.me.children.local.children.album;

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
import com.deray.meditation.module.music.children.me.children.local.children.album.adapter.AlbumAdapter;
import com.deray.meditation.module.music.children.me.children.local.children.album.bean.Album;
import com.deray.meditation.module.music.info.MusicInfoActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AlbumFragment extends BaseFragment<FragmentSingleBinding> {
    private Set<List<Music>> rawAlbums;
    private AlbumAdapter adapter;
    private List<Album> albums;

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
        initSinger();
        initTitle();
        initEvent();
    }

    private void initEvent() {
        adapter.setEasyItemListener(new OnEasyItemListener() {
            @Override
            public void onItemClick(int position) {
                String album = albums.get(position).getAlbum();
                Intent intent = new Intent(getActivity(),MusicInfoActivity.class);
                intent.putExtra(Constants.Music.MUSIC_INFO_ACTIVITY_KEY_NAME,album);
                intent.putExtra(Constants.Music.MUSIC_INFO_ACTIVITY_KEY_TAG,Constants.Music.MUSIC_INFO_ACTIVITY_KEY_TAG_ALBUM);
                startActivity(intent);
            }
        });
    }

    private void initSinger() {
        rawAlbums = MusicManager.get().getAlbums();
        adapter = new AlbumAdapter();
        albums = new ArrayList<>();
        if (rawAlbums != null) {
            for (List<Music> rawSinger : rawAlbums) {
                Album album = new Album();
                for (int i = 0; i < rawSinger.size(); i++) {
                    album.setName(rawSinger.get(i).getName());
                    album.setSinger(rawSinger.get(i).getSinger());
                    album.setAlbum(rawSinger.get(i).getAlbum());
                    album.setIcon(rawSinger.get(i).getMusicIcon() + "");
                    album.setCont(i + 1);
                }
                albums.add(album);
            }
        }
        adapter.addData(albums);
        view.singleContext.setAdapter(adapter);
    }

    private void initTitle() {
        view.includeItemTitle.tvItemTitleName.setText("全部专辑");
        if (rawAlbums != null) {
            view.includeItemTitle.tvMusicCount.setText("(共" + rawAlbums.size() + "个)");
        }
        view.includeItemTitle.llRightRoot.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

    }
}
