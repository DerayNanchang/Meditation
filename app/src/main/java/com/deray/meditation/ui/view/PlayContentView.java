package com.deray.meditation.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.deray.meditation.R;

public class PlayContentView extends View {

    private Paint paint;

    public PlayContentView(Context context) {
        this(context,null,0);
    }

    public PlayContentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PlayContentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initPoint();
    }

    private void initPoint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        paint.setMaskFilter(new BlurMaskFilter(100,BlurMaskFilter.Blur.INNER));
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_default);
        canvas.drawBitmap(bitmap,0,0,paint);*/
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_default);
        Bitmap extractAlpha = bitmap.extractAlpha();
        paint.setMaskFilter(new BlurMaskFilter(30, BlurMaskFilter.Blur.NORMAL));
        canvas.drawBitmap(bitmap,0,0,paint);
        //canvas.drawBitmap(extractAlpha,0,0,paint);

    }

}
