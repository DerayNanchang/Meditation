package com.deray.meditation.module.music.children.me.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseRecyclerViewAdapter;
import com.deray.meditation.base.BaseRecyclerViewHolder;
import com.deray.meditation.databinding.ItemViewPlayListDialogBinding;
import com.deray.meditation.module.music.children.me.bean.PlayListDialog;


/**
 * Created by Chris on 2018/4/9.
 */

public class PlayListDialogAdapter extends BaseRecyclerViewAdapter<PlayListDialog> {

    private OnItemMenuClick onItemMusicListClick;
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlayListDialogViewHolder(parent, R.layout.item_view_play_list_dialog);
    }

    private class PlayListDialogViewHolder extends BaseRecyclerViewHolder<PlayListDialog, ItemViewPlayListDialogBinding> {


        public PlayListDialogViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        public void onBindViewHolder(final PlayListDialog playListDialog, final int position) {
            binding.llBottomDialogAddIcon.setImageResource(playListDialog.getIconi());
            binding.llBottomDialogAddText.setText(playListDialog.getText());

            binding.rlBottomDialogAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemMusicListClick.OnItemMenuClick(v,playListDialog,position);
                }
            });
        }

    }

    public void setItemMenuListener(OnItemMenuClick onItemMusicListClick){
        this.onItemMusicListClick = onItemMusicListClick;
    }

    public interface OnItemMenuClick {
        void OnItemMenuClick(View v, PlayListDialog playListDialog, int position);
    }
}
