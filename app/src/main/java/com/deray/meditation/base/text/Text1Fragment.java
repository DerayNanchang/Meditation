package com.deray.meditation.base.text;

import android.content.Intent;
import android.view.View;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseFragment;
import com.deray.meditation.databinding.Text1Binding;

import java.util.Objects;

/**
 * Author: Chris
 * Blog: https://www.jianshu.com/u/a3534a2292e8
 * Date: 2018/11/29
 * Description
 */
public class Text1Fragment extends BaseFragment<Text1Binding> {
    @Override
    protected int getLayoutContentViewID() {
        return R.layout.text1;
    }

    @Override
    protected void init() {
        System.out.println("Text1Fragment");

        view.tvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),Main3Activity.class));
                Objects.requireNonNull(getActivity()).finish();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
