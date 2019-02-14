package com.deray.meditation.base;

import android.view.View;

/**
 * Created by Chris on 2018/3/24.
 */

public interface OnItemListener<T> {
    void onItemClick(View view, T data, int position);
}
