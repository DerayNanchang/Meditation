package com.deray.meditation.base;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Chris on 2018/3/16.
 */

/**
 * 根据项目经验 V4 包 Fragment 使用场景更广更多
 */
public abstract class BaseFragment<V extends ViewDataBinding> extends Fragment implements View.OnClickListener {
    protected V view;
    // 懒加载模式

    public Context mContext;
    // 是否可见
    protected boolean isVisible;
    // 是否准备好了
    private boolean isPrepared;
    // 是否是第一次进入
    private boolean isFirst = true;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getLayoutContentViewID() != 0) {
            view = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), getLayoutContentViewID(), null, false);
            return view.getRoot();
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        System.out.println("isVisible :" + isVisible + " isPrepared : " + isPrepared + " isFirst : "+isFirst );

        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
        create();
    }

    private void create() {
        if (!isVisible || !isPrepared || !isFirst){
            return;
        }
        init();
        isFirst = false;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()){
            // 可见
            isVisible = true;
            create();
        }else {
            // 不可见
            isVisible = false;
        }
    }

    protected abstract int getLayoutContentViewID();

    protected abstract void init();

    protected void setAlpha(View view, float... values) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", values);
        animator.setDuration(200);
        animator.start();
    }

}
