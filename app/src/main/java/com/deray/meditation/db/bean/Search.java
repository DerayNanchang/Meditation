package com.deray.meditation.db.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Search {
    // 搜索关键字的指标 Music  歌名 歌手 专辑
    // 类型 music / 视频 / 图书 等
    // 简体，繁体
    // 全拼 ，首字母

    // 主键
    @Id
    private String ID;          // 主键就= type + 歌名 + 歌手（音乐）  其他待定

    // 关键字（默认简体）
    private String search1;
    private String search2;
    private String search3;
    private String type;
    // 繁体
    private String traditionalSearch1;
    private String traditionalSearch2;
    private String traditionalSearch3;

    // 全拼
    private String completeSearch1;
    private String completeSearch2;
    private String completeSearch3;

    // 首字母
    private String initialSearch1;
    private String initialSearch2;
    private String initialSearch3;


    private String musicKey;


    @Generated(hash = 1186468498)
    public Search(String ID, String search1, String search2, String search3,
            String type, String traditionalSearch1, String traditionalSearch2,
            String traditionalSearch3, String completeSearch1,
            String completeSearch2, String completeSearch3, String initialSearch1,
            String initialSearch2, String initialSearch3, String musicKey) {
        this.ID = ID;
        this.search1 = search1;
        this.search2 = search2;
        this.search3 = search3;
        this.type = type;
        this.traditionalSearch1 = traditionalSearch1;
        this.traditionalSearch2 = traditionalSearch2;
        this.traditionalSearch3 = traditionalSearch3;
        this.completeSearch1 = completeSearch1;
        this.completeSearch2 = completeSearch2;
        this.completeSearch3 = completeSearch3;
        this.initialSearch1 = initialSearch1;
        this.initialSearch2 = initialSearch2;
        this.initialSearch3 = initialSearch3;
        this.musicKey = musicKey;
    }


    @Generated(hash = 1644193961)
    public Search() {
    }


    public String getID() {
        return this.ID;
    }


    public void setID(String ID) {
        this.ID = ID;
    }


    public String getSearch1() {
        return this.search1;
    }


    public void setSearch1(String search1) {
        this.search1 = search1;
    }


    public String getSearch2() {
        return this.search2;
    }


    public void setSearch2(String search2) {
        this.search2 = search2;
    }


    public String getSearch3() {
        return this.search3;
    }


    public void setSearch3(String search3) {
        this.search3 = search3;
    }


    public String getType() {
        return this.type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getTraditionalSearch1() {
        return this.traditionalSearch1;
    }


    public void setTraditionalSearch1(String traditionalSearch1) {
        this.traditionalSearch1 = traditionalSearch1;
    }


    public String getTraditionalSearch2() {
        return this.traditionalSearch2;
    }


    public void setTraditionalSearch2(String traditionalSearch2) {
        this.traditionalSearch2 = traditionalSearch2;
    }


    public String getTraditionalSearch3() {
        return this.traditionalSearch3;
    }


    public void setTraditionalSearch3(String traditionalSearch3) {
        this.traditionalSearch3 = traditionalSearch3;
    }


    public String getCompleteSearch1() {
        return this.completeSearch1;
    }


    public void setCompleteSearch1(String completeSearch1) {
        this.completeSearch1 = completeSearch1;
    }


    public String getCompleteSearch2() {
        return this.completeSearch2;
    }


    public void setCompleteSearch2(String completeSearch2) {
        this.completeSearch2 = completeSearch2;
    }


    public String getCompleteSearch3() {
        return this.completeSearch3;
    }


    public void setCompleteSearch3(String completeSearch3) {
        this.completeSearch3 = completeSearch3;
    }


    public String getInitialSearch1() {
        return this.initialSearch1;
    }


    public void setInitialSearch1(String initialSearch1) {
        this.initialSearch1 = initialSearch1;
    }


    public String getInitialSearch2() {
        return this.initialSearch2;
    }


    public void setInitialSearch2(String initialSearch2) {
        this.initialSearch2 = initialSearch2;
    }


    public String getInitialSearch3() {
        return this.initialSearch3;
    }


    public void setInitialSearch3(String initialSearch3) {
        this.initialSearch3 = initialSearch3;
    }


    public String getMusicKey() {
        return this.musicKey;
    }


    public void setMusicKey(String musicKey) {
        this.musicKey = musicKey;
    }

    
}
