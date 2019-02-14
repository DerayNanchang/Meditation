package com.deray.meditation.cache;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.deray.meditation.cache.base.SP;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.manage.music.AudioPlayerManager;
import com.deray.meditation.module.music.Lyric;
import com.deray.meditation.utils.StringUtils;
import com.deray.meditation.utils.rx.bean.PlayMusic;

import java.util.ArrayList;
import java.util.List;

public class MusicCacheManager {

    private MusicCacheManager() {
    }


    interface KEYS {
        String MUSIC = "music";
        String MUSICS = "Musics";
        String WROUGHT_MUSICS = "WroughtMusics";
        String QUEUE = "queue";
        String POSITION = "position";
        String NEW = "NEW";
        String HISTORY = "HISTORY";
        String PLAY_MODE = "PLAY_MODE";
        String SERVICE_CRONTAB = "CRONTAB";
        String SERVICE_HEADSET = "HEADSET";
        String LYRIC = "lyric";
    }

    private static class getInstance {
        private static MusicCacheManager manager = new MusicCacheManager();
    }

    public static MusicCacheManager get() {
        return getInstance.manager;
    }


    public void setCacheMusic(Music music) {
        String playMusicStr = JSON.toJSONString(music);
        SP.get().putString(KEYS.MUSIC, playMusicStr);
    }


    public Music getMusic() {
        String musicStr = SP.get().getString(KEYS.MUSIC, "");
        return JSON.parseObject(musicStr, Music.class);
    }

    /**
     * 获取正在播放歌曲的信息
     *
     * @return
     */
    public PlayMusic getCachePlayMusic() {
        String musicStr = SP.get().getString(KEYS.MUSIC, "");
        Music music = JSON.parseObject(musicStr, Music.class);
        if (music != null) {
            PlayMusic playMusic = new PlayMusic();
            playMusic.setIcon(music.getMusicIcon() + "");
            playMusic.setMusicLyric("");
            playMusic.setMusicName(music.getName());
            playMusic.setSinger(music.getSinger());
            playMusic.setPlayStatus(AudioPlayerManager.get().isPlay());
            playMusic.setProgress(AudioPlayerManager.get().getCurrentPosition());
            playMusic.setDuration(music.getDuration().intValue());
            playMusic.setDurationStr(StringUtils.formatTime("mm:ss", music.getDuration()));
            playMusic.setPlayStatus(AudioPlayerManager.get().isPlay());
            return playMusic;
        }
        return null;
    }


    //*************************本地所有音乐数据*************************//

