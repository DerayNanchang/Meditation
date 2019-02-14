package com.deray.meditation.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Chris on 2018/6/6.
 */

public class SlideScrollView extends ScrollView {
    private OnScrollChangedListener onScrollChangedListener;

    public SlideScrollView(Context context) {
        this(context, null);
    }

    public SlideScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        // 滑动监听
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(x, y, oldx, oldy);
        }
    }


    public interface OnScrollChangedListener {
        void onScrollChanged(int x, int y, int oldx, int oldy);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }

}
