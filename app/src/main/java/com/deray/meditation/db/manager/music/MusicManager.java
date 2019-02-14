package com.deray.meditation.db.manager.music;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.deray.meditation.cache.MusicCacheManager;
import com.deray.meditation.config.Constants;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.db.bean.PlayList;
import com.deray.meditation.db.bean.Search;
import com.deray.meditation.db.dao.MusicDao;
import com.deray.meditation.db.manager.base.DBManager;
import com.deray.meditation.db.manager.playlist.PlayListManager;
import com.deray.meditation.db.manager.search.SearchManager;
import com.deray.meditation.utils.MusicUtils;
import com.deray.meditation.utils.log.Toast;
import com.deray.meditation.utils.rx.RxBusManager;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MusicManager {
    private Context context;
    private OnScanLocalMusicsCallBack onScanLocalMusicsCallBack;


    public interface MusicType {
        int LOCAL = 0;
        int NET = 1;
    }

    private MusicManager() {
    }

    private static class GetInstance {
        private static MusicManager manager = new MusicManager();
    }

    public void init(Context context) {
        this.context = context;
    }

    public static MusicManager get() {
        return GetInstance.manager;
    }


    public void insertMusic(List<Music> musics) {

        for (Music music : musics){
            if (!isExist(music)){
                music.setMusicIcon(MusicUtils.getPlayMusicUri(music.getAlbumID()) + "");
                DBManager.get().getMusicDao().insert(music);
            }
        }
    }

    public boolean isExist(Music music){
        String key = setKey(music.getName(), music.getSinger());
        Music m = findMusicByKey(key);
        if (m == null){
            return false;
        }else {
            return true;
        }
    }




    public void updateMusic(Music music) {
        if (music == null) {
            return;
        }
        DBManager.get().getMusicDao().update(music);
    }

    public void updateMusicMenu(String musicMenuID, String icon) {
        PlayList playList = PlayListManager.get().findPlayListByName(musicMenuID);
        playList.setIcon(icon);
        DBManager.get().getPlayListDao().update(playList);
    }

    public List<Music> getMusics() {
        List<Music> musics = DBManager.get().getMusicDao().queryBuilder().build().list();
        if (musics == null) {
           return new ArrayList<>();
        } else {
            return musics;
        }
    }


    public List<String> getMusicSingers() {
        List<Music> musics = getMusics();
        List<String> singers = new ArrayList<>();
        if (musics == null || musics.size() == 0) {
            musics = getLocalMusic(null, null);
        }
        for (Music music : musics) {
            singers.add(music.getSinger());
        }
        return singers;
    }

    public List<String> getMusicSingers(List<Music> musics) {
        List<String> singers = new ArrayList<>();
        if (musics == null || musics.size() == 0) {
            musics = getLocalMusic(null, null);
        }
        for (Music music : musics) {
            singers.add(music.getSinger());
        }
        return singers;
    }

    public List<List<Music>> findMusicBySingers(List<String> singers) {
        List<List<Music>> singerMusics = new ArrayList<>();
        for (String singer : singers) {
            List<Music> musics = findMusicsBySinger(singer);
            singerMusics.add(musics);
        }
        return singerMusics;
    }

    public List<Music> findMusicsBySinger(String singer) {
        MusicDao dao = DBManager.get().getMusicDao();
        return dao.queryBuilder().where(MusicDao.Properties.Singer.eq(singer)).build().list();
    }


    public Set<List<Music>> getSingers() {
        List<String> singers = getMusicSingers();
        Set<List<Music>> singerMusics = new HashSet<>();
        for (String singer : singers) {
            List<Music> musics = findMusicsBySinger(singer);
            singerMusics.add(musics);
        }
        return singerMusics;
    }


    public List<String> getMusicAlbums() {
        List<Music> musics = getMusics();
        List<String> albums = new ArrayList<>();
        if (musics == null || musics.size() == 0) {
            musics = getLocalMusic(null, null);
        }
        for (Music music : musics) {
            albums.add(music.getAlbum());
        }
        return albums;
    }

    public List<Music> findMusicByAlbum(String album) {
        return DBManager.get().getMusicDao().queryBuilder().where(MusicDao.Properties.Album.eq(album)).build().list();
    }

    public List<Music> findMusicByPlayListID(String playListID) {
        return DBManager.get().getMusicDao().queryBuilder().where(MusicDao.Properties.Playlist.eq(playListID)).build().list();
    }


    public void deleteMusic(boolean isDeleteLocalMusic, Music music) {
        // 删除数据库内数据
        DBManager.get().getMusicDao().delete(music);
        if (isDeleteLocalMusic) {
            deleteLocalMusic(music);
            Toast.get().show("本地歌曲" + music.getName() + "删除歌曲成功");
            return;
        }
        Toast.get().show("歌单内" + music.getName() + "已经移除");
    }

    public Set<List<Music>> getAlbums() {
        List<String> albums = getMusicAlbums();
        Set<List<Music>> singerMusics = new HashSet<>();
        for (String album : albums) {
            List<Music> musics = findMusicByAlbum(album);
            singerMusics.add(musics);
        }
        return singerMusics;
    }

    public List<Music> findMusicsBySearchs(String editable) {
        List<Search> searches = SearchManager.get().findSearchByEditable(editable);
        List<Music> musics = new ArrayList<>();
        for (Search search : searches) {
            switch (search.getType()) {
                case SearchManager.TYPES.MUSIC + "":
                    String musicKey = search.getMusicKey();
                    Music music = findMusicByKey(musicKey);
                    musics.add(music);
                    break;
            }
        }
        return musics;
    }

    public boolean isSame(Music music1, Music music2) {
        if (music1 != null && music2 != null) {
            if (music1.getName().equals(music2.getName()) && music1.getSinger().equals(music2.getSinger())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    public Music findMusicByKey(String key) {
        return DBManager.get().getMusicDao().queryBuilder().where(MusicDao.Properties.Key.eq(key)).build().unique();
    }

    public String setKey(String name, String singer) {
        return name + singer;
    }

    public int deleteLocalMusic(Music music) {
        String[] agrs = {music.getMusicID() + "", music.getAlbumID() + "", music.getArtistID() + ""};

        String[] musicInfoArr = new String[]{
                MediaStore.Audio.Media._ID,         // 歌曲 ID
                MediaStore.Audio.Media.ALBUM_ID,    // 专辑 ID
                MediaStore.Audio.Media.ARTIST_ID,   // 歌手 ID
        };
        return context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Constants.Manager.MUSIC_MANAGER_BY_ID, agrs);
    }

    /**
     * 设置铃声
     */
    public void setRingtone(Context context, Music music) {
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(music.getMusicFile());
        // 查询音乐文件在媒体库是否存在
        Cursor cursor = context.getContentResolver().query(uri, null,
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


    public interface OnScanLocalMusicsCallBack{
        void agree(List<Music> musics);
        void reject();
    }

    public void scanLocalMusics(FragmentActivity activity, final OnScanLocalMusicsCallBack onScanLocalMusicsCallBack){
        RxPermissions rxPermissions = new RxPermissions(activity);
        Disposable subscribe = rxPermissions.requestEach(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribeOn(Schedulers.io())
                .map(new Function<Permission, List<Music>>() {
                    @Override
                    public List<Music> apply(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 找到本地所有歌曲
                            List<Music> musics = MusicManager.get().getLocalMusic(null, null);
                            // 存到本地数据库中
                            MusicManager.get().insertMusic(musics);
                            MusicCacheManager.get().setCacheMusics(MusicManager.get().getMusics());
                            SearchManager.get().setMusicSearch(MusicManager.get().getMusics());
                            RxBusManager.get().setScanLocalMusics(MusicManager.get().getMusics());
                            return musics;
                        } else {
                            return null;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Music>>() {
                    @Override
                    public void accept(List<Music> musics) throws Exception {
                        if (musics == null){
                            Toast.get().show("读取权限尚未开启，请先开启");
                            onScanLocalMusicsCallBack.reject();
                        }else {
                            onScanLocalMusicsCallBack.agree(musics);
                        }
                    }
                });
    }
    public void scanLocalMusics(Fragment fragment, final OnScanLocalMusicsCallBack onScanLocalMusicsCallBack){
        RxPermissions rxPermissions = new RxPermissions(fragment);
        Disposable subscribe = rxPermissions.requestEach(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribeOn(Schedulers.io())
                .map(new Function<Permission, List<Music>>() {
                    @Override
                    public List<Music> apply(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 找到本地所有歌曲
                            List<Music> musics = MusicManager.get().getLocalMusic(null, null);
                            // 存到本地数据库中
                            MusicManager.get().insertMusic(musics);
                            MusicCacheManager.get().setCacheMusics(MusicManager.get().getMusics());
                            SearchManager.get().setMusicSearch(MusicManager.get().getMusics());
                            RxBusManager.get().setScanLocalMusics(MusicManager.get().getMusics());
                            return musics;
                        } else {
                            return null;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Music>>() {
                    @Override
                    public void accept(List<Music> musics) throws Exception {
                        if (musics == null){
                            Toast.get().show("读取权限尚未开启，请先开启");
                            onScanLocalMusicsCallBack.reject();
                        }else {
                            onScanLocalMusicsCallBack.agree(musics);
                        }
                    }
                });
    }

    public List<Music> getLocalMusic(String selection, String[] selectionArgs) {
        List<Music> musicInfo = new ArrayList<>();
        String[] musicInfoArr = new String[]{
                MediaStore.Audio.Media._ID,         // 歌曲 ID
                MediaStore.Audio.Media.ALBUM_ID,    // 专辑 ID
                MediaStore.Audio.Media.ARTIST_ID,   // 歌手 ID
                MediaStore.Audio.Media.IS_MUSIC,    // 是否音乐
                MediaStore.Audio.Media.TITLE,       // 歌曲名称
                MediaStore.Audio.Media.ARTIST,      // 歌手
                MediaStore.Audio.Media.ALBUM,       // 歌曲的专辑名
                MediaStore.Audio.Media.DURATION,    // 歌曲的时长
                MediaStore.Audio.Media.DATA,        // 歌曲文件的全路径名
                MediaStore.Audio.Media.SIZE,        // 歌曲大小
                MediaStore.Audio.Media.DATE_ADDED,
        };
        // 根据用户设置的最短时间过滤歌曲
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicInfoArr, selection, selectionArgs, MediaStore.Audio.Media.DATE_ADDED);

        while (cursor.moveToNext()) {

            if ((cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC)) == 0)) {
                continue;
            }
            Long musicID = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID));
            Long albumID = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            Long artistID = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST_ID));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
            Long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
            String musicFile = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            Long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.SIZE));
            String dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATE_ADDED));
            Long dateAddedL = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATE_ADDED));
            Music music = new Music();
            music.setType(MusicType.LOCAL);
            music.setMusicID(musicID);
            music.setAlbumID(albumID);
            music.setArtistID(artistID);
            music.setName(name);
            music.setSinger(singer);
            music.setAlbum(album);
            music.setDuration(duration);
            music.setMusicFile(musicFile);
            music.setKey(setKey(name, singer));
            music.setMusicSize(size);
            music.setDateAdd(dateAdded);
            music.setDateAddL(dateAddedL);
            musicInfo.add(music);
        }
        cursor.close();

        // 自己实现倒叙
        Collections.sort(musicInfo, new Comparator<Music>() {
            @Override
            public int compare(Music o1, Music o2) {
                // 小于返回 负数 等于返回1 大于返回正数
                if (o2.getDateAddL() > o1.getDateAddL()) {
                    return 1;

                } else if (o1.getDateAddL() == o2.getDateAddL()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        return musicInfo;
    }
}
