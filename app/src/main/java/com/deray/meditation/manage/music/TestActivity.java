package com.deray.meditation.manage.music;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.deray.meditation.R;
import java.io.IOException;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        String url  = "/storage/emulated/0/netease/cloudmusic/Music/Audio Machine - Breath and Life.mp3";

        try {
            final MediaPlayer player = new MediaPlayer();
            player.reset();
            player.setDataSource(url);
            player.prepareAsync();
            //MediaSessionManager.get().updateMetaData(music);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    player.start();
                    //state = AudioPlayerManager.STATE.PLAY;
                    //MediaSessionManager.get().updatePlaybackState();
                    //System.out.println("是否正在播放："+player.isPlaying());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
