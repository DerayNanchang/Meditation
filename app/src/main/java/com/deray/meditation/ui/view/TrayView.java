package com.deray.meditation.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.deray.meditation.R;


/**
 * Created by Chris on 2018/4/16.
 */

public class TrayView extends View {

    private Paint paint = new Paint();
    private int flag = 0;
    private static final float CHANGE_HEIGHT = 200;

    public TrayView(Context context) {
        this(context, null, 0);
    }

    public TrayView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap musicDiskSurrounding = BitmapFactory.decodeResource(getResources(), R.mipmap.play_page_disc);
        if (flag == 0) {
            setPivotY(getPivotY() - CHANGE_HEIGHT);
        }
        flag = 1;
        canvas.drawCircle(getPivotX(), getPivotY(), (musicDiskSurrounding.getHeight()/2 + 5), paint);
    }


    private void initPaint() {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0x33000000);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(10);
    }
}
