package com.deray.meditation.ui.anim;

import android.view.animation.AlphaAnimation;

public class AnimaUtils {
    public static AlphaAnimation alpha(float start,float end,int duration){
        AlphaAnimation alphaAnimation = new AlphaAnimation(start, end);
        alphaAnimation.setDuration(duration);    //深浅动画持续时间
        alphaAnimation.setFillAfter(true);   //动画结束时保持结束的画面
        return alphaAnimation;
    }

    public static AlphaAnimation alpha(float start,float end){
        AlphaAnimation alphaAnimation = new AlphaAnimation(start, end);
        alphaAnimation.setDuration(1500);    //深浅动画持续时间
        alphaAnimation.setFillAfter(true);   //动画结束时保持结束的画面
        return alphaAnimation;
    }
}
