package com.deray.meditation.utils.rx.bean;

/**
 * Created by Deray on 2018/3/25.
 */

public class RxBean<T> {
    private int tag;
    private T message;

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }
}