    /**
     * 获取所有的缓存音乐数据
     *
     * @return
     */
    public List<Music> getCacheMusics() {
        try {
            String musicsStr = SP.get().getString(KEYS.MUSICS, "");
            return JSON.parseArray(musicsStr, Music.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 保存手机内所有扫描的歌曲
     *
     * @param musics
     */
    public void setCacheMusics(List<Music> musics) {
        String musicStr = JSON.toJSONString(musics);
        SP.get().putString(KEYS.MUSICS, musicStr);
    }

    //********************************************************//


    //*************************随机播放音乐列表*************************//

    public List<Music> getWroughtMusics() {
        try {
            String musicsStr = SP.get().getString(KEYS.WROUGHT_MUSICS, "");
            return JSON.parseArray(musicsStr, Music.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void setCacheWroughtMusics(List<Music> musics) {
        String musicStr = JSON.toJSONString(musics);
        SP.get().putString(KEYS.WROUGHT_MUSICS, musicStr);
    }


    //********************************************************//


    //*************************顺序播放，单曲循环播放列表(正在播放的队列)*************************//

    public void setQueueMusics(List<Music> music) {
        String musicStr = JSON.toJSONString(music);
        SP.get().putString(KEYS.QUEUE, musicStr);
    }

    /**
     * 获取所有的缓存队列音乐数据
     *
     * @return
     */
    public List<Music> getQueueMusics() {
        try {
            String musicsStr = SP.get().getString(KEYS.QUEUE, "");
            return JSON.parseArray(musicsStr, Music.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    //********************************************************//


    //*************************顺序播放，单曲循环播放列表(最新的队列)*************************//


    public void setNewQueueMusics(List<Music> music) {
        String musicStr = JSON.toJSONString(music);
        SP.get().putString(KEYS.NEW, musicStr);
    }

    /**
     * 获取所有的缓存队列音乐数据
     *
     * @return
     */
    public List<Music> getNewQueueMusics() {
        try {
            String musicsStr = SP.get().getString(KEYS.NEW, "");
            return JSON.parseArray(musicsStr, Music.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    //********************************************************//


    //*************************播放队列 position *************************//

    public void setQueueMusicPosition(int positon) {
        SP.get().putInt(KEYS.POSITION, positon);
    }

    public int getQueueMusicPosition() {
        if (SP.get().getInt(KEYS.POSITION, 0) >= Integer.MAX_VALUE / 2) {
            //
        }
        return SP.get().getInt(KEYS.POSITION, 0);
    }


    //********************************************************//


    public void setHistoryMusic(Music music) {
        List<Music> musics = getHistoryMusics();
        List<Music> musics2 = new ArrayList<>();
        if (musics.size() >= 100) {
            musics.remove(musics.size() - 1);
        }
        musics.add(music);

        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            List<Test> unique = musics.stream().distinct().collect(Collectors.<Test>toList());
        }*/

        for (Music music1 : musics) {
            if (!musics2.contains(music1)) {
                musics2.add(0, music1);
            }
        }
        SP.get().putString(KEYS.HISTORY, JSON.toJSONString(musics2));
    }

    public void clearHistory() {
        List<Music> musics = getHistoryMusics();
        if (musics != null) {
            musics.clear();
        }
    }

    public List<Music> update(List<Music> musics, Music music) {
        for (Music updateMusic : musics) {
            if (music.getName().equals(updateMusic.getName()) && music.getSinger().equals(updateMusic.getSinger())) {
                updateMusic.setIsLike(music.getIsLike());
            }
        }
        return musics;
    }

    public void updateMusicState(Music music) {
        List<Music> newQueueMusics = getNewQueueMusics();
        List<Music> queueMusics = getQueueMusics();
        List<Music> localAllMusics = getCacheMusics();
        List<Music> historyMusics = getHistoryMusics();

        if (newQueueMusics != null) {
            setNewQueueMusics(update(newQueueMusics, music));
        }
        if (queueMusics != null) {
            setQueueMusics(update(queueMusics, music));
        }
        if (localAllMusics != null) {
            setCacheMusics(update(localAllMusics, music));
        }
        if (historyMusics != null) {
            SP.get().putString(KEYS.HISTORY, JSON.toJSONString(update(historyMusics, music)));
        }
    }

    public List<Music> getHistoryMusics() {
        String historyQueueStr = SP.get().getString(KEYS.HISTORY, "");
        if (TextUtils.isEmpty(historyQueueStr)) {
            return new ArrayList<>();
        }
        List<Music> musics = JSON.parseArray(historyQueueStr, Music.class);
        if (musics == null) {
            return new ArrayList<>();
        }
        return musics;
    }

    public String setKey(long id) {
        return id + "_big";
    }

    public void savePlayMode(String mode) {
        SP.get().putString(KEYS.PLAY_MODE, mode);
    }

    public String getPlayMode() {
        return SP.get().getString(KEYS.PLAY_MODE, AudioPlayerManager.get().playMode);
    }


    public void saveServiceCrontab(boolean crontab) {
        SP.get().putBoolean(KEYS.SERVICE_CRONTAB, crontab);
    }

    public boolean getServiceCrontab() {
        return SP.get().getBoolean(KEYS.SERVICE_CRONTAB, true);
    }

    public void saveServiceHeadSet(boolean headset) {
        SP.get().putBoolean(KEYS.SERVICE_HEADSET, headset);
    }

    public boolean getServiceHeadSet() {
        return SP.get().getBoolean(KEYS.SERVICE_HEADSET, true);
    }


    public void saveLyric(Lyric lyric) {
        SP.get().putString(KEYS.LYRIC, JSON.toJSONString(lyric));
    }

    public Lyric getLyric() {
        String lyricStr = SP.get().getString(KEYS.LYRIC, "");
        if (TextUtils.isEmpty(lyricStr)) {
            return null;
        } else {
            return JSON.parseObject(lyricStr, Lyric.class);
        }
    }
}
