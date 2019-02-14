package com.deray.meditation.module.music.children.me.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chris on 2018/1/12.
 */

public class PlayListBean implements Parcelable {
    private String icon;
    private String name;
    private int downStatus;
    private String musicTotal;
    private String musicDownNum;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDownStatus() {
        return downStatus;
    }

    public void setDownStatus(int downStatus) {
        this.downStatus = downStatus;
    }

    public String getMusicTotal() {
        return musicTotal;
    }

    public void setMusicTotal(String musicTotal) {
        this.musicTotal = musicTotal;
    }

    public String getMusicDownNum() {
        return musicDownNum;
    }

    public void setMusicDownNum(String musicDownNum) {
        this.musicDownNum = musicDownNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.icon);
        dest.writeString(this.name);
        dest.writeInt(this.downStatus);
        dest.writeString(this.musicTotal);
        dest.writeString(this.musicDownNum);
    }

    public PlayListBean() {
    }

    protected PlayListBean(Parcel in) {
        this.icon = in.readString();
        this.name = in.readString();
        this.downStatus = in.readInt();
        this.musicTotal = in.readString();
        this.musicDownNum = in.readString();
    }

    public static final Parcelable.Creator<PlayListBean> CREATOR = new Parcelable.Creator<PlayListBean>() {
        @Override
        public PlayListBean createFromParcel(Parcel source) {
            return new PlayListBean(source);
        }

        @Override
        public PlayListBean[] newArray(int size) {
            return new PlayListBean[size];
        }
    };
}
