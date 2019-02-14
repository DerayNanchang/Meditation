package com.deray.meditation.manage.view;

import android.app.AlertDialog;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.deray.meditation.R;
import com.deray.meditation.db.bean.PlayList;
import com.deray.meditation.module.music.children.me.adapter.DialogMusicListAdapter;
import com.deray.meditation.module.music.children.me.adapter.PlayListDialogAdapter;
import com.deray.meditation.module.music.children.me.bean.PlayListBean;
import com.deray.meditation.module.music.children.me.bean.PlayListDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 2018/4/9.
 */

public class DialogManager {

    private DialogManager() {
    }

    private static class DialogManagerInstance {
        private static DialogManager dialogManager = new DialogManager();
    }

    public static DialogManager get() {
        return DialogManagerInstance.dialogManager;
    }

    public void showMenu(Context context, String title, int[] resIcons, String[] names, PlayListDialogAdapter adapter, BottomSheetDialog bottomSheetDialog) {

        View inflate = LayoutInflater.from(context).inflate(R.layout.music_list_menu, null, false);
        TextView tv_title = inflate.findViewById(R.id.tv_music_list_title);
        RecyclerView content = inflate.findViewById(R.id.rl_music_list_content);
        content.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        tv_title.setText(title);
        //PlayListDialogAdapter adapter = new PlayListDialogAdapter();
        //adapter.setOnItemListener(this);
        List<PlayListDialog> playListDialogs = new ArrayList<>();
        for (int i = 0; i < resIcons.length; i++) {
            PlayListDialog playListDialog = new PlayListDialog();
            playListDialog.setIconi(resIcons[i]);
            playListDialog.setText(names[i]);
            playListDialogs.add(playListDialog);
        }
        adapter.addData(playListDialogs);
        content.setAdapter(adapter);
        bottomSheetDialog.setContentView(inflate);
        bottomSheetDialog.show();
    }


    public AlertDialog showDialog(Context context, String title, List<PlayList> playlists, DialogMusicListAdapter adapter) {
        // 目前只有本地音乐
        List<PlayListBean> diyPlayListBeanList = new ArrayList<>();
        for (PlayList playlist : playlists) {
            PlayListBean playListBean = new PlayListBean();
            playListBean.setIcon(playlist.getIcon());
            playListBean.setName(playlist.getName());
            playListBean.setMusicTotal(playlist.getMusics().size() + "");
            playListBean.setMusicDownNum(playlist.getMusics().size() + "");
            diyPlayListBeanList.add(playListBean);
        }

        PlayListBean addPlayList = new PlayListBean();
        addPlayList.setIcon("");
        addPlayList.setName("创建歌单");
        diyPlayListBeanList.add(0,addPlayList);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View inflate = LayoutInflater.from(context).inflate(R.layout.music_list_menu, null, false);
        TextView tv_title = inflate.findViewById(R.id.tv_music_list_title);
        RecyclerView rv_content = inflate.findViewById(R.id.rl_music_list_content);
        rv_content.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        tv_title.setText(title);
        adapter.addData(diyPlayListBeanList);
        rv_content.setAdapter(adapter);
        builder.setView(inflate);
        AlertDialog dialog = builder.show();

        return dialog;
    }


/*    public static void simpleDialog(Context context, String title, String message, String agree,new) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(agree, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogAgree.onAgree();
                    }
                })
                .setNegativeButton("")
    }

    public void setOnDialogAgreel(onDialogAgree dialogAgree) {
        this.dialogAgree = dialogAgree;

    }

    public interface onDialogAgree {
        void onAgree(DialogInterface dialog);
    }*/
}
