package com.deray.meditation.utils.rx;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.deray.meditation.R;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.http.OBTransformer;
import com.deray.meditation.module.music.children.me.children.local.children.single.adapter.SingleAdapter;
import com.deray.meditation.module.music.play.PlayViewPageAdapter;
import com.deray.meditation.ui.view.CircleProgressBarView;
import com.deray.meditation.utils.MusicUtils;
import com.deray.meditation.utils.glide.GlideUtils;
import com.deray.meditation.utils.rx.bean.PlayMusic;
import com.deray.meditation.utils.rx.bean.RxBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Created by Deray on 2018/3/27.
 */

public class RxBusManager {

    interface KEYS {
        int PLAY_MUSIC_PROGRESS = -1;
        int PLAY_MUSIC_INFO = 1;
        int MUSICS = 2;
        int MUSIC_STATE = 3;
        int SCAN_MUSIC = 4;
        int LYRIC = 5;
    }

    private RxBusManager() {
    }

    private static class getLocalRxBusManagerInstance {
        private static RxBusManager rxBusManager = new RxBusManager();
    }

    public static RxBusManager get() {
        return getLocalRxBusManagerInstance.rxBusManager;
    }


    /**
     *  更新实时歌曲信息
     * @param playMusic
     */
    public void setPlayMusicProgress(PlayMusic playMusic) {
        // 频繁更换头像 耗费资源，这里仅更新 进度数据
        RxBean<PlayMusic> rxBean = new RxBean<>();
        rxBean.setTag(KEYS.PLAY_MUSIC_PROGRESS);
        rxBean.setMessage(playMusic);
        RxBus.get().post(rxBean);
    }

    public void setPlayMusicInfo(PlayMusic playMusic){
        RxBean<PlayMusic> rxBean = new RxBean<>();
        rxBean.setTag(KEYS.PLAY_MUSIC_INFO);
        rxBean.setMessage(playMusic);
        RxBus.get().post(rxBean);
    }

