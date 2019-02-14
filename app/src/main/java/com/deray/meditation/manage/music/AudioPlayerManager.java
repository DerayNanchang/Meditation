package com.deray.meditation.manage.music;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import com.deray.meditation.cache.MusicCacheManager;
import com.deray.meditation.config.Constants;
import com.deray.meditation.db.bean.Music;
import com.deray.meditation.http.APIS;
import com.deray.meditation.http.HttpManager;
import com.deray.meditation.http.OBTransformer;
import com.deray.meditation.manage.ServiceManager;
import com.deray.meditation.module.music.Lyric;
import com.deray.meditation.module.music.NeteaseSearch;
import com.deray.meditation.module.music.play.PlayActivity;
import com.deray.meditation.utils.MusicUtils;
import com.deray.meditation.utils.StringUtils;
import com.deray.meditation.utils.log.Toast;
import com.deray.meditation.utils.rx.RxBusManager;
import com.deray.meditation.utils.rx.bean.PlayMusic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AudioPlayerManager implements MediaPlayer.OnCompletionListener {

    private MediaPlayer player;
    private STATE state = STATE.IDLE;
    public String playMode = Constants.Manager.AUDIO_PLAY_MANAGER_ORDER;
    private boolean isController = false;
    private int NEXT = -10;
    private int PRE = -12;
    private Context context;
    private Music playMusic;
    private Timer timer;
    private OnFinish playFinish;
    private OnStartActivity onStartActivity;
    private List<Lyric.LyricInfo> infos = new ArrayList<>();
    private File lyric;
    private OnLyricCallBack onLyricCallBack;


    private enum STATE {
        // 闲置，播放，暂停
        IDLE, PLAY, PAUSE
    }

    public Music getPlayMusic() {
        return playMusic;
    }

    public enum PLAY_MODE {
        // 顺序，随机，循环
        ORDER, RANDOM, CIRCULATION
    }

    private AudioPlayerManager() {
    }


    private static class AudioPlayInstance {
        private static AudioPlayerManager audioPlayer = new AudioPlayerManager();
    }

    public static AudioPlayerManager get() {
        return AudioPlayInstance.audioPlayer;
    }

    /**
     * 初始化参数
     */
    public void init(Context context) {
        this.context = context;
        if (player == null) {
            initPlay();
        }
    }

    private void initPlay() {
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
    }

    public int getQueuePosition() {
        return MusicCacheManager.get().getQueueMusicPosition();
    }

    public List<Music> getQueueMusics() {
        return MusicCacheManager.get().getQueueMusics();
    }

    public List<Music> getNewQueueMusic() {
        return MusicCacheManager.get().getNewQueueMusics();
    }


    // 点击的是其他歌曲  or 点击的是正在唱的歌曲
    public void playPause() {
        int position = getQueuePosition();
        List<Music> musics = getQueueMusics();
        if (musics == null || musics.size() <= 0) {
            return;
        }
        // 1.还没有播放过音乐
        if (isIdle()) {
            play(position);
        } else if (isPlay()) {
            // 是否是控制器播放
            if (isController) {
                // 控制器 暂停
                pause();
            } else {
                // item 点击
                if (isPlayExist()) {
                    //context.startActivity(new Intent(context, PlayActivity.class));
                    onStartActivity.onStartActivity();
                } else {
                    play(position);
                }
            }
        } else {
            // 暂停
            if (isController) {
                start();
            } else {
                // item 点击
                if (isPlayExist()) {
                    //context.startActivity(new Intent(context, PlayActivity.class));
                    onStartActivity.onStartActivity();
                } else {
                    play(position);
                }
            }
        }
    }


    public interface OnStartActivity {
        void onStartActivity();
    }

    public void setOnStartActivityListener(OnStartActivity onStartActivity) {
        this.onStartActivity = onStartActivity;
    }


    private void start() {
        player.start();
        state = STATE.PLAY;
    }


    private void pause() {
        player.pause();
        state = STATE.PAUSE;
    }


    public void headSetStart() {
        /*Toast.get().show(player+"");
        if (player != null){
            if (isIdle()){
                int position = getQueuePosition();
                play(position);
            }else {
                start();
            }
            // 发送事件
            RxBusManager.get().setMusicPlayState(isPlay());
        }*/
    }

    public void headSetPause() {
        if (player != null) {
            if (!isIdle()) {
                pause();
                // 发送事件
                RxBusManager.get().setMusicPlayState(isPlay());
            }
        }
    }


    public void seekTo(int progress) {
        player.seekTo(progress);
    }

    /**
     * next
     */
    public void next() {
        List<Music> musics = getQueueMusics();
        if (musics == null) {
            return;
        }
        if (musics.size() == 0) {
            return;
        }
        int position = 0;
        switch (playMode) {
            case Constants.Manager.AUDIO_PLAY_MANAGER_ORDER:
                position = order(NEXT);
                break;
            case Constants.Manager.AUDIO_PLAY_MANAGER_RANDOM:
               /* //1. 获取当前播放的 position
                int oldPosition = position;

                //2. 获取心的 position，随机数
                position = random();
                //3. 获取 随机播放队列
                List<Music> random = MusicCacheManager.get().getWroughtMusics();
                if (oldPosition == random.size() - 1){
                    // 最后一首歌 下一首应该置为 0
                    Collections.swap(random,);
                }*/
                position = random();
                break;
            case Constants.Manager.AUDIO_PLAY_MANAGER_CIRCULATION:
                position = circulation(NEXT);
                break;
        }
        play(position);
    }


    /**
     * next
     */
    public void pre() {
        List<Music> musics = getQueueMusics();
        if (musics == null) {
            return;
        }
        if (musics.size() == 0) {
            return;
        }
        int position = 0;
        switch (playMode) {
            case Constants.Manager.AUDIO_PLAY_MANAGER_ORDER:
                position = order(PRE);
                break;
            case Constants.Manager.AUDIO_PLAY_MANAGER_RANDOM:
                position = random();
                break;
            case Constants.Manager.AUDIO_PLAY_MANAGER_CIRCULATION:
                position = circulation(PRE);
                break;
        }
        play(position);
    }

    public void stop() {
        player.stop();
        state = STATE.IDLE;
    }

    public long getAudioPosition() {
        if (isPlay() || isPause()) {
            return player.getCurrentPosition();
        } else {
            return 0;
        }
    }


    // 顺序，随机，循环
    //ORDER,RANDOM,CIRCULATION
    public int order(int tag) {
        List<Music> musics = getQueueMusics();
        int position = getQueuePosition();
        if (tag == NEXT) {
            if (position == musics.size() - 1) {
                position = 0;
            } else {
                position = position + 1;
            }
            return position;
        } else {
            if (position == 0) {
                position = musics.size() - 1;
            } else {
                position = position - 1;
            }
            return position;
        }
    }

    public int random() {
        List<Music> musics = getQueueMusics();
//        int position = getQueuePosition();
        Random random = new Random();
        return random.nextInt(musics.size());
    }

    public int circulation(int tag) {
        int position = getQueuePosition();
        if (isController) {
            return order(tag);
        } else {
            return position;
        }
    }

    public String getPlayMode() {
        return playMode;
    }

    public boolean isPlay() {
        return state == STATE.PLAY;
    }

    public boolean isPause() {
        return state == STATE.PAUSE;
    }

    public boolean isIdle() {
        return state == STATE.IDLE;
    }

    public boolean isController() {
        return isController;
    }

    public void setController(boolean controller) {
        isController = controller;
    }

    public boolean isPlayExist() {
        List<Music> newQueueMusic = getNewQueueMusic();
        int position = getQueuePosition();
        if (newQueueMusic != null && playMusic != null) {
            if (newQueueMusic.get(position) != null) {
                if (newQueueMusic.get(position).getName().equals(playMusic.getName()) && newQueueMusic.get(position).getSinger().equals(playMusic.getSinger())) {
                    // 同一首歌
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    /**
     * 播放
     *
     * @param position
     */
    public void play(int position) {
        if (context == null) {
            return;
        }
        if (getNewQueueMusic() == null || getNewQueueMusic().size() == 0) {
            return;
        }

        if (position >= getNewQueueMusic().size()) {
            return;
        }

        MusicCacheManager.get().setQueueMusicPosition(position);
        final Music music = getNewQueueMusic().get(position);
        if (music == null) {
            return;
        }
        if (!ServiceManager.get().isServiceExisted(context, ServiceManager.ServiceNames.MUSIC_SERVICE)) {
            ServiceManager.get().startService(context);
        }
        try {
            player.reset();
            player.setDataSource(music.getMusicFile());
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    state = STATE.PLAY;
                    player.start();
                    playMusic = music;

                    // 添加历史歌单队列
                    MusicCacheManager.get().setHistoryMusic(music);
                    // 添加缓存音乐
                    MusicCacheManager.get().setCacheMusic(music);
                    List<Music> musics = MusicCacheManager.get().getNewQueueMusics();
                    for (Music musicList : musics) {
                        // 当前正在播放的歌曲
                        if (musicList.getName().equals(music.getName()) && musicList.getSinger().equals(music.getSinger())) {
                            musicList.setIsPlay(true);
                            RxBusManager.get().setMusics(musicList);
                        } else {
                            musicList.setIsPlay(false);
                        }
                    }
                    PlayMusic playMusic = getPlayMusic(music);
                    RxBusManager.get().setPlayMusicInfo(playMusic);

                    // 加载一个计时器 显示最新的进度
                    if (PlayActivity.handler != null) {
                        addTimer();
                    }

                    File meditation = new File(Environment.getExternalStorageDirectory(), "meditation");
                    if (!meditation.exists()) {
                        meditation.mkdir();
                    }
                    lyric = new File(meditation.getAbsolutePath(), "lyric");
                    if (!lyric.exists()) {
                        lyric.mkdir();
                    }

                    // 获取歌词
                    //if (MusicUtils.getLyric(music.getName()))
                    boolean exist = MusicUtils.lyricExist(music.getName());
                    if (infos.size() > 0) {
                        infos.clear();
                    }
                    if (exist) {
                        // 存在直接读取本地歌词
                        setInfo(music.getName());
                    } else {
                        // 如果本地不存在  去请求网络数据 获取歌词
                        setMusicInfo(music.getName(), music.getSinger());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("异常 ： " + e.getMessage());
            Toast.get().show("当前歌曲无法播放");
        }
    }

    private void addTimer() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // 更新当前播放音乐信息
                    updatePlayState(playMusic);
                    //getTopActivity(context);
                    Message message = PlayActivity.handler.obtainMessage();
                    Bundle data = message.getData();
                    if (getCurrentPosition() < 10000000) {
                        data.putInt(Constants.Music.PLAY_ACTIVITY_KEY_MUSIC_PROGRESS, getCurrentPosition());
                        data.putInt(Constants.Music.PLAY_ACTIVITY_KEY_MUSIC_DURATION, getDuration());
                    }
                    message.setData(data);
                    PlayActivity.handler.sendMessage(message);
                }
            }, 0, 500);
        }
    }

    public void setSeekTo(int progress) {
        player.seekTo(progress);
    }


    //判断当前界面显示的是哪个Activity
    public static String getTopActivity(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        Log.d("Chunna.zheng", "pkg:" + cn.getPackageName());//包名
        Log.d("Chunna.zheng", "cls:" + cn.getClassName());//包名加类名
        System.out.println("cls:" + cn.getClassName());
        return cn.getClassName();
    }

    public String getDurationStr() {
        if (player == null) {
            Music music = MusicCacheManager.get().getMusic();
            if (music != null) {
                return StringUtils.formatTime("mm:ss", music.getDuration());
            } else {
                return "00:00";
            }
        } else {
            return StringUtils.formatTime("mm:ss", getEffectiveDuration());
        }
    }

    public int getEffectiveDuration() {
        int duration = player.getDuration();
        if (duration < 10000000 && duration > 10) {
            return duration;
        } else {
            return 0;
        }
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public int getDuration() {
        if (player == null) {
            Music music = MusicCacheManager.get().getMusic();
            if (music != null) {
                return music.getDuration().intValue();
            } else {
                return 0;
            }
        } else {
            return getEffectiveDuration();
        }
    }

    public int getCurrentPosition() {
        if (player == null) {
            return 0;
        } else {
            return player.getCurrentPosition();
        }
    }

    public String getCurrentPositionStr() {
        return StringUtils.formatTime("mm:ss", player.getCurrentPosition());
    }

    private void updatePlayState(Music music) {
        PlayMusic playMusic = getPlayMusic(music);
        RxBusManager.get().setPlayMusicProgress(playMusic);
    }

    @NonNull
    private PlayMusic getPlayMusic(Music music) {
        PlayMusic playMusic = new PlayMusic();
        playMusic.setIcon(music.getMusicIcon());
        playMusic.setMusicLyric("");
        playMusic.setMusicName(music.getName());
        playMusic.setSinger(music.getSinger());
        playMusic.setPlayStatus(isPlay());
        playMusic.setProgress(getCurrentPosition());
        playMusic.setDuration(getDuration());
        playMusic.setDurationStr(getDurationStr());
        return playMusic;
    }

    private long time;

    /**
     * 播放完成 or 播放异常回调方法
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (isNext(mp)) {
            // 下一曲
            // 自动播放完成
            isController = false;
            state = STATE.PLAY;
            if (playFinish != null) {
                playFinish.onFinish();
            }
        }
    }

    public boolean isNext(MediaPlayer mp) {
        long timeMillis = SystemClock.currentThreadTimeMillis();
        if (timeMillis - time > 200) {
            // 播放完成回调方法  自动下一曲播放并且 控制器置为 false
            // 终端播放 也会执行该方法
            isController = false;
            // 说明播放歌曲完成
            if (mp.getDuration() < 10000000 && mp.getDuration() > 10) {
                if (getDuration() - getCurrentPosition() < 600) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            time = timeMillis;
            return false;
        }
    }

    public void setOnFinish(OnFinish playFinish) {
        this.playFinish = playFinish;
    }

    public interface OnFinish {
        void onFinish();
    }


    public void setPlayMode(String playMode) {
        this.playMode = playMode;
    }


    public void updatePlayModeState() {
        String playMode = getPlayMode();
        switch (playMode) {
            case Constants.Manager.AUDIO_PLAY_MANAGER_ORDER:
                this.playMode = Constants.Manager.AUDIO_PLAY_MANAGER_RANDOM;
                break;
            case Constants.Manager.AUDIO_PLAY_MANAGER_RANDOM:
                this.playMode = Constants.Manager.AUDIO_PLAY_MANAGER_CIRCULATION;
                break;
            case Constants.Manager.AUDIO_PLAY_MANAGER_CIRCULATION:
                this.playMode = Constants.Manager.AUDIO_PLAY_MANAGER_ORDER;
                break;
        }
        MusicCacheManager.get().savePlayMode(this.playMode);
    }

    public void onDestroy() {
        player.reset();
        player = null;
        state = STATE.IDLE;
        isController = false;
    }

/*3 durationHint 获得焦点的时间长短
    在AudioManager中定义了四种类型

            AUDIOFOCUS_GAIN //长时间获得焦点
    AUDIOFOCUS_GAIN_TRANSIENT //短暂性获得焦点，用完应立即释放
            AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK //短暂性获得焦点并降音，可混音播放
    AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE //短暂性获得焦点，录音或者语音识别*/

    private AudioManager.OnAudioFocusChangeListener mAudioFocusChange = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    //长时间丢失焦点,当其他应用申请的焦点为AUDIOFOCUS_GAIN时，
                    //会触发此回调事件，例如播放QQ音乐，网易云音乐等
                    //通常需要暂停音乐播放，若没有暂停播放就会出现和其他音乐同时输出声音
                    //Log.d(TAG, "AUDIOFOCUS_LOSS");
                    headSetPause();
                    //释放焦点，该方法可根据需要来决定是否调用
                    //若焦点释放掉之后，将不会再自动获得
                    //mAudioManager.abandonAudioFocus(mAudioFocusChange);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    //短暂性丢失焦点，当其他应用申请AUDIOFOCUS_GAIN_TRANSIENT或AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE时，
                    //会触发此回调事件，例如播放短视频，拨打电话等。
                    //通常需要暂停音乐播放
                    headSetPause();
                    //Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //短暂性丢失焦点并作降音处理
                    //Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    //当其他应用申请焦点之后又释放焦点会触发此回调
                    //可重新播放音乐
                    //Log.d(TAG, "AUDIOFOCUS_GAIN");
                    headSetStart();
                    break;
            }
        }
    };

    public void setMusicInfo(final String name, final String author) {

        Disposable subscribe = HttpManager.request().get(APIS.class)
                .getSearchNeteaseMusic(HttpManager.Keys.ML_WEI_KEY, name, "so", 0, 10)
                .compose(OBTransformer.<NeteaseSearch>toMain())
                .subscribe(new Consumer<NeteaseSearch>() {
                    @Override
                    public void accept(NeteaseSearch neteaseSearch) throws Exception {
                        if (neteaseSearch.getCode().equals("OK")) {
                            List<NeteaseSearch.BodyBean> body = neteaseSearch.getBody();
                            for (NeteaseSearch.BodyBean bodyBean : body) {
                                if (bodyBean.getTitle().equals(name) && bodyBean.getAuthor().equals(author)) {
                                    // 去请求歌词
                                    Request request = new Request.Builder().url(bodyBean.getLrc())
                                            .get()
                                            .build();

                                    OkHttpClient client = new OkHttpClient();
                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            String msg = "无法检索到对应歌曲...";
                                            setLyricMsg(msg);
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {

                                            File lrcFile = new File(lyric.getAbsolutePath(), name + ".lrc");
                                            if (!lrcFile.exists()) {
                                                boolean mkdir = lrcFile.createNewFile();
                                            }

                                            if (response.body() != null) {
                                                String string = response.body().string();
                                                FileWriter writer = null;
                                                try {
                                                    writer = new FileWriter(lrcFile.getAbsolutePath());
                                                    writer.write(string);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                } finally {
                                                    try {
                                                        if (writer != null) {
                                                            writer.close();
                                                        }
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                            setInfo(name);
                                        }
                                    });
                                    return;
                                }
                            }
                        }else {
                            String msg = "无法检索到对应歌曲...";
                            setLyricMsg(msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        // 网络异常
                        String msg = "无网络无法下载歌词...";
                        setLyricMsg(msg);

                    }
                });
    }


    private void setLyricMsg(String msg){
        Lyric lyric = new Lyric();
        List<Lyric.LyricInfo> list = new ArrayList<>();
        Lyric.LyricInfo info = new Lyric.LyricInfo(0, msg);
        list.add(info);
        lyric.setLyricInfoList(list);
        MusicCacheManager.get().saveLyric(lyric);
    }

          /*[by:雪之原]
            [ti:]
            [ar:]
            [al:]
            [by:九九Lrc歌词网～www.99Lrc.net]
            [00:00.00]
            [00:15.28]東京に出てきてから
            [00:18.38]いくつも春が過ぎました
            [00:22.56]あなたの夢はもう叶いましたか?*/

    private void setInfo(String name) {
        try {
            File file = new File(MusicUtils.getLyric(name));
            BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
            String s;
            Lyric lyric = new Lyric();
            while ((s = reader.readLine()) != null) {
                // 过滤掉非正常数据,只要 by 歌词时间,歌词内容
                if (s.contains("by") || s.contains("0")) {

                    // 解析出所有者
                    if (s.contains("by")) {
                        String by = s.substring(1, s.length() - 1);
                        lyric.setBy(by);
                        continue;
                    }

                    // 解析歌词
                    //[00：00.00 组成：doriko
                    String[] split = s.split("]");
                    // 先把歌词拿出来
                    String lyricStr = split[split.length - 1];
                    // 再去解析时间
                    for (int i = 0; i < split.length - 1; i++) {
                        int time = parseTime(split[i]);
                        // 创建对象添加进去
                        Lyric.LyricInfo info = new Lyric.LyricInfo();
                        info.setStartTime(time);
                        info.setText(lyricStr);
                        infos.add(info);
                    }
                }
            }
            lyric.setLyricInfoList(infos);
            // 序列化到本地
            //System.out.println("设置新数据:" + JSON.toJSONString(lyric));
            MusicCacheManager.get().saveLyric(lyric);

            if (onLyricCallBack != null){
                onLyricCallBack.onCallback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnLyricCallBackListener(OnLyricCallBack onLyricCallBack){
        this.onLyricCallBack = onLyricCallBack;
    }

    public interface OnLyricCallBack{
        void onCallback();
    }


    private int parseTime(String s) {
        //01:03:
        //00：00.00
        int hour = 0;
        int min = 0;
        float second = 0;
        String timeStr = s.substring(1);
        String[] times = timeStr.split(":");
        if (times.length == 3) {
            // 包括小时
            hour = Integer.parseInt(times[0]) * 60 * 60 * 1000;
            min = Integer.parseInt(times[1]) * 60 * 1000;
            second = Float.parseFloat(times[2]) * 1000;

        } else if (times.length == 2) {
            // 仅仅 分秒
            min = Integer.parseInt(times[0]) * 60 * 1000;
            second = Float.parseFloat(times[1]) * 1000;
        } else {
            // 秒
            second = Float.parseFloat(times[0]) * 1000;
        }
        return (int) (hour + min + second);
    }


}
