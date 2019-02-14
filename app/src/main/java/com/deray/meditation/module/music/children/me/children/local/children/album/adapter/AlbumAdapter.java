package com.deray.meditation.module.music.children.me.children.local.children.album.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseRecyclerViewAdapter;
import com.deray.meditation.base.BaseRecyclerViewHolder;
import com.deray.meditation.databinding.ItemBaseViewBinding;
import com.deray.meditation.module.music.children.me.children.local.children.album.bean.Album;
import com.deray.meditation.utils.glide.GlideUtils;

public class AlbumAdapter extends BaseRecyclerViewAdapter<Album> {
    @NonNull
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumViewHolder(parent,R.layout.item_base_view);
    }

    private class AlbumViewHolder extends BaseRecyclerViewHolder<Album,ItemBaseViewBinding> {

        public AlbumViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        public void onBindViewHolder(Album album, final int position) {
            binding.tvMinePlayListTitle.setText(album.getAlbum());
            binding.tvMinePlayListDownloadTotal.setText(album.getCont()+" 首   " + album.getSinger());
            binding.ivMinePlayListDownloadStatus.setVisibility(View.GONE);
            binding.tvMinePlayListDownloadMusic.setVisibility(View.GONE);
            GlideUtils.baseMusicState(binding.ivMinePlayListIcon,album.getIcon(),R.mipmap.icon_default);

            binding.rlPlayListRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEasyItemListener.onItemClick(position);
                }
            });
        }
    }
}
