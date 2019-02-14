package com.deray.meditation.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.deray.meditation.R;
import com.deray.meditation.cache.MusicCacheManager;
import com.deray.meditation.config.Constants;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.db.bean.PlayList;
import com.deray.meditation.db.manager.music.MusicManager;
import com.deray.meditation.db.manager.playlist.PlayListManager;
import com.deray.meditation.manage.music.AudioPlayerManager;
import com.deray.meditation.utils.log.Toast;
import com.deray.meditation.utils.rx.RxBusManager;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 2018/2/5.
 */

public class MusicUtils {


    private static void setQueueMusic() {
        List<Music> newQueueMusics = MusicCacheManager.get().getNewQueueMusics();
        MusicCacheManager.get().setQueueMusics(newQueueMusics);
    }

    public static List<Music> initLocalMusic() {
        List<Music> musics = MusicCacheManager.get().getCacheMusics();
        return setNewMusics(musics);
    }

    public static String getLyricDir(){
        return getAppDir()+"/lyric";
    }

    public static String getAppDir(){
        return Environment.getExternalStorageDirectory()+"/meditation";
    }

    public static String getLyric(String name){
        return getLyricDir()+"/"+name+".lrc";
    }

    public static boolean lyricExist(String name){
        String lyric = getLyric(name);
        File file = new File(lyric);
        return file.exists();
    }


    public static List<Music> initHistory() {
        List<Music> musics = MusicCacheManager.get().getHistoryMusics();
        return setNewMusics(musics);
    }

    public static List<Music> setNewMusics(List<Music> musics){
        MusicCacheManager.get().setNewQueueMusics(musics);
        return MusicCacheManager.get().getNewQueueMusics();
    }

    public static List<Music> getReorganizeMusics(List<Music> musics, Music playMusic){
        if (musics == null){
            return new ArrayList<>();
        }
        if (playMusic == null){
            Music music = MusicCacheManager.get().getMusic();
            if (music != null){
                playMusic = music;
            }
        }
        for (Music music : musics){
            if (playMusic != null){
                if (!(music.getName().equals(playMusic.getName()) && music.getSinger().equals(playMusic.getSinger()))){
                    music.setIsPlay(false);
                }else {
                    music.setIsPlay(true);
                }
            }else {
                music.setIsPlay(false);
            }
        }
        return musics;
    }

    public static List<Music> getReorganizeLocalMusics(Fragment fragment){
        List<Music> musics = MusicUtils.initLocalMusic();
        return getReorganizeMusics(musics,null);
    }

    public static List<Music> initSingerOrAlbumMusic(int type,String name){
        // 歌手/专辑 音乐
        List<Music> musics = new ArrayList<>();
        switch (type) {
            case Constants.Music.MUSIC_INFO_ACTIVITY_KEY_TAG_SINGER:
                // 单曲
                musics = MusicManager.get().findMusicsBySinger(name);
                break;
            case Constants.Music.MUSIC_INFO_ACTIVITY_KEY_TAG_ALBUM:
                // 专辑
                musics = MusicManager.get().findMusicByAlbum(name);
                break;
        }
        return setNewMusics(musics);
    }


    public static List<Music> initPlayListMusics(String musicMenuID) {
        PlayList playList = PlayListManager.get().findPlayListByName(musicMenuID);
        if (playList != null){
            List<Music> musics = playList.getMusics();
            return setNewMusics(musics);
        }else {
            return new ArrayList<>();
        }
    }


    public static void setOnClickMusic(int position) {
        MusicUtils.setQueueMusic();
        AudioPlayerManager.get().setController(false);
        MusicCacheManager.get().setQueueMusicPosition(position);
        AudioPlayerManager.get().playPause();
        RxBusManager.get().setMusicPlayState(AudioPlayerManager.get().isPlay());
    }

    public static Bitmap getLocalMusicBitmap(Context context, long songid, long albumid) {
        Uri S_ARTWORK_URI = Uri.parse("content://media/external/audio/albumart");
        Bitmap bm = null;
        // 专辑id和歌曲id小于0说明没有专辑、歌曲，并抛出异常
        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException(
                    "Must specify an album or a song id");
        }
        try {
            if (albumid < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/"
                        + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    // 创建一个流
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(S_ARTWORK_URI, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);


                } else {
                    return null;
                }
            }
        } catch (FileNotFoundException ex) {
        }
        if (bm == null) {
            Resources resources = context.getResources();
            Drawable drawable = resources.getDrawable(R.mipmap.icon_default);
            //Drawable 转 Bitmap
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bm = bitmapDrawable.getBitmap();
        }
        return Bitmap.createScaledBitmap(bm, 150, 150, true);
    }


    public static Uri getPlayMusicUri(long albumid) {
        Uri S_ARTWORK_URI = Uri.parse("content://media/external/audio/albumart");
        // 专辑id和歌曲id小于0说明没有专辑、歌曲，并抛出异常
        if (albumid < 0) {
            throw new IllegalArgumentException(
                    "Must specify an album or a song id");
        }
        return ContentUris.withAppendedId(S_ARTWORK_URI, albumid);
    }


    public static Bitmap getPlayBitmap(Context context, long songid, long albumid) {
        Uri S_ARTWORK_URI = Uri.parse("content://media/external/audio/albumart");
        Bitmap bm = null;
        // 专辑id和歌曲id小于0说明没有专辑、歌曲，并抛出异常
        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException(
                    "Must specify an album or a song id");
        }
        try {
            if (albumid < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/"
                        + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(S_ARTWORK_URI, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);


                } else {
                    return null;
                }
            }
        } catch (FileNotFoundException ex) {
        }
        if (bm == null) {
            Resources resources = context.getResources();
            Drawable drawable = resources.getDrawable(R.mipmap.icon_default);
            //Drawable 转 Bitmap
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bm = bitmapDrawable.getBitmap();
        }
        return Bitmap.createScaledBitmap(bm, 350, 350, true);
    }


    /**
     * 设置铃声
     */
    private void setRingtone(Context context,Music music) {
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(music.getMusicFile());
        // 查询音乐文件在媒体库是否存在
        Cursor cursor =context.getContentResolver().query(uri, null,
                MediaStore.MediaColumns.DATA + "=?", new String[]{music.getMusicFile()}, null);
        if (cursor == null) {
            return;
        }
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            String _id = cursor.getString(0);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.IS_MUSIC, true);
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            values.put(MediaStore.Audio.Media.IS_ALARM, false);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
            values.put(MediaStore.Audio.Media.IS_PODCAST, false);

            context.getContentResolver().update(uri, values, MediaStore.MediaColumns.DATA + "=?",
                    new String[]{music.getMusicFile()});
            Uri newUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));
            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
            Toast.get().show("设置铃声成功");
        }
        cursor.close();
    }

    //设置--铃声的具体方法
    public static void setMyRingtone(Context context, String path) {
        File sdfile = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
        Uri newUri = context.getContentResolver().insert(uri, values);
        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
        Toast.get().show("设置来电铃声成功！");
    }

    //设置--提示音的具体实现方法
    public void setMyNotification(Context context, String path) {

        File sdfile = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
        Uri newUri = context.getContentResolver().insert(uri, values);

        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, newUri);
        Toast.get().show("设置通知铃声成功！");
    }

    //设置--闹铃音的具体实现方法
    public void setMyAlarm(Context context, String path) {
        File sdfile = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
        Uri newUri = context.getContentResolver().insert(uri, values);
        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM, newUri);
        Toast.get().show("设置闹钟铃声成功！");
    }


}
