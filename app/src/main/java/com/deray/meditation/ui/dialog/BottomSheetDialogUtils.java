package com.deray.meditation.ui.dialog;

import android.app.Activity;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;

import com.deray.meditation.R;
import com.deray.meditation.cache.MusicCacheManager;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.db.manager.music.MusicManager;
import com.deray.meditation.db.manager.playlist.PlayListManager;
import com.deray.meditation.manage.view.DialogManager;
import com.deray.meditation.module.music.children.me.adapter.PlayListDialogAdapter;
import com.deray.meditation.module.music.children.me.bean.PlayListDialog;

import java.util.List;

/**
 * Created by Administrator on 2018/9/21.
 */

public class BottomSheetDialogUtils {

    public static void show(final Activity context, final BottomSheetDialog sheetDialog, final Music music, final int position2) {
        String[] names = new String[]{"下一首播放", "收藏到歌单", "设为铃声", "分享", "查看歌曲信息", "删除"};
        int[] icons = new int[]{R.drawable.svg_pause, R.drawable.svg_playlist, R.drawable.svg_ring, R.drawable.svg_shear, R.drawable.svg_music_info, R.drawable.svg_delete};
        PlayListDialogAdapter adapter = new PlayListDialogAdapter();
        String title = "歌曲 ：" + music.getName();
        DialogManager.get().showMenu(context, title, icons, names, adapter, sheetDialog);

        adapter.setItemMenuListener(new PlayListDialogAdapter.OnItemMenuClick() {
            @Override
            public void OnItemMenuClick(View v, PlayListDialog playListDialog, int position) {
                //"下一首播放", "收藏到歌单", "设为铃声", "分享", "查看歌曲信息", "删除"
                switch (position) {
                    case 0:
                        // 添加到下一曲
                        List<Music> musics = MusicCacheManager.get().getQueueMusics();
                        if (musics.size() == position2+1) {
                            musics.add(0, music);
                        } else {
                            musics.add(position2 + 1, music);
                        }
                        break;
                    case 1:
                        // 收藏到歌单
                        PlayListManager.get().addMenuPlay(context,music);
                        break;
                    case 2:
                        // 设为铃声
                        requestSetRingtone(context,music);
                        break;
                    case 3:
                        // 分享
                        break;
                    case 4:
                        // 查看歌曲信息
                        AlertDialogManager.get().showMusicInfo(context,music);
                        break;
                    case 5:
                        // 删除歌曲
                        AlertDialogManager.get().deleteMusic(context,music);
                        break;
                }
                sheetDialog.dismiss();
            }
        });
    }

    private static void requestSetRingtone(Activity context, final Music music) {
        MusicManager.get().setRingtone(context,music);
    }
}
