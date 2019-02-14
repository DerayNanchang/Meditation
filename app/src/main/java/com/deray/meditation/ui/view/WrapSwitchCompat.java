package com.deray.meditation.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deray.meditation.R;
import com.deray.meditation.cache.MusicCacheManager;

public class WrapSwitchCompat extends LinearLayout {

    private String name;
    private Boolean state;
    private TextView tvItemName;
    private SwitchCompat scSettingsState;

    public WrapSwitchCompat(Context context) {
        this(context,null,0);
    }

    public WrapSwitchCompat(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public WrapSwitchCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WrapSwitchCompat);
        name = typedArray.getString(R.styleable.WrapSwitchCompat_setName);
        state = typedArray.getBoolean(R.styleable.WrapSwitchCompat_setState,true);

        loadSrc();

    }

    public void loadSrc(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_wrap_switch_compat, null, false);
        tvItemName = view.findViewById(R.id.tvItemName);
        scSettingsState = view.findViewById(R.id.scSettingsState);
        RelativeLayout rlRoot = view.findViewById(R.id.rlRoot);
        tvItemName.setText(name);
        scSettingsState.setChecked(state);
        addView(view);
    }

    public void setName(String name){
        tvItemName.setText(name);
    }

    public void setState(boolean state){
        scSettingsState.setChecked(state);
    }
}
