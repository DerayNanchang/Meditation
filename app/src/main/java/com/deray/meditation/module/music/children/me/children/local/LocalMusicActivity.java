package com.deray.meditation.module.music.children.me.children.local;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseActivity;
import com.deray.meditation.databinding.ActivityLocalMusicBinding;
import com.deray.meditation.manage.music.AudioPlayerManager;
import com.deray.meditation.module.main.bean.TabEntity;
import com.deray.meditation.module.music.adapter.MusicFragmentPageAdapter;
import com.deray.meditation.module.music.children.me.children.local.children.album.AlbumFragment;
import com.deray.meditation.module.music.children.me.children.local.children.singer.SingerFragment;
import com.deray.meditation.module.music.children.me.children.local.children.single.SingleFragment;
import com.deray.meditation.module.music.play.PlayActivity;
import com.deray.meditation.module.music.search.ScanLocalMusicActivity;
import com.deray.meditation.module.music.search.SearchMusicActivity;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class LocalMusicActivity extends BaseActivity<ActivityLocalMusicBinding> {

    private String[] titles = new String[]{"单曲","歌手","专辑"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean isAddMusicLayout() {
        return true;
    }


    @Override
    protected int getLayoutContentViewID() {
        return R.layout.activity_local_music;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {

        initViewPage();
        initTabLayout();
        initEvent();
        setSupportActionBar(view.tbLocalToolbar);
    }

    private void initViewPage() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new SingleFragment());
        fragments.add(new SingerFragment());
        fragments.add(new AlbumFragment());

        MusicFragmentPageAdapter adapter = new MusicFragmentPageAdapter(getSupportFragmentManager(), fragments, titles);

        view.vpLocalViewPage.setAdapter(adapter);
        view.vpLocalViewPage.setOffscreenPageLimit(2);
        view.vpLocalViewPage.setCurrentItem(0);
    }

    private void initTabLayout() {

        ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();
        for (String title : titles){
            TabEntity entity = new TabEntity(title);
            tabEntities.add(entity);
        }
        view.ctlLocalTabLayout.setTabData(tabEntities);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_scan_local_music:
                startActivity(new Intent(LocalMusicActivity.this, ScanLocalMusicActivity.class));
                return true;

            case R.id.action_search:
                // 歌曲查询
                startActivity(new Intent(LocalMusicActivity.this,SearchMusicActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initEvent() {
        /**
         *  viewPage
         */
        view.vpLocalViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                view.ctlLocalTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        /**
         *  tabLayout
         */
        view.ctlLocalTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                view.vpLocalViewPage.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        AudioPlayerManager.get().setOnStartActivityListener(new AudioPlayerManager.OnStartActivity() {
            @Override
            public void onStartActivity() {
                explodeTransitionAnimation(LocalMusicActivity.this,PlayActivity.class);
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {

                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_local_music,menu);
        return true;
    }

    @Override
    public void onClick(View v) {

    }
}
