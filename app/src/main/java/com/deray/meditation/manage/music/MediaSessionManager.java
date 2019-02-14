package com.deray.meditation.manage.music;

import android.content.Context;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.deray.meditation.cache.MusicCacheManager;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.utils.MusicUtils;

public class MediaSessionManager {

    private static final String TAG = "MediaSessionManager";
    private MediaSessionCompat mediaSession;
    private Context context;

    private static final long MEDIA_SESSION_ACTIONS = PlaybackStateCompat.ACTION_PLAY
            | PlaybackStateCompat.ACTION_PAUSE
            | PlaybackStateCompat.ACTION_PLAY_PAUSE
            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            | PlaybackStateCompat.ACTION_STOP
            | PlaybackStateCompat.ACTION_SEEK_TO;

    private MediaSessionManager() {

    }



    private static class MediaSessionManagerInstance {
       private static MediaSessionManager manager = new MediaSessionManager();
    }
    public static MediaSessionManager get(){
       return MediaSessionManagerInstance.manager;
    }

    public void init(Context context){
        this.context = context;
        if (mediaSession == null){
            mediaSession = new MediaSessionCompat(context, TAG);
            mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
            mediaSession.setCallback(callback);
            // 开启
            mediaSession.setActive(true);
        }
    }


    /*public void updatePlaybackState(){

        int state = AudioPlayerManager.get().isPlay() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;

        mediaSession.setPlaybackState(
                new PlaybackStateCompat.Builder()
                        .setActions(MEDIA_SESSION_ACTIONS)
                        .setState(state, AudioPlayerManager.get().getAudioPosition(), 1)
                        .build());
    }*/


    public void updateMetaData(Music music){
        if (music == null){
            mediaSession.setMetadata(null);
            return;
        }

        MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, music.getName())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, music.getSinger())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, music.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, music.getSinger())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, music.getDuration())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, MusicUtils.getLocalMusicBitmap(context, music.getMusicID(), music.getAlbumID()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            metaData.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,MusicCacheManager.get().getCacheMusics().size());
        }
        mediaSession.setMetadata(metaData.build());
    }

    private MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback(){
        @Override
        public void onPlay() {
            AudioPlayerManager.get().playPause();
        }

        @Override
        public void onPause() {
            AudioPlayerManager.get().playPause();
        }

        @Override
        public void onSkipToNext() {
            AudioPlayerManager.get().next();
        }

        @Override
        public void onSkipToPrevious() {
            AudioPlayerManager.get().pre();
        }

        @Override
        public void onStop() {
            AudioPlayerManager.get().stop();
        }

        @Override
        public void onSeekTo(long pos) {
            AudioPlayerManager.get().seekTo((int)pos);
        }
    };
}
