package com.deray.meditation.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Objects;

@Entity
public class Music {

    /*    MediaStore.Audio.Media._ID,         // 歌曲 ID
    MediaStore.Audio.Media.ALBUM_ID,    // 专辑 ID
    MediaStore.Audio.Media.ARTIST_ID,   // 歌手 ID
    MediaStore.Audio.Media.IS_MUSIC,    // 是否音乐
    MediaStore.Audio.Media.TITLE,       // 歌曲名称
    MediaStore.Audio.Media.ARTIST,      // 歌手
    MediaStore.Audio.Media.ALBUM,       // 歌曲的专辑名
    MediaStore.Audio.Media.DURATION,    // 歌曲的时长
    MediaStore.Audio.Media.DATA,        // 歌曲文件的全路径名
    MediaStore.Audio.Media.SIZE,        // 歌曲大小*/
    @Id
    @Property(nameInDb = "key")
    private String key;

    @Property(nameInDb = "musicID")
    private Long musicID;

    @Property(nameInDb = "albumID")
    private Long albumID;

    @Property(nameInDb = "artistID")
    private Long artistID;

    @Property(nameInDb = "name")
    private String name;

    // 歌手（艺术家）
    @Property(nameInDb = "singer")
    private String singer;

    // 专辑
    @Property(nameInDb = "album")
    private String album;

    // 播放时间
    @Property(nameInDb = "duration")
    private Long duration;

    @Property(nameInDb = "musicFile")
    private String musicFile;

    // 歌曲大小
    @Property(nameInDb = "musicSize")
    private Long musicSize;

    @Property(nameInDb = "dateAdd")
    private String dateAdd;

    @Property(nameInDb = "dateAddL")
    private Long dateAddL;

    // 歌曲类型 网络 ，本地
    @Property(nameInDb = "type")
    private Integer type;
    // 网络歌曲图片
    @Property(nameInDb = "musicIcon")
    private String musicIcon;

    // 点击次数
    @Property(nameInDb = "clicks")
    private Long clicks;

    // 是否标记喜欢
    @Property(nameInDb = "isLike")
    private Boolean isLike;

    @Property(nameInDb = "isPlay")
    private boolean isPlay;

    @Property(nameInDb = "playlist")
    private String playlist;

    @Generated(hash = 521644359)
    public Music(String key, Long musicID, Long albumID, Long artistID, String name,
            String singer, String album, Long duration, String musicFile,
            Long musicSize, String dateAdd, Long dateAddL, Integer type,
            String musicIcon, Long clicks, Boolean isLike, boolean isPlay,
            String playlist) {
        this.key = key;
        this.musicID = musicID;
        this.albumID = albumID;
        this.artistID = artistID;
        this.name = name;
        this.singer = singer;
        this.album = album;
        this.duration = duration;
        this.musicFile = musicFile;
        this.musicSize = musicSize;
        this.dateAdd = dateAdd;
        this.dateAddL = dateAddL;
        this.type = type;
        this.musicIcon = musicIcon;
        this.clicks = clicks;
        this.isLike = isLike;
        this.isPlay = isPlay;
        this.playlist = playlist;
    }

    @Generated(hash = 1263212761)
    public Music() {
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getMusicID() {
        return this.musicID;
    }

    public void setMusicID(Long musicID) {
        this.musicID = musicID;
    }

    public Long getAlbumID() {
        return this.albumID;
    }

    public void setAlbumID(Long albumID) {
        this.albumID = albumID;
    }

    public Long getArtistID() {
        return this.artistID;
    }

    public void setArtistID(Long artistID) {
        this.artistID = artistID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return this.singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Long getDuration() {
        return this.duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getMusicFile() {
        return this.musicFile;
    }

    public void setMusicFile(String musicFile) {
        this.musicFile = musicFile;
    }

    public Long getMusicSize() {
        return this.musicSize;
    }

    public void setMusicSize(Long musicSize) {
        this.musicSize = musicSize;
    }

    public String getDateAdd() {
        return this.dateAdd;
    }

    public void setDateAdd(String dateAdd) {
        this.dateAdd = dateAdd;
    }

    public Long getDateAddL() {
        return this.dateAddL;
    }

    public void setDateAddL(Long dateAddL) {
        this.dateAddL = dateAddL;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMusicIcon() {
        return this.musicIcon;
    }

    public void setMusicIcon(String musicIcon) {
        this.musicIcon = musicIcon;
    }

    public Long getClicks() {
        return this.clicks;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }

    public Boolean getIsLike() {
        return this.isLike;
    }

    public void setIsLike(Boolean isLike) {
        this.isLike = isLike;
    }

    public boolean getIsPlay() {
        return this.isPlay;
    }

    public void setIsPlay(boolean isPlay) {
        this.isPlay = isPlay;
    }

    public String getPlaylist() {
        return this.playlist;
    }

    public void setPlaylist(String playlist) {
        this.playlist = playlist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Music)) return false;
        Music music = (Music) o;
        return isPlay == music.isPlay &&
                Objects.equals(getKey(), music.getKey()) &&
                Objects.equals(getMusicID(), music.getMusicID()) &&
                Objects.equals(getAlbumID(), music.getAlbumID()) &&
                Objects.equals(getArtistID(), music.getArtistID()) &&
                Objects.equals(getName(), music.getName()) &&
                Objects.equals(getSinger(), music.getSinger()) &&
                Objects.equals(getAlbum(), music.getAlbum()) &&
                Objects.equals(getDuration(), music.getDuration()) &&
                Objects.equals(getMusicFile(), music.getMusicFile()) &&
                Objects.equals(getMusicSize(), music.getMusicSize()) &&
                Objects.equals(getDateAdd(), music.getDateAdd()) &&
                Objects.equals(getDateAddL(), music.getDateAddL()) &&
                Objects.equals(getType(), music.getType()) &&
                Objects.equals(getMusicIcon(), music.getMusicIcon()) &&
                Objects.equals(getClicks(), music.getClicks()) &&
                Objects.equals(getIsLike(), music.getIsLike()) &&
                Objects.equals(getPlaylist(), music.getPlaylist());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getKey(), getMusicID(), getAlbumID(), getArtistID(), getName(), getSinger(), getAlbum(), getDuration(), getMusicFile(), getMusicSize(), getDateAdd(), getDateAddL(), getType(), getMusicIcon(), getClicks(), getIsLike(), isPlay, getPlaylist());
    }
}
