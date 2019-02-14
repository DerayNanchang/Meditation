package com.deray.meditation.base.text;

import android.view.View;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseFragment;

/**
 * Author: Chris
 * Blog: https://www.jianshu.com/u/a3534a2292e8
 * Date: 2018/11/29
 * Description
 */
public class Text2Fragment extends BaseFragment {
    @Override
    protected int getLayoutContentViewID() {
        return R.layout.text2;
    }

    @Override
    protected void init() {
        System.out.println("Text2Fragment");

    }

    @Override
    public void onClick(View v) {

    }
}
