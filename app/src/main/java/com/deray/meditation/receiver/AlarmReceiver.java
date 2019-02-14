package com.deray.meditation.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.deray.meditation.manage.music.AudioPlayerManager;
import com.deray.meditation.service.music.TimingService;
import com.deray.meditation.utils.log.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // 终止停止播放音乐,及相关动画
        AudioPlayerManager.get().headSetPause();

        context.stopService(new Intent(context,TimingService.class));
    }
}
