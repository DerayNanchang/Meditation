package com.deray.meditation.module.music.children.me;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.deray.meditation.R;
import com.deray.meditation.base.BaseFragment;
import com.deray.meditation.config.Constants;
import com.deray.meditation.databinding.FragmentMeBinding;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.db.bean.PlayList;
import com.deray.meditation.db.manager.music.MusicManager;
import com.deray.meditation.db.manager.playlist.PlayListManager;
import com.deray.meditation.manage.view.DialogManager;
import com.deray.meditation.module.music.MusicMenuActivity;
import com.deray.meditation.module.music.children.me.adapter.PlayListAdapter;
import com.deray.meditation.module.music.children.me.adapter.PlayListDialogAdapter;
import com.deray.meditation.module.music.children.me.bean.PlayListBean;
import com.deray.meditation.module.music.children.me.bean.PlayListDialog;
import com.deray.meditation.module.music.children.me.bean.PlayListTypeBean;
import com.deray.meditation.module.music.children.me.children.collection.CollectionMusicActivity;
import com.deray.meditation.module.music.children.me.children.down.DownLocalMusicActivity;
import com.deray.meditation.module.music.children.me.children.local.LocalMusicActivity;
import com.deray.meditation.module.music.children.me.children.recent.RecentMusicActivity;
import com.deray.meditation.module.music.children.me.presenter.MePresenter;
import com.deray.meditation.module.music.children.me.view.MeView;

import java.util.ArrayList;
import java.util.List;

public class MeFragment extends BaseFragment<FragmentMeBinding> implements MeView {

    private PlayListAdapter adapter;
    private MePresenter presenter;
    private List<PlayList> playlists;
    private List<PlayListTypeBean> playListTypeBeanList;
    private List<PlayListBean> diyPlayListBeanList;
    private List<List<PlayListBean>> playListData;
    private BottomSheetDialog childItemMenu;

    @Override
    protected int getLayoutContentViewID() {
        return R.layout.fragment_me;
    }