    @SuppressLint("CheckResult")
    public void getPlayMusicInfo(final Context context, final TextView name, final TextView singer, final ImageView icon){
        RxBus.get().register(RxBean.class)
                .compose(OBTransformer.<RxBean>toMain())
                .subscribe(new Consumer<RxBean>() {
                    @Override
                    public void accept(RxBean rxBean) throws Exception {
                        if (rxBean.getTag() == KEYS.PLAY_MUSIC_INFO) {
                            PlayMusic playMusic = (PlayMusic) rxBean.getMessage();
                            //使用Glide展示头像
                            if (playMusic == null ){
                                return;
                            }
                            name.setText(playMusic.getMusicName());
                            singer.setText(playMusic.getSinger());
                            /*if (!playMusic.getIcon().equals(icon.getTag())){
                                GlideUtils.baseMusicState(context, icon, playMusic.getIcon(), R.mipmap.icon_default);
                                icon.setTag(playMusic.getIcon());
                            }*/
                            GlideUtils.baseMusicState(context, icon, playMusic.getIcon(), R.mipmap.icon_default);
                        }
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void getPlayMusicProgress2(final CircleProgressBarView playState) {
        RxBus.get().register(RxBean.class)
                .compose(OBTransformer.<RxBean>toMain())
                .subscribe(new Consumer<RxBean>() {
                    @Override
                    public void accept(RxBean rxBean) throws Exception {
                        if (rxBean.getTag() == KEYS.PLAY_MUSIC_PROGRESS) {
                            PlayMusic playMusic = (PlayMusic) rxBean.getMessage();
                            if (playMusic.getDuration() != 0){
                                playState.setMax(playMusic.getDuration());
                                playState.setProgress(playMusic.getProgress());
                            }
                        }
                    }
                });
    }


    @SuppressLint("CheckResult")
    public void getPlayMusicInfo(final Toolbar toolbar, final ImageView playState, final TextView textView) {
        RxBus.get().register(RxBean.class)
                .compose(OBTransformer.<RxBean>toMain())
                .subscribe(new Consumer<RxBean>() {
                    @Override
                    public void accept(RxBean rxBean) throws Exception {
                        if (rxBean.getTag() == KEYS.PLAY_MUSIC_INFO) {
                            PlayMusic playMusic = (PlayMusic) rxBean.getMessage();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                toolbar.setTitle(TextUtils.isEmpty(playMusic.getMusicName()) ? "暂无歌曲" : playMusic.getMusicName());
                                toolbar.setSubtitle(TextUtils.isEmpty(playMusic.getSinger()) ? "暂无歌手" : playMusic.getSinger());
                            }
                            if (playMusic.isPlayStatus()) {
                                playState.setImageResource(R.drawable.svg_play_3);
                            } else {
                                playState.setImageResource(R.drawable.svg_pause_3);
                            }
                            if (!TextUtils.isEmpty(playMusic.getDurationStr()) || !playMusic.getDurationStr().equals("00:00")){
                                textView.setText(playMusic.getDurationStr());
                            }
                        }
                    }
                });
    }


    /**
     *   更新播放音乐的状态
     * @param musics
     */
    public void setMusics(Music musics){
        RxBean<Music> rxBean = new RxBean<>();
        rxBean.setTag(KEYS.MUSICS);
        rxBean.setMessage(musics);
        RxBus.get().post(rxBean);
    }

    @SuppressLint("CheckResult")
    public void getMusics(final int tag, final int type, final String name, final String musicMenuID, final SingleAdapter adapter){
        RxBus.get().register(RxBean.class)
                .compose(OBTransformer.<RxBean>toMain())
                .subscribe(new Consumer<RxBean>() {
                    @Override
                    public void accept(RxBean rxBean) throws Exception {
                        if (rxBean.getTag() == KEYS.MUSICS) {
                            Music playMusic = (Music) rxBean.getMessage();
                            List<Music> musics = new ArrayList<>();
                            if (tag == 1){
                                // 本地音乐
                                musics = MusicUtils.initLocalMusic();
                            }else if (tag == 2){
                                // 歌手 or 专辑
                                musics = MusicUtils.initSingerOrAlbumMusic(type, name);
                            }else if (tag == 3){
                                // 历史
                                musics = MusicUtils.initHistory();
                            }else if (tag == 4){
                                // 歌单
                                musics = MusicUtils.initPlayListMusics(musicMenuID);
                            }
                            for (Music music : musics){
                                if (music.getName().equals(playMusic.getName()) && music.getSinger().equals(playMusic.getSinger())){
                                    music.setIsPlay(true);
                                }else {
                                    music.setIsPlay(false);
                                }
                            }
                            adapter.addData(musics);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }


    public void setMusicPlayState(Boolean state){
        RxBean<Boolean> rxBean = new RxBean<>();
        rxBean.setTag(KEYS.MUSIC_STATE);
        rxBean.setMessage(state);
        RxBus.get().post(rxBean);
    }


    @SuppressLint("CheckResult")
    public void getMusicPlayState(final ImageView playState, final PlayViewPageAdapter pageAdapter){
        RxBus.get().register(RxBean.class)
                .compose(OBTransformer.<RxBean>toMain())
                .subscribe(new Consumer<RxBean>() {
                    @Override
                    public void accept(RxBean rxBean) throws Exception {
                        if (rxBean.getTag() == KEYS.MUSIC_STATE) {
                            Boolean isPlay = (Boolean) rxBean.getMessage();
                            if (isPlay) {
                                playState.setImageResource(R.drawable.svg_play_3);
                                if (pageAdapter != null){
                                    pageAdapter.setState(PlayViewPageAdapter.START);
                                }
                            } else {
                                playState.setImageResource(R.drawable.svg_pause_3);
                                if (pageAdapter != null){
                                    pageAdapter.setState(PlayViewPageAdapter.STOP);
                                }
                            }
                        }
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void getMusicPlayState(final CircleProgressBarView playState){
        RxBus.get().register(RxBean.class)
                .compose(OBTransformer.<RxBean>toMain())
                .subscribe(new Consumer<RxBean>() {
                    @Override
                    public void accept(RxBean rxBean) throws Exception {
                        if (rxBean.getTag() == KEYS.MUSIC_STATE) {
                            Boolean isPlay = (Boolean) rxBean.getMessage();
                            if (isPlay) {
                                playState.setPlay(true);
                            } else {
                                playState.setPlay(false);
                            }
                        }
                    }
                });
    }


    public void setScanLocalMusics(List<Music> musics){
        if (musics == null) {
            musics = new ArrayList<>();
        }
        RxBean<List<Music>> rxBean = new RxBean<>();
        rxBean.setTag(KEYS.SCAN_MUSIC);
        rxBean.setMessage(musics);
        RxBus.get().post(rxBean);
    }

    @SuppressLint("CheckResult")
    public void getScanLocalMusics(final SingleAdapter adapter, final TextView textView){
        RxBus.get().register(RxBean.class)
                .compose(OBTransformer.<RxBean>toMain())
                .subscribe(new Consumer<RxBean>() {
                    @Override
                    public void accept(RxBean rxBean) throws Exception {
                        if (rxBean.getTag() == KEYS.SCAN_MUSIC) {
                            List<Music> newMusics = (List<Music>) rxBean.getMessage();
                            List<Music> musics = MusicUtils.setNewMusics(newMusics);
                            adapter.addData(musics);
                            textView.setText(musics == null ? "(共" + 0 + "首)" : "(共" + musics.size() + "首)");
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
