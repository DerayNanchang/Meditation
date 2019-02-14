package com.deray.meditation.base.text;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Author: Chris
 * Blog: https://www.jianshu.com/u/a3534a2292e8
 * Date: 2018/11/29
 * Description
 */
public class Adapter extends FragmentPagerAdapter {
    private final List<Fragment> lists;

    public Adapter(FragmentManager fm, List<Fragment> lists) {
        super(fm);
        this.lists = lists;
    }

    @Override
    public Fragment getItem(int position) {
        return lists.get(position);
    }
    @Override
    public int getCount() {
        return lists.size();
    }
}
