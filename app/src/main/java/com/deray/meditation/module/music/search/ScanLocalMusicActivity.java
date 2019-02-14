package com.deray.meditation.module.music.search;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseActivity;
import com.deray.meditation.databinding.ActivityScanMusicBinding;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.db.manager.music.MusicManager;

import java.util.List;

public class ScanLocalMusicActivity extends BaseActivity<ActivityScanMusicBinding> {
    @Override
    protected boolean isAddMusicLayout() {
        return false;
    }

    @Override
    protected int getLayoutContentViewID() {
        return R.layout.activity_scan_music;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        init();
    }

    private void init() {
        view.btnScanStartScan.setOnClickListener(this);
        view.btnScanFinish.setOnClickListener(this);
        view.tbSearchTitle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_scan_start_scan:
                view.rvAnim.start();
                scanLocalMusic();
                break;
            case R.id.btn_scan_finish:
                //startActivity(new Intent(this, LocalMusicActivity.class));
                finish();
                break;
        }
    }

    @SuppressLint("CheckResult")
    private void scanLocalMusic() {

        MusicManager.get().scanLocalMusics(this, new MusicManager.OnScanLocalMusicsCallBack() {
            @Override
            public void agree(List<Music> musics) {
                view.tvScanLocalMusicCount.setText("一共扫描到了" + musics.size() + "首歌~");
                view.rvAnim.stop();
            }

            @Override
            public void reject() {

            }
        });

        /*RxPermissions rxPermissions = new RxPermissions(this);
        Disposable subscribe = rxPermissions.requestEach(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribeOn(Schedulers.io())
                .map(new Function<Permission, Integer>() {
                    @Override
                    public Integer apply(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 找到本地所有歌曲
                            List<Music> musics = MusicManager.get().getLocalMusic(null, null);
                            // 存到本地数据库中
                            MusicManager.get().insertMusic(musics);
                            MusicCacheManager.get().setCacheMusics(MusicManager.get().getMusics());
                            SearchManager.get().setMusicSearch(MusicManager.get().getMusics());
                            RxBusManager.get().setScanLocalMusics(MusicManager.get().getMusics());
                            return musics.size();
                        } else {
                            return -10;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (integer == -10){
                            Toast.get().show("读取权限尚未开启，请先开启");
                        }else {
                            view.tvScanLocalMusicCount.setText("一共扫描到了" + integer.toString() + "首歌~");
                            view.rvAnim.stop();
                        }
                    }
                });*/
    }
}
