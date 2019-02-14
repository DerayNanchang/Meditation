package com.deray.meditation.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.deray.meditation.R;
import com.deray.meditation.module.music.Lyric;

import java.util.ArrayList;
import java.util.List;

public class LyricView extends View {
    private int viewW;
    private int viewH;
    private List<Lyric.LyricInfo> lyricInfos = new ArrayList<>();
    private int centerPosition = 0;
    private int centerSize = getResources().getDimensionPixelSize(R.dimen.sp16);
    private int otherSize = getResources().getDimensionPixelSize(R.dimen.sp14);
    private int lineHeight = getResources().getDimensionPixelSize(R.dimen.sp40);
    private int centerColor = getResources().getColor(R.color.colorAccent);
    private int otherColor = getResources().getColor(android.R.color.darker_gray);
    private Paint paint;
    private String text;
    private Rect rect;

    private int progress = 0;
    private int duration = 0;
    private String by = "";
    private boolean updateByPro = true;
    private float offSet;

    private float moveUp;
    private UpdateProgress updateProgress;


    public LyricView(Context context) {
        this(context, null, 0);
    }

    public LyricView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void setLyric(Lyric lyric) {
        if (lyricInfos != null && lyricInfos.size() > 0) {
            lyricInfos.clear();
        }

        if (lyric == null) {
            lyric = new Lyric();
            lyricInfos.add(new Lyric.LyricInfo(0, "无歌词"));
            lyric.setLyricInfoList(lyricInfos);
        } else {
            if (lyric.getLyricInfoList() == null || lyric.getLyricInfoList().size() == 0) {
                lyricInfos.add(new Lyric.LyricInfo(0, "无歌词"));
            } else {
                this.lyricInfos = lyric.getLyricInfoList();
            }
            by = lyric.getBy();
        }
    }

    private void init() {
        // 初始化模拟数据

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);


        // 初始化
        lyricInfos.add(new Lyric.LyricInfo(0, "正在加载歌词.."));

        // 求出自身宽高
        text = lyricInfos.get(centerPosition).getText();
        rect = new Rect();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.viewW = w;
        this.viewH = h;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("------------------绘制-----------------");

        // 偏移量 = 行距总高度 * 偏移率
        // 便宜率 = 行距周期已经偏移的时间/行距总周期时间
        // 行距周期已经偏移时间 = 当前的进度 - 周期开始时间
        // 行距总周期时间 = 下周期开始时间 -  本周期开始时间
        // 求出居中行的偏移量 其他行就根据行高进行相应偏移

        // 自动滚动
        // 最后一行

        if (updateByPro) {
            if (lyricInfos.size() == 0) {
                return;
            }
            int lineDuration = 0;
            if (centerPosition == lyricInfos.size() - 1) {
                //最后一行
                lineDuration = duration - lyricInfos.get(centerPosition).getStartTime();
            } else {
                // 其他行
                lineDuration = lyricInfos.get(centerPosition + 1).getStartTime() - lyricInfos.get(centerPosition).getStartTime();
            }
            int bOffset = progress - lyricInfos.get(centerPosition).getStartTime();
            float offSetRate = bOffset / (float) lineDuration;
            offSet = lineHeight * offSetRate;
        }
        paint.getTextBounds(text, 0, text.length(), rect);
        int centerHeight = rect.height();
        int centerWidth = rect.width();

        // 画出中间位置
        float centerX = viewW / 2;
        float centerY = (viewH / 2 - centerHeight / 2) - offSet;

