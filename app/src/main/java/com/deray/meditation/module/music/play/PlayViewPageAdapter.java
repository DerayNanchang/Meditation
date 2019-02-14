package com.deray.meditation.module.music.play;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deray.meditation.R;
import com.deray.meditation.cache.MusicCacheManager;
import com.deray.meditation.cache.base.CacheManager;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.ui.view.RotateMusicDiskView;
import com.deray.meditation.utils.MusicUtils;

import java.util.ArrayList;
import java.util.List;

public class PlayViewPageAdapter extends PagerAdapter {
    private Context context;
    private List<Music> musics;
    private RotateMusicDiskView rmdvView;
    private View view;
    private RotateMusicDiskView diskView;
    private int state;
    public static final int START = 1;
    public static final int STOP = 2;
    private View musicDisk;
    private OnItemListener onItemListener;

    private List<View> aldView = new ArrayList<>();

    @Override
    public int getCount() {
        return musics.size();
    }

    public PlayViewPageAdapter(List<Music> bitmaps, Context context) {
        this.musics = bitmaps;
        this.context = context;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String key = MusicCacheManager.get().setKey(musics.get(position).getMusicID());
        Bitmap bitmap = CacheManager.get(key);
        if (bitmap == null) {
            bitmap = MusicUtils.getPlayBitmap(context, musics.get(position).getMusicID(), musics.get(position).getAlbumID());
            CacheManager.put(MusicCacheManager.get().setKey(musics.get(position).getMusicID()), bitmap);
        }
        if (musicDisk != null){
            aldView.add(musicDisk);
        }
        musicDisk = LayoutInflater.from(context).inflate(R.layout.view_rotate_play, null, false);
        rmdvView = musicDisk.findViewById(R.id.rmdv_view);
        rmdvView.setImage(bitmap);
        musicDisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemListener.onClick();
            }
        });
        container.addView(musicDisk);
        return musicDisk;
    }

    public interface OnItemListener{
        void onClick();
    }

    public void setOnItemListener(OnItemListener onItemListener){
        this.onItemListener = onItemListener;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {


        //container.removeView((View) object);
        /*if (aldView != null  && aldView.size() > 0){
            container.removeView(aldView.get(0));
            aldView.remove(0);
        }else {
            container.removeView((View) object);
        }*/
    }


    public RotateMusicDiskView getRotateView() {
        return rmdvView;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        view = (View) object;
        diskView = view.findViewById(R.id.rmdv_view);
        switch (state) {
            case START:
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        diskView.startAnimotion();
                        view.requestLayout();
                    }
                });


                break;
            case STOP:
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        diskView.stopAnimotion();
                        view.requestLayout();
                    }
                });
                break;
        }
        super.setPrimaryItem(container, position, object);
    }

    public void setState(int state) {
        this.state = state;
    }
}
