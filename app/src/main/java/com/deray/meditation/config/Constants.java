package com.deray.meditation.config;

import android.provider.MediaStore;

public interface Constants {

     interface Music{
         String PLAY_ACTIVITY_KEY_MUSIC_PROGRESS = "MUSIC_PROGRESS";
         String PLAY_ACTIVITY_KEY_MUSIC_DURATION = "KEY_MUSIC_DURATION";
         String PLAY_ACTIVITY_KEY_TIME = "TIME";
         String PLAY_ACTIVITY_TIMING_SERVICE = "com.deray.meditation.service.music.TimingService";

         String MUSIC_INFO_ACTIVITY_KEY_NAME = "NAME";
         String MUSIC_INFO_ACTIVITY_KEY_TAG = "TAG";
         int MUSIC_INFO_ACTIVITY_KEY_TAG_SINGER = 1;
         int MUSIC_INFO_ACTIVITY_KEY_TAG_ALBUM = 2;

         String MUSIC_MENU_ACTIVITY_KEY_MUSIC_LIST_INFO = "KEY_MUSIC_LIST_INFO";



         // 歌词目录
         //String lyricDir = Environment.getExternalStorageDirectory() + "\meditation\";
     }

     interface Config{
         String NOTIFICATION_CHANNELID_MUSIC_CONTROLLER = "musicController";
    }


     interface Manager{
         String MUSIC_MANAGER_BY_ID = MediaStore.Audio.Media._ID + " = ? and " + MediaStore.Audio.Media.ALBUM_ID + " = ? and " + MediaStore.Audio.Media.ARTIST_ID + " = ?";

         String AUDIO_PLAY_MANAGER_ORDER = "ORDER";
         String AUDIO_PLAY_MANAGER_RANDOM = "RANDOM";
         String AUDIO_PLAY_MANAGER_CIRCULATION = "CIRCULATION";
         String AUDIO_PLAY_MANAGER_ACTION_BUTTON = "ACTION_BUTTON";
         String AUDIO_PLAY_MANAGER_INTENT_BUTTONID_TAG = "ButtonId";
         /** 上一首 按钮点击 ID */
         int AUDIO_PLAY_MANAGER_BUTTON_PREV_ID = 1;
         /** 播放/暂停 按钮点击 ID */
         int AUDIO_PLAY_MANAGER_BUTTON_PALY_ID = 2;
         /** 下一首 按钮点击 ID */
         int AUDIO_PLAY_MANAGER_BUTTON_NEXT_ID = 3;
     }
}
