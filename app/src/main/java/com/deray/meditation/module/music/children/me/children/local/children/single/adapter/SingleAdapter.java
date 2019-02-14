package com.deray.meditation.module.music.children.me.children.local.children.single.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseRecyclerViewAdapter;
import com.deray.meditation.base.BaseRecyclerViewHolder;
import com.deray.meditation.databinding.ItemSingleItemContentBinding;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.utils.DensityUtil;


/**
 * Created by Chris on 2018/3/23.
 */

public class SingleAdapter extends BaseRecyclerViewAdapter<Music> {

    private int count;
    private onItemMenuClickListener menuListener;

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SingleItemContentHolder(parent, R.layout.item_single_item_content);
    }

    /**
     * 设置 drawables lift 图片
     *
     * @param context
     * @param view
     */
    private void initTextView(Context context, TextView view) {
        Drawable drawable = context.getResources().getDrawable(R.mipmap.finish);
        drawable.setBounds(0, 0, DensityUtil.dip2px(20), DensityUtil.dip2px(80));
        view.setCompoundDrawables(drawable, null, null, null);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    private class SingleItemContentHolder extends BaseRecyclerViewHolder<Music, ItemSingleItemContentBinding> {
        public SingleItemContentHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        public void onBindViewHolder(final Music music, final int position) {
            if (music.getIsPlay()) {
                binding.ivItemIconName.setVisibility(View.VISIBLE);
            } else {
                binding.ivItemIconName.setVisibility(View.GONE);
            }

            binding.tvSingleSinger.setSelected(true);

                if (music != null) {
                    if (!TextUtils.isEmpty(music.getName())) {
                        binding.tvItemTitleName.setText(music.getName());
                    }
                    if (!TextUtils.isEmpty(music.getSinger())) {
                        binding.tvSingleSinger.setText(music.getSinger() + " - " + music.getAlbum());
                    }
                }

            binding.rlContentRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onItemListener != null){
                        onItemListener.onItemClick(v, music, position);
                    }
                    if (onEasyItemListener != null){
                        onEasyItemListener.onItemClick(position);
                    }
                }
            });


            binding.rlContentRoot.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });

            binding.llSingleMusicItemMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (menuListener != null){
                            menuListener.onItemMenuClick(v, music, position);
                    }
                }
            });
        }
    }

    public void setItemMenuClickLinstener(onItemMenuClickListener onItemMenuClickListener) {
        this.menuListener = onItemMenuClickListener;
    }

    public interface onItemMenuClickListener {
        void onItemMenuClick(View view, Music music, int position);
    }
}
