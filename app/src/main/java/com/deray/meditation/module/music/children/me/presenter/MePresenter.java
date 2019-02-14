package com.deray.meditation.module.music.children.me.presenter;

import com.deray.meditation.R;
import com.deray.meditation.db.manager.playlist.PlayListManager;
import com.deray.meditation.module.music.children.me.bean.PlayListTypeBean;
import com.deray.meditation.module.music.children.me.view.MeView;

import java.util.ArrayList;
import java.util.List;

public class MePresenter {
    private  MeView view;

    public MePresenter(MeView view) {
        this.view = view;
    }

    public List<PlayListTypeBean> initPlayLists(int size) {
        List<PlayListTypeBean> type = new ArrayList<>();
        PlayListTypeBean playListTypeBean1 = new PlayListTypeBean();
        playListTypeBean1.setIcon(R.drawable.svg_music_me_right);
        playListTypeBean1.setTitle("我的歌单");
        playListTypeBean1.setPlayListTotal("(" + size + ")");
        type.add(playListTypeBean1);
        return type;
    }

    public void deletePlayList(String nowClickName) {
        PlayListManager manager = PlayListManager.get();
        manager.deletePlayList(manager.findPlayListByName(nowClickName));
    }
}
