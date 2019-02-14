package com.deray.meditation.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.deray.meditation.R;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.db.bean.PlayList;
import com.deray.meditation.db.manager.music.MusicManager;
import com.deray.meditation.db.manager.playlist.PlayListManager;

/**
 * Created by Administrator on 2018/9/21.
 */

public class AlertDialogManager {
    private AlertDialogManager() {
    }

    private static class GetInstance{
        private static AlertDialogManager manager = new AlertDialogManager();
    }

    public static AlertDialogManager get(){
        return GetInstance.manager;
    }

    private interface OnDialogCallBack{
        void onDialogCallBack();
    }



    public void deletePlayList(Context context, String title, String msg, final OnDialogCallBack onDialogCallBack){
        // 删除歌单
        new AlertDialog.Builder(context)
                .setTitle("温馨提示")
                .setMessage("确定要删除歌单吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onDialogCallBack.onDialogCallBack();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public void deleteMusic(Context context, final Music music){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_delete_view, null, false);
        final CheckBox cb_view = view.findViewById(R.id.cb_view);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        TextView tv_agree = view.findViewById(R.id.tv_agree);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .show();

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tv_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = cb_view.isChecked();
                MusicManager.get().deleteMusic(checked,music);
                dialog.dismiss();
            }
        });
    }

    public void showMusicInfo(Context context,Music music){
        new AlertDialog.Builder(context)
                .setTitle("歌曲详情")
                .setMessage("歌曲名称:"+music.getName()+"\n" + "歌手名称:"+music.getSinger()+"\n"+"专辑名称:"+music.getAlbum())
                .setPositiveButton("确定", null)
                .show();
    }

    public void onCreateMusicList(final Context context) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_view_create_music_list, null, false);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view);

        final TextView submit = view.findViewById(R.id.tv_create_submit);
        final EditText content = view.findViewById(R.id.et_create_music_list);
        final TextView size = view.findViewById(R.id.et_create_music_list_size);
        final TextView cancel = view.findViewById(R.id.tv_create_cancel);
        final AlertDialog dialog = builder.show();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 插入数据库
                if (content != null && content.length() > 0) {
                    // 判断是否已经初始化了
                    createPlayList(content.getText().toString().trim());
                    dialog.dismiss();
                }
            }
        });


        // 一定要放在 onClick 之后调用否则不起作用
        submit.setClickable(false);

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 计算数值长度 显示亮度
                if (s != null && s.length() != 0) {
                    submit.setClickable(true);
                    submit.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    size.setTextColor(context.getResources().getColor(R.color.black));
                    size.setText(s.length() + "/40");
                } else {
                    submit.setTextColor(context.getResources().getColor(R.color.themeColor88));
                    size.setTextColor(context.getResources().getColor(R.color.gray88));
                    submit.setClickable(false);
                    size.setText(0 + "/40");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 取消
                dialog.dismiss();
            }
        });
    }

    /**
     * 创建歌单
     *
     * @param playName
     */
    private void createPlayList(String playName) {
        PlayList playlist = new PlayList();
        playlist.setName(playName);
        playlist.setCreateTime(System.currentTimeMillis());
        PlayListManager.get().insertPlayList(playlist);
    }
}
