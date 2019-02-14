package com.deray.meditation.base.text;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.deray.meditation.R;
import com.deray.meditation.config.Constants;
import com.deray.meditation.http.APIS;
import com.deray.meditation.http.HttpManager;
import com.deray.meditation.http.OBTransformer;
import com.deray.meditation.module.music.NeteaseSearch;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    public final static String ACTION_BUTTON = "com.notifications.intent.action.ButtonClick";
    private NotificationManager manager;
    private ButtonBroadcastReceiver bReceiver;
    private NotificationCompat.Builder mBuilder;
    private RemoteViews mRemoteViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        ViewPager page = findViewById(R.id.viewPage);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new Text1Fragment());
        fragments.add(new Text2Fragment());
        page.setAdapter(new Adapter(getSupportFragmentManager(),fragments));
        page.setOffscreenPageLimit(1);

        /*RxPermissions permissions = new RxPermissions(this);
        Disposable disposable = permissions.requestEach(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        try {
                            lyricTest();
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("22222222:"+e.getMessage());
                        }



                    }
                });*/




        //initBuild_O();
        /*initData();
        initButtonReceiver();
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showButtonNotify();
            }
        });*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                System.out.println("ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("ACTION_UP");
                break;
        }
        return true;
    }

    private void lyricTest() throws IOException {
        Disposable subscribe = HttpManager.request().get(APIS.class).getSearchNeteaseMusic(HttpManager.Keys.ML_WEI_KEY, "抖音热歌", "so", 0, 10)
                .compose(OBTransformer.<NeteaseSearch>toMain())
                .subscribe(new Consumer<NeteaseSearch>() {
                    @Override
                    public void accept(NeteaseSearch neteaseSearch) throws Exception {
                        //System.out.println("数据："+JSON.toJSONString(neteaseSearch));

                        NeteaseSearch.BodyBean bean = neteaseSearch.getBody().get(0);
                        Request request = new Request.Builder().url(bean.getLrc())
                                .get()
                                .build();

                        OkHttpClient client = new OkHttpClient();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                                File file = new File(Environment.getExternalStorageDirectory(), "lyricTest");
                                if (!file.exists()){
                                    file.mkdir();
                                }

                                File lrcFile = new File(file.getAbsolutePath(), "lyric.lrc");
                                if (!lrcFile.exists()){
                                    boolean mkdir = lrcFile.createNewFile();
                                    System.out.println(mkdir);
                                }

                                if (response.body() != null){
                                    String string = response.body().string();
                                    method2(lrcFile.getAbsolutePath(),string);
                                }
                            }
                        });



                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 追加文件：使用FileWriter
     *
     * @param fileName
     * @param content
     */
    public static void method2(String fileName, String content) {
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(fileName);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initBuild_O() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = Constants.Config.NOTIFICATION_CHANNELID_MUSIC_CONTROLLER;
            String channelName = "music controller";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    private void initData() {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_standard_view);
    }

    /**
     * 带按钮的通知栏点击广播接收
     */
    public void initButtonReceiver() {
        bReceiver = new ButtonBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BUTTON);
        registerReceiver(bReceiver, intentFilter);
    }

    public void showButtonNotify(){
        mRemoteViews.setImageViewResource(R.id.ivIcon, R.mipmap.icon_default);
        //API3.0 以上的时候显示按钮。否则消失
        mRemoteViews.setTextViewText(R.id.tvName, "化身孤岛的鲸");
        mRemoteViews.setTextViewText(R.id.tvSingle, "不才 - 翻唱");
        mRemoteViews.setImageViewResource(R.id.ivPlay, R.drawable.svg_pause_4);
        /*if(isPlay){
            mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.drawable.btn_pause);
        }else{
            mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.drawable.btn_play);
        }*/
        //点击的事件处理
        Intent buttonIntent = new Intent(ACTION_BUTTON);
        /* 上一首按钮 */
        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PREV_ID);
        //这里加了广播，所及INTENT的必须用getBroadcast方法
        PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.ivPre, intent_prev);

        /* 播放/暂停  按钮 */
        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PALY_ID);
        PendingIntent intent_paly = PendingIntent.getBroadcast(this, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.ivPlay, intent_paly);
        /* 下一首 按钮  */
        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_NEXT_ID);
        PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.ivNext, intent_next);

        mBuilder.setCustomContentView(mRemoteViews)
                //.setCustomBigContentView() 大图时的布局
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker("正在播放")
                .setPriority(Notification.PRIORITY_MAX)// 设置该通知优先级
                .setOngoing(true)
                .setSmallIcon(R.mipmap.icon_default);
        manager.notify(1, mBuilder.build());
    }

    public final static String INTENT_BUTTONID_TAG = "ButtonId";
    /** 上一首 按钮点击 ID */
    public final static int BUTTON_PREV_ID = 1;
    /** 播放/暂停 按钮点击 ID */
    public final static int BUTTON_PALY_ID = 2;
    /** 下一首 按钮点击 ID */
    public final static int BUTTON_NEXT_ID = 3;


    /**
     *	 广播监听按钮点击时间
     */
    public class ButtonBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ACTION_BUTTON)){
                //通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
                int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
                switch (buttonId) {
                    case BUTTON_PREV_ID:
                        Toast.makeText(getApplicationContext(), "上一首", Toast.LENGTH_SHORT).show();
                        break;
                    case BUTTON_PALY_ID:
                        String play_status = "";
                        play_status = "开始播放";
                        /*if(isPlay){
                            play_status = "开始播放";
                        }else{
                            play_status = "已暂停";
                        }*/
                        //showButtonNotify();
                        Toast.makeText(getApplicationContext(), play_status, Toast.LENGTH_SHORT).show();
                        break;
                    case BUTTON_NEXT_ID:
                        Toast.makeText(getApplicationContext(), "下一首", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bReceiver != null) {
            unregisterReceiver(bReceiver);
        }
    }

    @Override
    public void onClick(View v) {
    }
}
