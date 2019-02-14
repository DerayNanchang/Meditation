package com.deray.meditation.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Created by Chris on 2018/1/26.
 */

public class EnhancedExpandableListView extends ExpandableListView {
    public EnhancedExpandableListView(Context context) {
        super(context);
    }

    public EnhancedExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EnhancedExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 为了适应 Scrollview
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }
}
