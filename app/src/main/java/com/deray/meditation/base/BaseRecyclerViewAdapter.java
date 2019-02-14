package com.deray.meditation.base;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 2017/5/29.
 */

public abstract class BaseRecyclerViewAdapter<D> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {
    protected List<D> data = new ArrayList<>();
    protected OnItemListener onItemListener;
    protected OnLongItemListener onLongItemListener;
    protected OnEasyItemListener onEasyItemListener;


    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        holder.onBaseBindViewHolder(data.get(position), position);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addData(List<D> data) {
        this.data = data;
    }

    public List<D> getData() {
        return this.data;
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void remove(int data) {
        this.data.remove(data);
    }

    public void remove(Object o) {
        this.data.remove(o);
    }

    public void removeAll(List<D> data) {
        this.data.removeAll(data);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    public void setOnLongItemListener(OnLongItemListener onLongItemListener) {
        this.onLongItemListener = onLongItemListener;
    }

    public void setEasyItemListener(OnEasyItemListener onEasyItemListener){
        this.onEasyItemListener = onEasyItemListener;
    }

}
