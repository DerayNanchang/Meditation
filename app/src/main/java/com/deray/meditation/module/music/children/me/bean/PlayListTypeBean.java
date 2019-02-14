package com.deray.meditation.module.music.children.me.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chris on 2018/1/12.
 */

public class PlayListTypeBean implements Parcelable {
    private int icon;
    private String title;
    private String playListTotal;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlayListTotal() {
        return playListTotal;
    }

    public void setPlayListTotal(String playListTotal) {
        this.playListTotal = playListTotal;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.icon);
        dest.writeString(this.title);
        dest.writeString(this.playListTotal);
    }

    public PlayListTypeBean() {
    }

    protected PlayListTypeBean(Parcel in) {
        this.icon = in.readInt();
        this.title = in.readString();
        this.playListTotal = in.readString();
    }

    public static final Parcelable.Creator<PlayListTypeBean> CREATOR = new Parcelable.Creator<PlayListTypeBean>() {
        @Override
        public PlayListTypeBean createFromParcel(Parcel source) {
            return new PlayListTypeBean(source);
        }

        @Override
        public PlayListTypeBean[] newArray(int size) {
            return new PlayListTypeBean[size];
        }
    };
}
