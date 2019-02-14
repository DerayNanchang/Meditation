package com.deray.meditation.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deray.meditation.R;


/**
 * Created by Chris on 2018/1/8.
 */

public class ViewMusicHomeItem extends LinearLayout implements View.OnClickListener {

    private RelativeLayout rl_music_home_list_root;
    private ImageView iv_music_home_list_icon;
    private TextView tv_music_home_list_title;
    private TextView tv_music_home_list_quantity;
    private View v_music_home_list_view;
    private IViewMusicHomeItem iViewMusicHomeItem;
    private boolean isLineView;
    private String title;
    private String quantity;

    public ViewMusicHomeItem(Context context) {
        super(context);
    }

    public ViewMusicHomeItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ViewMusicHomeItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public void initView(Context context, AttributeSet attrs) {
        getAttributesValue(context, attrs);
        setView();
    }

    private void getAttributesValue(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ViewMusicHomeItemStyleable);
        isLineView = ta.getBoolean(R.styleable.ViewMusicHomeItemStyleable_bottomLineView, true);
        //TODO 暂时还没有找到怎么设置 mipmap 图片 属性
        //ta.getDrawable(R.styleable.ViewMusicHomeItemStyleable_is_show_bottom_line_view, R.mipmap.ic_launcher);
        title = ta.getString(R.styleable.ViewMusicHomeItemStyleable_setTitle);
        quantity = ta.getString(R.styleable.ViewMusicHomeItemStyleable_setQuantity);
    }


    private void setView() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.item_view_music_home, null);
        rl_music_home_list_root = inflate.findViewById(R.id.rl_music_home_list_root);
        iv_music_home_list_icon = inflate.findViewById(R.id.iv_music_home_list_icon);
        tv_music_home_list_title = inflate.findViewById(R.id.tv_music_home_list_title);
        tv_music_home_list_quantity = inflate.findViewById(R.id.tv_music_home_list_quantity);
        v_music_home_list_view = inflate.findViewById(R.id.v_music_home_list_view);
        setBottomLineView(isLineView);
        setTitle(title);
        setQuantity(quantity);
        addView(inflate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_music_home_list_root:
                if (iViewMusicHomeItem != null) {
                    iViewMusicHomeItem.OnMusicHomeListRootListener(v);
                }
                break;
        }
    }

    // 对外暴露接口 提供回调
    public void setMusicHomeListRootListener(IViewMusicHomeItem iViewMusicHomeItem) {
        this.iViewMusicHomeItem = iViewMusicHomeItem;
        rl_music_home_list_root.setOnClickListener(this);
    }

    public void setBottomLineView(boolean isLineView) {
        if (isLineView) {
            v_music_home_list_view.setVisibility(VISIBLE);
        } else {
            v_music_home_list_view.setVisibility(GONE);
        }
    }

    public boolean getBottomLineView() {
        return isLineView;
    }

    public void setSrc(@DrawableRes int resId) {
        iv_music_home_list_icon.setImageResource(resId);
    }

    public void setTitle(String title) {
        tv_music_home_list_title.setText(title + "");
    }

    public String getTitle() {
        return tv_music_home_list_title.getText().toString().trim();
    }

    public void setQuantity(String quantity) {
        tv_music_home_list_quantity.setText(quantity + "");
    }

    public String getQuantity() {
        return tv_music_home_list_quantity.getText().toString().trim();
    }

    public interface IViewMusicHomeItem {
        void OnMusicHomeListRootListener(View view);
    }
}
