package com.deray.meditation.db.manager.playlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;

import com.deray.meditation.db.bean.Music;
import com.deray.meditation.db.bean.PlayList;
import com.deray.meditation.db.dao.PlayListDao;
import com.deray.meditation.db.manager.base.DBManager;
import com.deray.meditation.db.manager.music.MusicManager;
import com.deray.meditation.manage.view.DialogManager;
import com.deray.meditation.module.music.children.me.adapter.DialogMusicListAdapter;
import com.deray.meditation.module.music.children.me.bean.PlayListBean;
import com.deray.meditation.ui.dialog.AlertDialogManager;
import com.deray.meditation.utils.log.Toast;

import java.util.List;

/**
 * Created by Administrator on 2018/9/19.
 */

public class PlayListManager {
    private PlayListManager() {

    }

    private static class getInstance {
        private static PlayListManager manager = new PlayListManager();
    }

    public static PlayListManager get() {
        return getInstance.manager;
    }

    public void insertPlayList(PlayList playList) {
        DBManager.get().getPlayListDao().insert(playList);
    }

    public void deletePlayList(PlayList playList) {
        DBManager.get().getPlayListDao().delete(playList);
    }

    public List<PlayList> getAllPlayList() {
        return DBManager.get().getPlayListDao().queryBuilder().list();
    }

    public PlayList findPlayListByName(String name) {
        return DBManager.get().getPlayListDao().queryBuilder().where(PlayListDao.Properties.Name.eq(name)).unique();
    }



    public void insertMusic(String playListID, Music music) {
        PlayList playList = findPlayListByName(playListID);
        if (playList != null) {
            List<Music> musics = playList.getMusics();
            if (musics != null) {
                for (Music music2 : musics) {
                    if (MusicManager.get().isSame(music2,music)){
                        Toast.get().show("该歌单已经存在该歌曲");
                        return;
                    }
                }
            }
        }
        music.setPlaylist(playListID);
        MusicManager.get().updateMusic(music);
        System.out.println("图片数据源头：" + music.getMusicIcon());
        MusicManager.get().updateMusicMenu(playListID,music.getMusicIcon());
        Toast.get().show("已收藏至歌单");
    }

    public void addMenuPlay(final Activity activity, final Music playMusic) {
        String title = "选择歌单 ：";
        PlayListManager.get().clearCache();
        List<PlayList> playlists = PlayListManager.get().getAllPlayList();
        // 默认歌单
        DialogMusicListAdapter dialogMusicListAdapter = new DialogMusicListAdapter();
        final AlertDialog dialog = DialogManager.get().showDialog(activity, title, playlists, dialogMusicListAdapter);
        dialogMusicListAdapter.setOnMusicListItemListener(new DialogMusicListAdapter.onMusicListItemListener() {
            @Override
            public void onMusicListItemClick(View view, PlayListBean playListBean, int position) {
                if (position == 0){
                    // 创建歌单
                    AlertDialogManager.get().onCreateMusicList(activity);
                    dialog.dismiss();
                }else {
                    PlayListManager.get().insertMusic(playListBean.getName(), playMusic);
                    dialog.dismiss();
                }
            }
        });
    }

    public void clearCache(){
        DBManager.get().getPlayListDao().detachAll();
    }
}
