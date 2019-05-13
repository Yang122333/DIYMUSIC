package com.example.yang.diymusic.presenter;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.Toast;

import com.example.yang.diymusic.audio.AudioService;
import com.example.yang.diymusic.audio.FileUtil;
import com.example.yang.diymusic.model.RecodeModel;
import com.example.yang.diymusic.view.PianoView;
import com.example.yang.diymusic.view.ui.PianoUI;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PianoPresenter extends BasePresenter<PianoUI> {
    public static final int RECORD = 1;
    public static final int PLAY = 2;
    public static final int START = 150;
    private static final int END = 850;
    public static final int FULL = 1000;
    public static final double TOPERCENT = 1.0 / FULL;
    public static final int ONE_RECT = (END - START) / PianoView.WHITE_PIANO_KEY_COUNT;
    public static final int SCREEN_RECT = ONE_RECT * 8;
    private static final double RATIO = 1.0 / (FULL - START - (FULL - END)) * FULL;
    private double seekBarPercent = 0;
    private Context context;
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private long startTime;
    private MediaPlayer player = new MediaPlayer();

    public PianoPresenter(Context context) {
        this.context = context;
    }

    public int handleProgress(int progress) {
        if (progress < START) {
            return START;
        } else if (progress > END) {
            return END;
        } else {
            seekBarPercent = (progress - START) * RATIO * TOPERCENT;
            return progress;
        }
    }

    public void startRecordOrPlay(int type) {
        if (type == RECORD) {
            startRecord();
        } else {
            stopRecordOrPlay(true);
            startPlay();
        }
    }

    private void startRecord() {
        startTime = System.currentTimeMillis();
        timer.schedule(new TimerTask() {
            int time = 0;
            int temp = 0;
            @Override
            public void run() {
                if (temp % 2 == 0) {
                    time++;
                }
                temp++;
                handler.post(() -> getUi().showRecordHint("正在录制：" + time + " s"));
            }
        }, 0, 500);
    }

    private void startPlay() {
        timer.schedule(new TimerTask() {
            int time = 0;
            int temp = 0;
            @Override
            public void run() {
                if (temp % 2 == 0) {
                    time++;
                }
                temp++;
                handler.post(() -> getUi().showPlayHint("正在播放：" + time + " s"));
            }
        }, 0, 500);
    }

    public void stopRecordOrPlay(boolean isPlaying) {
        timer.cancel();
        timer = new Timer();
        getUi().hideHint();
        if (!isPlaying) {
            getUi().showDialog();
        } else {
            if (player != null) {
                player.reset();
            }
        }
    }

    public boolean record(String name) {
        AudioService audioService = AudioService.getInstance(context);
        if (FileUtil.checkFileExist(new File(audioService.MUSIC + File.separator + name + ".wav"))) {
            Toast.makeText(context, "文件已存在", Toast.LENGTH_SHORT).show();
            return false;
        }
        getUi().showLoading();
        Observable.create(subscriber -> {
            audioService.produceAudio(name, RecodeModel.getInstance().getList(), startTime);
            subscriber.onNext(null);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    getUi().dismissLoading();
                    RecodeModel.getInstance().getList().clear();
                });
        return true;

    }

    public double getSeekBarPercent() {
        return seekBarPercent;
    }

    public void playAudio(String fileName) {
        if(player != null){
            player.stop();
        }

        startRecordOrPlay(PLAY);
        try {
            player.setDataSource(AudioService.getInstance(context).MUSIC + File.separator + fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setOnCompletionListener(mp -> stopRecordOrPlay(true));
    }

    @Override
    public void disAttachView() {
        super.disAttachView();
        if (player != null) {
            player.stop();
            player.release();
        }
    }
}
