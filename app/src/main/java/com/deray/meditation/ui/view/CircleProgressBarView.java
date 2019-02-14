package com.deray.meditation.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.deray.meditation.R;
import com.deray.meditation.utils.DensityUtil;

public class CircleProgressBarView extends View {

    private Paint circle;
    private Paint triangle;
    private Path path;
    private Paint parallel;
    private Paint outerRing;
    private int rawRingColor;
    private int processedRingColor;
    private int playSignColor;
    private int pauseSignColor;
    private float ringWidth;
    private float playSignHeight;
    private float playSignSpacing;
    private float pauseSignLength;
    private float pauseSignWidth;
    private float radius;
    private float progress;
    private float max;
    private boolean isPlay = false;
    private RectF rectF2;

    public CircleProgressBarView(Context context) {
        this(context,null,0);
    }

    public CircleProgressBarView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public CircleProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBarView);
        progress = typedArray.getFloat(R.styleable.CircleProgressBarView_progress, 0f);
        max = typedArray.getFloat(R.styleable.CircleProgressBarView_max, 100f);
        radius = typedArray.getDimension(R.styleable.CircleProgressBarView_radius, DensityUtil.dip2px(15));
        rawRingColor = typedArray.getColor(R.styleable.CircleProgressBarView_rawRingColor, context.getResources().getColor(R.color.lightBlack));
        processedRingColor = typedArray.getColor(R.styleable.CircleProgressBarView_processedRingColor, context.getResources().getColor(R.color.colorAccent));
        playSignColor = typedArray.getColor(R.styleable.CircleProgressBarView_playSignColor, context.getResources().getColor(R.color.colorAccent));
        pauseSignColor = typedArray.getColor(R.styleable.CircleProgressBarView_pauseSignColor, context.getResources().getColor(R.color.lightBlack));
        ringWidth = typedArray.getDimension(R.styleable.CircleProgressBarView_ringWidth, 5);
        playSignHeight = typedArray.getDimension(R.styleable.CircleProgressBarView_playSignHeight, DensityUtil.dip2px(12));
        playSignSpacing = typedArray.getDimension(R.styleable.CircleProgressBarView_playSignSpacing, DensityUtil.dip2px(6));
        pauseSignLength = typedArray.getDimension(R.styleable.CircleProgressBarView_pauseSignLength, DensityUtil.dip2px(8));
        pauseSignWidth = typedArray.getDimension(R.styleable.CircleProgressBarView_pauseSignWidth, DensityUtil.dip2px(10));
        initPaint();
    }

    private void initPaint() {

        circle = new Paint();
        circle.setColor(rawRingColor);
        circle.setStyle(Paint.Style.STROKE);
        circle.setStrokeWidth(ringWidth);
        circle.setAntiAlias(true);
        // 1. 背景环
        rectF2 = new RectF(-radius,-radius, radius,radius);

        triangle = new Paint();
        triangle.setColor(pauseSignColor);
        triangle.setStyle(Paint.Style.STROKE);
        triangle.setStrokeWidth(ringWidth - 1);
        triangle.setAntiAlias(true);
        path = new Path();

        // 播放标识（平行线）
        parallel = new Paint();
        parallel.setColor(playSignColor);
        parallel.setStyle(Paint.Style.FILL);
        parallel.setStrokeWidth(ringWidth - 1);
        parallel.setAntiAlias(true);

        // 画色彩环
        outerRing = new Paint();
        outerRing.setColor(processedRingColor);
        outerRing.setStyle(Paint.Style.STROKE);
        outerRing.setStrokeWidth(ringWidth);
        outerRing.setAntiAlias(true);
    }


    public int getCircleWidth(){
        return (getWidth() - getPaddingLeft()-getPaddingRight())/2;
    }

    public int getCircleHeight(){
        return (getHeight() - getPaddingTop()-getPaddingBottom())/2;
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(getCircleWidth(),getCircleHeight());


        if (isPlay){
            // 3. 背景圆颜色， 画内部播放标志
            canvasArc(canvas,getResources().getColor(R.color.unSelectColor));
            canvasParallel(canvas);
        }else {
            // 2.背景圆颜色， 画内部三角形
            canvasArc(canvas,rawRingColor);
            canvasTriangle(canvas);
        }
        // 4. 画环状进度条 （原理 弧度）
        RectF rectF = new RectF(-radius,-radius, radius,radius);
        canvas.drawArc(rectF,-90,progress /  max * 360,false,outerRing);

    }

    private void canvasArc(Canvas canvas ,int color) {
        circle.setColor(color);
        canvas.drawArc(rectF2, -90, 360, false, circle);
    }

    private void canvasTriangle(Canvas canvas) {
        Point trianglePoint = getTriangleStartPoint();
        //rawRingColor
        canvas.drawLine(trianglePoint.x,trianglePoint.y - 1,trianglePoint.x,trianglePoint.y + pauseSignWidth + 1,triangle);
        canvas.drawLine(trianglePoint.x,trianglePoint.y + pauseSignWidth,pauseSignLength/2 + 5,0,triangle);
        canvas.drawLine(pauseSignLength/2 + 5,0,trianglePoint.x,trianglePoint.y,triangle);
    }

    private void canvasParallel(Canvas canvas) {
        Point parallelPoint = getParallelStartPoint();
        canvas.drawLine(parallelPoint.x,parallelPoint.y,parallelPoint.x,parallelPoint.y + playSignHeight,parallel);
        canvas.drawLine(parallelPoint.x + playSignSpacing,parallelPoint.y,parallelPoint.x + playSignSpacing,parallelPoint.y + playSignHeight,parallel);
    }
    /**
     *  获取三角形初始点的坐标
     * @return
     */
    public Point getTriangleStartPoint(){
        float x = (0 - pauseSignLength) / 2;
        float y = (0 - pauseSignWidth) / 2;
        return new Point((int) x,(int) y);
    }

    public Point getParallelStartPoint(){
        float x = (0 - playSignSpacing)/2;
        float y = (0 - playSignHeight) /2;
        return new Point((int) x,(int) y);
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
        postInvalidate();
    }

    public void setProgress(float progress){
        if(progress<0){
            throw new IllegalArgumentException("progress不能小于0");
        }
        if(progress>max){
            progress = max;
        }
        if(progress <=max){
            this.progress = progress;
            postInvalidate();
        }
    }

    public void setMax(float max){
        this.max = max;
    }

    public void setProgress(int progress){
        if(progress<0){
            //throw new IllegalArgumentException("progress不能小于0");
            return;
        }
        if(progress>max){
            progress = (int) max;
        }
        if(progress <=max){
            this.progress = progress;
            postInvalidate();
        }
        this.progress = (float) progress;
    }

    public void setMax(int max){
        this.max = (float) max;
    }

    public int getRawRingColor() {
        return rawRingColor;
    }

    public void setRawRingColor(int rawRingColor) {
        this.rawRingColor = rawRingColor;
    }

    public int getProcessedRingColor() {
        return processedRingColor;
    }

    public void setProcessedRingColor(int processedRingColor) {
        this.processedRingColor = processedRingColor;
    }

    public int getPlaySignColor() {
        return playSignColor;
    }

    public void setPlaySignColor(int playSignColor) {
        this.playSignColor = playSignColor;
    }

    public int getPauseSignColor() {
        return pauseSignColor;
    }

    public void setPauseSignColor(int pauseSignColor) {
        this.pauseSignColor = pauseSignColor;
    }

    public float getRingWidth() {
        return ringWidth;
    }

    public void setRingWidth(float ringWidth) {
        this.ringWidth = ringWidth;
    }

    public float getPlaySignHeight() {
        return playSignHeight;
    }

    public void setPlaySignHeight(float playSignHeight) {
        this.playSignHeight = playSignHeight;
    }

    public float getPlaySignSpacing() {
        return playSignSpacing;
    }

    public void setPlaySignSpacing(float playSignSpacing) {
        this.playSignSpacing = playSignSpacing;
    }

    public float getPauseSignLength() {
        return pauseSignLength;
    }

    public void setPauseSignLength(float pauseSignLength) {
        this.pauseSignLength = pauseSignLength;
    }

    public float getPauseSignWidth() {
        return pauseSignWidth;
    }

    public void setPauseSignWidth(float pauseSignWidth) {
        this.pauseSignWidth = pauseSignWidth;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
