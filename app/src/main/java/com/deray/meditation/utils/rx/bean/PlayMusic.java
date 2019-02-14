package com.deray.meditation.utils.rx.bean;

import com.deray.meditation.manage.music.AudioPlayerManager;

/**
 * Created by Deray on 2018/3/29.
 */

public class PlayMusic {

    private String musicName;
    private String singer;
    private String musicLyric;
    private String icon;
    private boolean playStatus;
    private int progress;
    private String DurationStr;
    private int Duration;


    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }


    public String getDurationStr() {
        return DurationStr.trim();
    }

    public void setDurationStr(String durationStr) {
        DurationStr = durationStr;
    }

    public boolean isPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(boolean playStatus) {
        this.playStatus = playStatus;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicLyric() {
        return musicLyric;
    }

    public void setMusicLyric(String musicLyric) {
        this.musicLyric = musicLyric;
    }
}
