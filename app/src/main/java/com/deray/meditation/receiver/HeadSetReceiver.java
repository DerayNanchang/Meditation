package com.deray.meditation.receiver;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.deray.meditation.cache.MusicCacheManager;
import com.deray.meditation.utils.log.Toast;

public class HeadSetReceiver extends BroadcastReceiver {

    private OnHeadSetListener onHeadSetListener;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (BluetoothProfile.STATE_DISCONNECTED == adapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
                // 蓝牙断开事件
                if (MusicCacheManager.get().getServiceHeadSet()){
                    onHeadSetListener.onBluetoothDisconnected();
                }
            }else {
                if (MusicCacheManager.get().getServiceHeadSet()){
                    onHeadSetListener.onBluetoothConnected();
                }
            }
        } else if ("android.intent.action.HEADSET_PLUG".equals(action)) {
            if (intent.hasExtra("state")){
                if (intent.getIntExtra("state", 0) == 0){
                    if (MusicCacheManager.get().getServiceHeadSet()){
                        onHeadSetListener.onHeadsetDisconnected();
                    }
                }
                else if (intent.getIntExtra("state", 0) == 1){
                    if (MusicCacheManager.get().getServiceHeadSet()){
                        onHeadSetListener.onHeadsetConnected();
                    }
                }
            }
        }
    }

    public void setOnHeadSetListener(OnHeadSetListener onHeadSetListener){
        this.onHeadSetListener = onHeadSetListener;
    }
}
