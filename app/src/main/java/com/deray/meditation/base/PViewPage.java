package com.deray.meditation.base;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Chris on 2018/5/8.
 */

public class PViewPage extends ViewPager {
    public PViewPage(Context context) {
        super(context);
    }

    public PViewPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {

        try {
            return super.onInterceptHoverEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        try {
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
