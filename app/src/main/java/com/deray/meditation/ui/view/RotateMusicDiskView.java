package com.deray.meditation.ui.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.deray.meditation.R;
import com.deray.meditation.utils.ScreenUtils;


/**
 * Created by Chris on 2018/4/14.
 */

public class RotateMusicDiskView extends View {


    private static final int CHANGE_HEIGHT = 200;

    AnimatorSet animSet = new AnimatorSet();
    LinearInterpolator interpolator = new LinearInterpolator();
    Paint musicDiskAmbient = new Paint();
    private Bitmap bitmap;
    private float musicDiskSurroundingHeight;
    private int flag = 0;
    private float musicDiskInternalHeight;
    private ObjectAnimator rotate;

    public RotateMusicDiskView(Context context) {
        this(context, null);
    }

    public RotateMusicDiskView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateMusicDiskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        initPaint();
        initAnim();
    }

    private void initAnim() {
        rotate = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);
    }


    public void setImage(Bitmap bitmap) {
        this.bitmap = bitmap;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap == null) {
            return;
        }
        Bitmap musicDiskSurrounding = BitmapFactory.decodeResource(getResources(), R.mipmap.play_page_disc);
        Bitmap musicDiskInternal2 = resizeImage(bitmap, ScreenUtils.getScreenWidth() / 2, ScreenUtils.getScreenWidth() / 2);
        Bitmap musicDiskInternal = createCircleImage(musicDiskInternal2);
        if (flag == 0) {
            musicDiskSurroundingHeight = getPivotY() - (musicDiskSurrounding.getHeight() / 2) - CHANGE_HEIGHT;
            musicDiskInternalHeight = getPivotY() - (musicDiskInternal.getHeight() / 2) - CHANGE_HEIGHT;
        }
        // 中心点坐标
        canvas.drawBitmap(musicDiskSurrounding, (getPivotX() - (musicDiskSurrounding.getWidth() / 2)), musicDiskSurroundingHeight, musicDiskAmbient);
        canvas.drawBitmap(musicDiskInternal, (getPivotX() - (musicDiskInternal.getWidth() / 2)), musicDiskInternalHeight, musicDiskAmbient);
        if (flag == 0) {
            setPivotY(getPivotY() - CHANGE_HEIGHT);
        }
        flag = 1;
        rotate.setRepeatMode(ValueAnimator.RESTART);
        rotate.setRepeatCount(-1);
        animSet.setInterpolator(interpolator);
        animSet.play(rotate);
        animSet.setDuration(25000);
        animSet.start();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void startAnimotion() {
        if (animSet != null) {
            rotate.resume();
        }
    }

    
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void stopAnimotion() {
        if (animSet != null) {
            if (animSet.isRunning()){
                rotate.pause();
            }
        }
    }


    /**
     * 将图片放大或缩小到指定尺寸
     */
    public static Bitmap resizeImage(Bitmap source, int dstWidth, int dstHeight) {
        if (source == null) {
            return null;
        }
        return Bitmap.createScaledBitmap(source, dstWidth, dstHeight, true);
    }

    public void initPaint() {
        musicDiskAmbient.setColor(Color.RED);
        musicDiskAmbient.setStyle(Paint.Style.FILL);
        musicDiskAmbient.setStrokeWidth(10);
        musicDiskAmbient.setAntiAlias(true);


    }

    public Bitmap createCircleImage(Bitmap source) {
        if (source == null) {
            return null;
        }
        int length = Math.min(source.getWidth(), source.getHeight());
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(source.getWidth() / 2, source.getHeight() / 2, length / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }
}