        for (int i = 0; i < lyricInfos.size(); i++) {
            // 中间行的 Y +( 当前行行号 -  中间行行号) * 行高
            if (i == centerPosition) {
                paint.setColor(centerColor);
                paint.setTextSize(centerSize);
            } else {
                paint.setColor(otherColor);
                paint.setTextSize(otherSize);
            }

            float otherX = viewW / 2;
            float otherY = (centerY + (i - centerPosition) * lineHeight);

            if (otherY < 0 || otherY > viewH + lineHeight) continue;

            canvas.drawText(lyricInfos.get(i).getText(), otherX, otherY, paint);
        }
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void updateProgress(int progress) {
        if (!updateByPro) return;
        // 核心 比对找到居中行
        // 居中行 = 当前进度 >= 歌词开始时间并且 小于 下行开始时间 就说明当前行是居中行
        // progress > 最后一行歌词的开始时间，说明是最后一行为中间行

        /* -- 1. 歌词居中分部呈现 -- >
        // 指定居中行
        // 根据居中行展开其他行
        // 居中行的高度 + 其他行距离居中行的行高
        // 其他行距离居中行的行高 = (其他当前行数 - 居中行当前行数) * 行高

        /* -- 2. 自动找寻居中行 -- >
        // if 歌词最后一行 = 当前进度(progress) >= 最后一行的开始时间
        // else 遍历所有数据, 居中行 = 当前进度 >= 开始时间 and 当前进度小于下行的开始时间

        /* -- 3.歌词自动滚动 --*/
        // 偏移量 = 行距周期总时间 * 偏移率
        // 便宜率 = 行距周期已经偏移的时间/行距总周期时间
        // 行距周期已经偏移时间 = 当前的进度 - 周期开始时间
        // 行距总周期时间 = 下周期开始时间 -  本周期开始时间
        // 求出居中行的偏移量 其他行就根据行高进行相应偏移


        for (int i = 0; i < lyricInfos.size(); i++) {

            if (progress >= lyricInfos.get(lyricInfos.size() - 1).getStartTime()) {
                // 最后一行
                centerPosition = lyricInfos.size() - 1;
                break;
            } else {
                // 不是最后一行
                if (progress >= lyricInfos.get(i).getStartTime() && progress < lyricInfos.get(i + 1).getStartTime()) {
                    centerPosition = i;
                    break;
                }
            }
        }
        // 重新绘制
        invalidate();
    }

    private float moveDownY;    // 按下时的 Y 值
    float markY = 0f;           // 按下时，滚动的偏移量


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 停止歌词随歌曲进度滚动
                // 记录当前 y
                updateByPro = false;
                moveDownY = event.getY();
                // 手指按下时记录当前中间行偏移量
                markY = this.offSet;
                break;
            case MotionEvent.ACTION_MOVE:
                float endY = event.getY();  // 获取滑动时的Y 值
                float moveValue = moveDownY - endY;     // 移动的Y = 按下时的Y - 滑动的Y
                this.offSet = moveValue + markY;        // 最终滑动偏移Y = 移动的Y + 原来已经偏移的Y
                // 当移动的距离大于行高，就进行偏移
                if (Math.abs(offSet) >= lineHeight) {    // 最终偏移量如果大于行高
                    // 获取偏移行数
                    float lineNum = offSet / lineHeight;    // 获取已经滑动的行数
                    centerPosition += lineNum;              // 新的中心行 = 原来的中心行 + 已经滑动的行数
                    if (centerPosition < 0) centerPosition = 0;
                    if (centerPosition > (lyricInfos.size() - 1))
                        centerPosition = lyricInfos.size() - 1;

                    // 问题 2 没有重新赋值 moveDownY
                    // 例 如果已经偏移了，然后继续偏移，应该是从最新的行计算(上一个的 endY) moveDownY 而不是从一开始计算 moveDownY
                    moveDownY = endY;


                    // 问题 1 偏移量
                    // 若是已经移动了，没有重新更改偏移量的值
                    // 例，当已经从 1 偏移到 第二行时，它的偏移量依然是（移动的偏移量 + 旧的偏移量） ：moveValue + markY
                    offSet = offSet % lineHeight;


                    // 问题 3 markY 行偏移量
                    // 例 当移动的距离 70%20=10 + 最初的5个偏移量 = 最终行偏移量
                    markY = offSet;
                }


                invalidate();

                break;
            case MotionEvent.ACTION_UP:

                updateByPro = true;

                Lyric.LyricInfo info = lyricInfos.get(centerPosition);
                if (updateProgress != null) {
                    updateProgress.onUpdateProcess(info.getStartTime());
                }

                // onclick 点击事件的方法入口
                performClick();
                break;
        }
        return true;
    }

    public void setOnUpdateProgressListener(UpdateProgress updateProgress) {
        this.updateProgress = updateProgress;
    }

    public interface UpdateProgress {
        void onUpdateProcess(int progress);
    }
}
