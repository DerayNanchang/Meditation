package com.deray.meditation.module.music;

import java.util.List;

public class Lyric {
    private String by;
    private List<LyricInfo> lyricInfoList;

    public static class LyricInfo{
        public LyricInfo() {
        }

        public LyricInfo(int startTime, String text) {
            this.startTime = startTime;
            this.text = text;
        }

        private int startTime;
        private String text;

        public int getStartTime() {
            return startTime;
        }

        public void setStartTime(int startTime) {
            this.startTime = startTime;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public List<LyricInfo> getLyricInfoList() {
        return lyricInfoList;
    }

    public void setLyricInfoList(List<LyricInfo> lyricInfoList) {
        this.lyricInfoList = lyricInfoList;
    }
}
