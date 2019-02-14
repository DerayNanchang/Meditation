package com.deray.meditation.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by Chris on 2017/5/29.
 */

public abstract class BaseRecyclerViewHolder2<T> extends RecyclerView.ViewHolder {


    public BaseRecyclerViewHolder2(ViewGroup viewGroup, int layoutId) {
        // 注意要依附 viewGroup，不然显示item不全!!
        super(LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false));
        // 得到这个View绑定的Binding
    }


    /**
     * @param object   the data of bind
     * @param position the item position of recyclerView
     */
    public abstract void onBindViewHolder(T object, final int position);

    /**
     * 当数据改变时，binding会在下一帧去改变数据，如果我们需要立即改变，就去调用executePendingBindings方法。
     */
    void onBaseBindViewHolder(T object, final int position) {
        onBindViewHolder(object, position);
    }
}
