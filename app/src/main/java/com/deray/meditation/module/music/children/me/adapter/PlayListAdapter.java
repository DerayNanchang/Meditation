package com.deray.meditation.module.music.children.me.adapter;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deray.meditation.R;
import com.deray.meditation.module.music.children.me.bean.PlayListBean;
import com.deray.meditation.module.music.children.me.bean.PlayListTypeBean;
import com.deray.meditation.utils.glide.GlideUtils;

import java.util.List;

/**
 * Created by Chris on 2018/1/12.
 */

public class PlayListAdapter extends BaseExpandableListAdapter implements View.OnClickListener {
    private Context context;
    private List<PlayListTypeBean> playListTypeGroup;
    private List<List<PlayListBean>> playList;
    // private BottomSheetDialog bottomSheetDialog;
    //  private ObjectAnimator objectAnimator;
    private boolean flag;
    private BottomSheetDialog bottomSheetDialog;
    private SheetItemClick sheetItemClick;
    private ChildItemClick childItemClick;


    public void addData(List<PlayListTypeBean> playListTypeGroup, List<List<PlayListBean>> playList) {
        this.playListTypeGroup = playListTypeGroup;
        this.playList = playList;
    }

    public PlayListAdapter(Context context, List<PlayListTypeBean> playListTypeGroup, List<List<PlayListBean>> playList) {
        this.context = context;
        this.playListTypeGroup = playListTypeGroup;
        this.playList = playList;
    }

    public PlayListAdapter(Context context) {
        this.context = context;
    }


    //获取与给定的组相关的数据，得到数组groups中元素的数据
    @Override
    public PlayListTypeBean getGroup(int groupPosition) {
        return playListTypeGroup.get(groupPosition);
    }

    //获取的群体数量，得到groups里元素的个数
    @Override
    public int getGroupCount() {
        return playListTypeGroup.size();
    }

    //获取与孩子在给定的组相关的数据,得到数组children中元素的数据
    @Override
    public PlayListBean getChild(int groupPosition, int childPosition) {
        return playList.get(groupPosition).get(childPosition);
    }

