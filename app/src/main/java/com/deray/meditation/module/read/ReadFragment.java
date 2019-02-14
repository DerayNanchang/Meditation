package com.deray.meditation.module.read;

import android.view.View;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseFragment;

public class ReadFragment extends BaseFragment {

    @Override
    protected int getLayoutContentViewID() {
        return R.layout.fragment_read;
    }

    @Override
    protected void init() {
        System.out.println("ReadFragment");
    }

    @Override
    public void onClick(View v) {

    }
}
