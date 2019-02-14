package com.deray.meditation.module.music.children.service;

import android.view.View;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseFragment;
import com.deray.meditation.cache.MusicCacheManager;
import com.deray.meditation.databinding.FragmentServiceBinding;

public class ServiceFragment extends BaseFragment<FragmentServiceBinding> {

    private MusicCacheManager manager;

    @Override
    protected int getLayoutContentViewID() {
        return R.layout.fragment_service;
    }

    @Override
    protected void init() {
        manager = MusicCacheManager.get();
        view.wscCrontab.setState(manager.getServiceCrontab());
        view.wscHeadSet.setState(manager.getServiceHeadSet());

        view.wscCrontab.setOnClickListener(this);
        view.wscHeadSet.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.wscCrontab:
                {
                    boolean state = manager.getServiceCrontab();
                    if (state){
                        manager.saveServiceCrontab(false);
                        view.wscCrontab.setState(false);
                    }else {
                        manager.saveServiceCrontab(true);
                        view.wscCrontab.setState(true);
                    }
                }
                break;
            case R.id.wscHeadSet:
                {
                    boolean state = manager.getServiceHeadSet();
                    if (state){
                        manager.saveServiceHeadSet(false);
                        view.wscHeadSet.setState(false);
                    }else {
                        manager.saveServiceHeadSet(true);
                        view.wscHeadSet.setState(true);
                    }
                }
                break;
        }
    }
}
