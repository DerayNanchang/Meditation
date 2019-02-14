package com.deray.meditation.module.music.children.me.children.local.children.single;

import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.deray.meditation.R;
import com.deray.meditation.base.BaseFragment;
import com.deray.meditation.base.OnItemListener;
import com.deray.meditation.databinding.FragmentSingleBinding;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.db.manager.music.MusicManager;
import com.deray.meditation.manage.music.AudioPlayerManager;
import com.deray.meditation.module.music.children.me.children.local.children.single.adapter.SingleAdapter;
import com.deray.meditation.ui.dialog.BottomSheetDialogUtils;
import com.deray.meditation.utils.MusicUtils;
import com.deray.meditation.utils.RecyclerViewUtils;
import com.deray.meditation.utils.rx.RxBusManager;

import java.util.ArrayList;
import java.util.List;

public class SingleFragment extends BaseFragment<FragmentSingleBinding> {

    private SingleAdapter adapter;
    private LinearLayoutManager manager;
    private BottomSheetDialog sheetDialog;

    @Override
    protected int getLayoutContentViewID() {
        return R.layout.fragment_single;
    }

    @Override
    protected void init() {
        sheetDialog = new BottomSheetDialog(getActivity());
        List<Music> musics = MusicUtils.getReorganizeLocalMusics(this);
        if (musics.size() == 0){
            if (musics.size() == 0){
                MusicManager.get().scanLocalMusics(this, new MusicManager.OnScanLocalMusicsCallBack() {
                    @Override
                    public void agree(List<Music> musics) {
                        setAlpha(view.llLoading,1,0);
                        view.llLoading.setVisibility(View.GONE);
                        List<Music> newMusics = MusicUtils.setNewMusics(musics);
                        adapter.addData(newMusics);
                        view.includeItemTitle.tvMusicCount.setText(newMusics == null ? "(共" + 0 + "首)" : "(共" + newMusics.size() + "首)");
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void reject() {
                        setAlpha(view.llLoading,1,0);
                        view.llLoading.setVisibility(View.GONE);
                        new ArrayList<>();
                    }
                });
            }
        }else {
            setAlpha(view.llLoading,1,0);
            view.llLoading.setVisibility(View.GONE);
        }
        manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        view.singleContext.setLayoutManager(manager);
        adapter = new SingleAdapter();
        initData(musics, adapter);
        initEvent();
        RxBusManager.get().getMusics(1,0,"","",adapter);
        RxBusManager.get().getScanLocalMusics(adapter,view.includeItemTitle.tvMusicCount);
    }


    private void initEvent() {
        adapter.setOnItemListener(new OnItemListener<Music>() {
            @Override
            public void onItemClick(View view, Music data, int position) {
                MusicUtils.setOnClickMusic(position);
            }
        });

        adapter.setItemMenuClickLinstener(new SingleAdapter.onItemMenuClickListener() {
            @Override
            public void onItemMenuClick(View view, final Music music, int position) {
                // 单曲更多的点击事件
                selectItemMenu(music,position);
            }
        });

        view.singleContext.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
               /* if (mShouldScroll) {
                    mShouldScroll = false;
                    smoothMoveToPosition(view.singleContext, mToPosition);
                }*/
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });
        view.includeItemTitle.llLocate.setOnClickListener(this);
        view.includeItemTitle.rlTitleRoot.setOnClickListener(this);
    }

    private void selectItemMenu(Music music,int position) {
        BottomSheetDialogUtils.show(getActivity(),sheetDialog,music,position);

    }


    private void initData(List<Music> musics, SingleAdapter adapter) {

        if (musics == null) {
            musics = new ArrayList<>();
            if (musics.size() == 0){
                musics = MusicUtils.getReorganizeLocalMusics(this);
            }
        }
        view.includeItemTitle.tvMusicCount.setText(musics == null ? "(共" + 0 + "首)" : "(共" + musics.size() + "首)");
        adapter.addData(musics);
        view.singleContext.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_locate:
                int position = AudioPlayerManager.get().getQueuePosition();
                if (position < 0) {
                    return;
                }
                RecyclerViewUtils.MoveToPosition(manager, position);
                break;

            case R.id.rl_title_root:
                List<Music> music = AudioPlayerManager.get().getQueueMusics();
                if (music != null && music.size() > 0){
                    MusicUtils.setOnClickMusic(0);
                }
                break;
        }
    }

    /**
     * 目标项是否在最后一个可见项之后
     */
    private boolean mShouldScroll;
    /**
     * 记录目标项位置
     */
    private int mToPosition;

    /**
     * 滑动到指定位置
     *
     * @param mRecyclerView
     * @param position
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));

        if (position < firstItem) {
            // 如果跳转位置在第一个可见位置之前，就smoothScrollToPosition可以直接跳转
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 跳转位置在第一个可见项之后，最后一个可见项之前
            // smoothScrollToPosition根本不会动，此时调用smoothScrollBy来滑动到指定位置
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 如果要跳转的位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }

    /**
     * RecyclerView 移动到当前位置，
     *
     * @param manager 设置RecyclerView对应的manager
     * @param n       要跳转的位置
     */
    public void MoveToPosition(LinearLayoutManager manager, int n) {
        manager.scrollToPositionWithOffset(n, 0);
        manager.setStackFromEnd(true);
    }

}
