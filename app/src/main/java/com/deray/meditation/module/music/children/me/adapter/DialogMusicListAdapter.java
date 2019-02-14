package com.deray.meditation.module.music.children.me.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseRecyclerViewAdapter;
import com.deray.meditation.base.BaseRecyclerViewHolder;
import com.deray.meditation.databinding.ItemBaseViewBinding;
import com.deray.meditation.module.music.children.me.bean.PlayListBean;
import com.deray.meditation.utils.glide.GlideUtils;

/**
 * Created by Chris on 2018/4/10.
 */

public class DialogMusicListAdapter extends BaseRecyclerViewAdapter<PlayListBean> {
    private onMusicListItemListener onMusicListItem;

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DialogMusicListViewHolder(parent, R.layout.item_base_view);
    }

    private class DialogMusicListViewHolder extends BaseRecyclerViewHolder<PlayListBean, ItemBaseViewBinding> {

        public DialogMusicListViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        public void onBindViewHolder(final PlayListBean playListBean, final int position) {
            binding.llMinePlayListMore.setVisibility(View.GONE);
            GlideUtils.baseMusicState(binding.ivMinePlayListIcon,playListBean.getIcon(),R.mipmap.icon_default);


            /*if (playListBean.getMusicDownNum().equals(playListBean.getMusicTotal())) {
                binding.ivMinePlayListDownloadStatus.setImageResource(R.mipmap.icon_mine_play_list_download_status2);
            } else {
                binding.ivMinePlayListDownloadStatus.setImageResource(R.mipmap.icon_mine_play_list_download_status1);
            }*/

            binding.tvMinePlayListTitle.setText(playListBean.getName() + "");
            if (position != 0){
                binding.tvMinePlayListDownloadTotal.setText(playListBean.getMusicTotal() + "首,");
                binding.tvMinePlayListDownloadMusic.setText("已下载了" + playListBean.getMusicDownNum() + "首");
                binding.ivMinePlayListDownloadStatus.setVisibility(View.VISIBLE);
            }else {
                binding.ivMinePlayListDownloadStatus.setVisibility(View.GONE);
            }
            binding.rlPlayListRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMusicListItem.onMusicListItemClick(v, playListBean, position);
                }
            });
        }
    }

    public void setOnMusicListItemListener(onMusicListItemListener onMusicListItem) {
        this.onMusicListItem = onMusicListItem;
    }

    public interface onMusicListItemListener {
        void onMusicListItemClick(View view, PlayListBean playListBean, int position);
    }
}