    //取得指定组中的children个数，就是groups中每一个条目中的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        if (playList.size() != 0){
            return playList.get(groupPosition).size();
        }else {
            return 0;
        }
    }

    //获取组在给定的位置编号，即groups中元素的ID
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //获取在给定的组的children的ID，也就是children中元素的ID
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //表示孩子是否和组ID是跨基础数据的更改稳定
    @Override
    public boolean hasStableIds() {
        return false;
    }


    class GroupHolder {
        ImageView iv_mine_play_left_icon_switch;
        TextView tv_mine_play_title;
        TextView tv_mine_play_title_number;
        ImageView iv_mine_play_left_icon_setting;

        public GroupHolder(View groupView) {
            iv_mine_play_left_icon_switch = groupView.findViewById(R.id.iv_mine_play_left_icon_switch);
            tv_mine_play_title = groupView.findViewById(R.id.tv_mine_play_title);
            tv_mine_play_title_number = groupView.findViewById(R.id.tv_mine_play_title_number);
            iv_mine_play_left_icon_setting = groupView.findViewById(R.id.iv_mine_play_left_icon_setting);
        }
    }


    //获取一个视图显示给定组，存放groups
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_view_mine_play_list_type, null);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        groupHolder.iv_mine_play_left_icon_setting.setOnClickListener(this);
        bindGroupView(groupPosition, isExpanded, groupHolder);
        return convertView;
    }


    private void bindGroupView(int position, boolean isExpanded, GroupHolder groupHolder) {
        if (getGroup(position) != null) {
            if (!TextUtils.isEmpty(getGroup(position).getTitle())) {
                groupHolder.tv_mine_play_title.setText(getGroup(position).getTitle());
            }
            if (!TextUtils.isEmpty(playListTypeGroup.get(position).getPlayListTotal())) {
                groupHolder.tv_mine_play_title_number.setText(getGroup(position).getPlayListTotal());
            }
            groupHolder.iv_mine_play_left_icon_switch.setImageResource(getGroup(position).getIcon());
            setIconStatus(isExpanded, groupHolder.iv_mine_play_left_icon_switch);
        }
    }

    private void setIconStatus(boolean isExpanded, ImageView iv_mine_play_left_icon_switch) {
        if (isExpanded) {
            iv_mine_play_left_icon_switch.setImageResource(R.drawable.svg_music_me_bottom);
        } else {
            iv_mine_play_left_icon_switch.setImageResource(R.drawable.svg_music_me_right);
        }
    }

    class ChildHolder {
        ImageView iv_mine_play_list_icon;
        TextView tv_mine_play_list_title;
        ImageView iv_mine_play_list_download_status;
        TextView tv_mine_play_list_download_music;
        TextView tv_mine_play_list_download_total;
        LinearLayout ll_mine_play_list_more;

        public ChildHolder(View childView) {
            iv_mine_play_list_icon = childView.findViewById(R.id.iv_mine_play_list_icon);
            tv_mine_play_list_title = childView.findViewById(R.id.tv_mine_play_list_title);
            iv_mine_play_list_download_status = childView.findViewById(R.id.iv_mine_play_list_download_status);
            tv_mine_play_list_download_music = childView.findViewById(R.id.tv_mine_play_list_download_music);
            tv_mine_play_list_download_total = childView.findViewById(R.id.tv_mine_play_list_download_total);
            ll_mine_play_list_more = childView.findViewById(R.id.ll_mine_play_list_more);

        }
    }

    //获取一个视图显示在给定的组 的儿童的数据，就是存放children
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_base_view, null);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        bindChildView(childHolder, groupPosition, childPosition, isLastChild);
        return convertView;
    }


    private void bindChildView(ChildHolder childHolder, final int groupPosition, final int childPosition, boolean isLastChild) {
        if (playList != null && playList.size() > 0) {
            if (playList.get(groupPosition) != null && playList.get(groupPosition).size() > 0) {
                if (getChild(groupPosition, childPosition) != null) {

                    GlideUtils.baseMusicState(childHolder.iv_mine_play_list_icon,getChild(groupPosition,childPosition).getIcon(),R.mipmap.icon_default);

                    childHolder.ll_mine_play_list_more.setVisibility(View.VISIBLE);
                    if (getChild(groupPosition,childPosition).getMusicDownNum().equals(getChild(groupPosition,childPosition).getMusicTotal())){
                        childHolder.iv_mine_play_list_download_status.setImageResource(R.drawable.svg_music_me_download_finish);
                    }else {
                        childHolder.iv_mine_play_list_download_status.setImageResource(R.drawable.svg_music_me_download_unfinish);
                    }
                    childHolder.ll_mine_play_list_more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            childItemClick.onChildItemClick(v, getChild(groupPosition, childPosition), groupPosition, childPosition);
                        }
                    });
                    if (!TextUtils.isEmpty(getChild(groupPosition, childPosition).getName())) {
                        childHolder.tv_mine_play_list_title.setText(getChild(groupPosition, childPosition).getName());
                    }

                    if (!TextUtils.isEmpty(getChild(groupPosition, childPosition).getMusicTotal())) {
                        childHolder.tv_mine_play_list_download_total.setText(getChild(groupPosition, childPosition).getMusicTotal() + "首,");
                    }

                    if (!TextUtils.isEmpty(getChild(groupPosition, childPosition).getMusicDownNum())) {
                        childHolder.tv_mine_play_list_download_music.setText("已下载了" + getChild(groupPosition, childPosition).getMusicDownNum() + "首");
                    }
                }
            }
        }
    }

    //孩子在指定的位置是可选的，即：children中的元素是可点击的
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_mine_play_left_icon_setting:
                showDialog();
                break;
            case R.id.rl_bottom_dialog_add:
                sheetItemClick.onSheetClick(v, bottomSheetDialog);
                break;

            case R.id.rl_bottm_dialog_management:
                break;
        }
    }


    public void setBottomSheetItemOnClick(SheetItemClick sheetItemClick) {
        this.sheetItemClick = sheetItemClick;
    }

    public void setChildItemClick(ChildItemClick childItemClick) {
        this.childItemClick = childItemClick;
    }

    public interface SheetItemClick {
        void onSheetClick(View view, BottomSheetDialog bottomSheetDialog);
    }

    public interface ChildItemClick<T> {
        void onChildItemClick(View view, T data, int groupPosition, int childPosition);
    }


    private void showDialog() {
        View bottomDialogView = View.inflate(context, R.layout.item_bottom_show_dialog, null);
        RelativeLayout rl_bottom_dialog_add = bottomDialogView.findViewById(R.id.rl_bottom_dialog_add);
        RelativeLayout rl_bottm_dialog_management = bottomDialogView.findViewById(R.id.rl_bottm_dialog_management);
        rl_bottom_dialog_add.setOnClickListener(this);
        rl_bottm_dialog_management.setOnClickListener(this);
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bottomDialogView);
        bottomSheetDialog.show();
    }

    private void openChildren(ImageView iv_mine_play_left_icon_switch) {
        flag = true;
        final String ROTATION = "rotation";
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(iv_mine_play_left_icon_switch, ROTATION, 0f, 90f);
        objectAnimator.setDuration(100);
        objectAnimator.start();
    }

    private void closeChildren(ImageView iv_mine_play_left_icon_switch) {
        if (flag) {
            final String ROTATION = "rotation";
            ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(iv_mine_play_left_icon_switch, ROTATION, 0f, -90f);
            objectAnimator.setDuration(100);
            objectAnimator.start();
        }
        flag = false;
    }


}
