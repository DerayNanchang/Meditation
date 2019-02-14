package com.deray.meditation.module.video;

import android.view.View;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseFragment;

public class VideoFragment extends BaseFragment {

    @Override
    protected int getLayoutContentViewID() {
        return R.layout.fragment_video;
    }

    @Override
    protected void init() {
        System.out.println("VideoFragment");
    }

    @Override
    public void onClick(View v) {

    }
}
