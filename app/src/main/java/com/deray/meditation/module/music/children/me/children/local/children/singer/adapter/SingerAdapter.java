package com.deray.meditation.module.music.children.me.children.local.children.singer.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseRecyclerViewAdapter;
import com.deray.meditation.base.BaseRecyclerViewHolder;
import com.deray.meditation.databinding.ItemBaseViewBinding;
import com.deray.meditation.module.music.children.me.children.local.children.singer.bean.Singer;
import com.deray.meditation.utils.glide.GlideUtils;

public class SingerAdapter extends BaseRecyclerViewAdapter<Singer> {
    @NonNull
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SingerViewHolder(parent,R.layout.item_base_view);
    }

    private class SingerViewHolder extends BaseRecyclerViewHolder<Singer,ItemBaseViewBinding> {

        public SingerViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        public void onBindViewHolder(Singer singer, final int position) {
            binding.tvMinePlayListTitle.setText(singer.getSinger());
            binding.tvMinePlayListDownloadTotal.setText(singer.getCont()+" 首");
            binding.ivMinePlayListDownloadStatus.setVisibility(View.GONE);
            binding.tvMinePlayListDownloadMusic.setVisibility(View.GONE);
            GlideUtils.baseMusicState(binding.ivMinePlayListIcon,singer.getIcon(),R.mipmap.icon_default);

            binding.rlPlayListRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEasyItemListener.onItemClick(position);
                }
            });
        }
    }
}
