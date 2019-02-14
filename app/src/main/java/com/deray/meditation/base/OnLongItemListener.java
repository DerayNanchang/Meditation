package com.deray.meditation.base;

import android.view.View;

/**
 * Created by Chris on 2018/3/24.
 */

public interface OnLongItemListener<T> {
    void onLongItemClick(View view, T data, int position);
}
