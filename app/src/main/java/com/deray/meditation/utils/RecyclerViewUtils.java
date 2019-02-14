package com.deray.meditation.utils;

import android.support.v7.widget.LinearLayoutManager;

public class RecyclerViewUtils {

    /**
     * RecyclerView 移动到当前位置，
     *
     * @param manager  设置RecyclerView对应的manager
     * @param n  要跳转的位置
     */
    public static void MoveToPosition(LinearLayoutManager manager, int n) {
        manager.scrollToPositionWithOffset(n, 0);
        manager.setStackFromEnd(true);
    }
}
