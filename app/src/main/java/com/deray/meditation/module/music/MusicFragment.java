package com.deray.meditation.module.music;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseFragment;
import com.deray.meditation.databinding.FragmentMusicBinding;
import com.deray.meditation.module.main.bean.TabEntity;
import com.deray.meditation.module.music.adapter.MusicFragmentPageAdapter;
import com.deray.meditation.module.music.children.me.MeFragment;
import com.deray.meditation.module.music.children.service.ServiceFragment;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

public class MusicFragment extends BaseFragment<FragmentMusicBinding> {

    /*String [] titles  = new String[]{"我的","服务","设置"};*/
    String [] titles  = new String[]{"我的","服务"};
    @Override
    protected int getLayoutContentViewID() {
        return R.layout.fragment_music;
    }

    @Override
    protected void init() {
        System.out.println("MusicFragment");
        initViewPage();
        initTabLayout();
        initEvent();
    }

    private void initViewPage() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MeFragment());
        fragments.add(new ServiceFragment());
        //fragments.add(new SettingsFragment());
        view.vpHomePage.setAdapter(new MusicFragmentPageAdapter(getChildFragmentManager(),fragments,titles));
        view.vpHomePage.setOffscreenPageLimit(2);
        view.vpHomePage.setCurrentItem(0);
    }

    private void initTabLayout() {

        ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();
        for (String title : titles){
            TabEntity entity = new TabEntity(title);
            tabEntities.add(entity);
        }
        view.ctlHomeTabLayout.setTabData(tabEntities);
    }

    private void initEvent() {
        // viewPageAdapter 滑动事件
        view.vpHomePage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                view.ctlHomeTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // tablayout 点击事件
        view.ctlHomeTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                view.vpHomePage.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

    }

    @Override
    public void onClick(View v) {

    }
}