    @Override
    protected void init() {
        childItemMenu = new BottomSheetDialog(getActivity());
        presenter = new MePresenter(this);
        adapter = new PlayListAdapter(getActivity());
        updateData(1);
        initViewMusicHomeItem();
        initEvent();

    }
    private void initEvent() {
        // 普通的点击事件
        view.vmhtMusicMeLocalMusics.setOnClickListener(this);
        view.vmhtMusicMeRecentlyPlayed.setOnClickListener(this);
        view.vmhtMusicMeDownloadManagement.setOnClickListener(this);
        view.vmhtMusicMeCollection.setOnClickListener(this);

        // title 更多点击事件
        adapter.setBottomSheetItemOnClick(new PlayListAdapter.SheetItemClick() {
            @Override
            public void onSheetClick(View view, BottomSheetDialog bottomSheetDialog) {
                switch (view.getId()) {
                    case R.id.rl_bottom_dialog_add:
                        bottomSheetDialog.dismiss();
                        // 新建歌单的 adapter
                        onCreateMusicList();
                        break;

                    case R.id.rl_bottm_dialog_management:
                        bottomSheetDialog.dismiss();
                        break;
                }
            }
        });

        view.elvMusicMineContent.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (diyPlayListBeanList != null && diyPlayListBeanList.size() > 0) {
                    String playlistName = diyPlayListBeanList.get(childPosition).getName();
                    Intent intent = new Intent(getContext(), MusicMenuActivity.class);
                    intent.putExtra(Constants.Music.MUSIC_MENU_ACTIVITY_KEY_MUSIC_LIST_INFO, playlistName);
                    startActivity(intent);
                }
                return true;
            }
        });

        // 子 item 更多点击事件
        adapter.setChildItemClick(new PlayListAdapter.ChildItemClick<PlayListBean>() {
            @Override
            public void onChildItemClick(View view, PlayListBean data, int groupPosition, int childPosition) {

                final String nowClickName = data.getName();
                String title = "歌单 ：" + nowClickName;
                int[] icons = new int[]{R.drawable.svg_home_download_3, R.drawable.svg_shear, R.drawable.svg_edit, R.drawable.svg_delete};
                String[] names = new String[]{"下载", "分享", "编辑歌单信息", "删除"};
                PlayListDialogAdapter adapter = new PlayListDialogAdapter();
                DialogManager.get().showMenu(getContext(), title, icons, names, adapter, childItemMenu);
                adapter.setItemMenuListener(new PlayListDialogAdapter.OnItemMenuClick() {
                    @Override
                    public void OnItemMenuClick(View v, PlayListDialog playListDialog, int position) {
                        switch (position) {

                            case 0:
                                // 下载
                                break;

                            case 1:
                                // 分享

                                break;

                            case 2:
                                // 编辑歌单信息
                                /*Intent intent = new Intent(getActivity(), UpdatePlayListActivity.class);
                                intent.putExtra(TravelConfig.IMusicConfig.KEY_ACTION_UPDATE_PLAY, nowClickName);
                                childItemMenu.dismiss();
                                startActivity(intent);*/
                                break;
                            case 3:
                                // 删除歌单
                                /*AlertDialogManager.get().deletePlayList(getActivity(), "删除歌单", "确定删除歌单吗？", new AlertDialogManager.OnDialogCallBack() {
                                    @Override
                                    public void onDialogCallBack() {
                                        presenter.deletePlayList(nowClickName);
                                        updateData(2);
                                    }
                                });
                                childItemMenu.dismiss();*/
                                break;
                        }
                    }
                });

            }
        });
    }

    private void initViewMusicHomeItem() {
        view.vmhtMusicMeLocalMusics.setSrc(R.drawable.svg_home_music_2);
        view.vmhtMusicMeRecentlyPlayed.setSrc(R.drawable.svg_home_recently_3);
        view.vmhtMusicMeDownloadManagement.setSrc(R.drawable.svg_home_download_3);
        view.vmhtMusicMeCollection.setSrc(R.drawable.svg_home_collect_3);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.vmht_music_me_localMusics:
                startActivity(new Intent(getActivity(),LocalMusicActivity.class));
                break;
            case R.id.vmht_music_me_recentlyPlayed:
                startActivity(new Intent(getActivity(),RecentMusicActivity.class));
                break;
            case R.id.vmht_music_me_downloadManagement:
                startActivity(new Intent(getActivity(),DownLocalMusicActivity.class));
                break;
            case R.id.vmht_music_me_Collection:
                startActivity(new Intent(getActivity(),CollectionMusicActivity.class));
                break;
        }
    }

    private void onCreateMusicList() {
        final View view = LayoutInflater.from(this.getContext()).inflate(R.layout.item_view_create_music_list, null, false);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext())
                .setView(view);

        final TextView submit = view.findViewById(R.id.tv_create_submit);
        final EditText content = view.findViewById(R.id.et_create_music_list);
        final TextView size = view.findViewById(R.id.et_create_music_list_size);
        final TextView cancel = view.findViewById(R.id.tv_create_cancel);
        final AlertDialog dialog = builder.show();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 插入数据库
                if (content != null && content.length() > 0) {
                    // 判断是否已经初始化了
                    createPlayList(content.getText().toString().trim());
                    updateData(2);
                    dialog.dismiss();
                }
            }
        });


        // 一定要放在 onClick 之后调用否则不起作用
        submit.setClickable(false);

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 计算数值长度 显示亮度
                if (s != null && s.length() != 0) {
                    submit.setClickable(true);
                    submit.setTextColor(getResources().getColor(R.color.colorAccent));
                    size.setTextColor(getResources().getColor(R.color.black));
                    size.setText(s.length() + "/40");
                } else {
                    submit.setTextColor(getResources().getColor(R.color.themeColor88));
                    size.setTextColor(getResources().getColor(R.color.gray88));
                    submit.setClickable(false);
                    size.setText(0 + "/40");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 取消
                dialog.dismiss();
            }
        });
    }

    /**
     * 创建歌单
     *
     * @param playName
     */
    public void createPlayList(String playName) {
        PlayList playlist = new PlayList();
        playlist.setName(playName);
        playlist.setCreateTime(System.currentTimeMillis());
        PlayListManager.get().insertPlayList(playlist);
    }

    @Override
    public void onResume() {
        super.onResume();

        PlayListManager.get().clearCache();
        updateData(1);
        // 展开
        int count = view.elvMusicMineContent.getCount();
        for (int i = 0; i < count; i++) {
            view.elvMusicMineContent.expandGroup(i);
        }
    }

    private void updateData(int tag) {
        showData();
        if (adapter != null){
            adapter.addData(playListTypeBeanList, playListData);
            if (tag == 1){
                view.elvMusicMineContent.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void showData() {
        initPlayListType();
        initPlayList();
    }

    private void initPlayListType() {
        playlists = PlayListManager.get().getAllPlayList();
        if (playlists != null && presenter != null){
            playListTypeBeanList = presenter.initPlayLists(playlists.size());
        }
    }

    private void initPlayList() {
        // 目前只有本地音乐
        diyPlayListBeanList = new ArrayList<>();
        for (PlayList playlist : playlists) {
            PlayListBean playListBean = new PlayListBean();
            playListBean.setName(playlist.getName());
            playListBean.setIcon(playlist.getIcon());
            List<Music> localMusics = new ArrayList<>();
            for (Music music : playlist.getMusics()) {
                if (music.getType() == MusicManager.MusicType.LOCAL) {
                    // 本地音乐是下载过了的
                    localMusics.add(music);
                }
            }
            System.out.println(JSON.toJSONString(playlist));
            playListBean.setMusicTotal(playlist.getMusics().size() + "");
            playListBean.setMusicDownNum(localMusics.size() + "");
            diyPlayListBeanList.add(playListBean);
        }

        playListData = new ArrayList<>();
        playListData.add(diyPlayListBeanList);
    }
}
