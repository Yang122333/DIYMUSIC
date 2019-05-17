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
    public int start = 122;
    private int end = 878;
    public static final int FULL = 1000;
    public static final double TOPERCENT = 1.0 / FULL;
    public int one_rect = (end - start) / PianoView.WHITE_PIANO_KEY_COUNT;
    public int screen_rect = one_rect * 8;
    private double ratio = 1.0 / (FULL - start - (FULL - end)) * FULL;
    private double seekBarPercent = 0;
    private Context context;
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private long startTime;
    private MediaPlayer player;

    public PianoPresenter(Context context) {
        this.context = context;
    }

    public void setBarStart(int number, int offset) {
        start = offset;
        end = FULL - offset;
        screen_rect = one_rect * number;
    }

    public int handleProgress(int progress) {
        if (progress < start) {
            return start;
        } else if (progress > end) {
            return end;
        } else {
            seekBarPercent = (progress - start) * ratio * TOPERCENT;
            return progress;
        }
    }

    //开始录音或者播放提示文案
    public void startRecordOrPlay(boolean isPlay) {
        if (!isPlay) {
            if(timer != null){
                timer.cancel();
            }
            startRecord();
        } else {
            if(timer != null){
                timer.cancel();
            }
            startPlay();
        }
    }

    //操作UI显示文案开始录音
    private void startRecord() {
        startTime = System.currentTimeMillis();
        timer = new Timer();
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

    //操作UI显示文案开始播放
    private void startPlay() {
        timer = new Timer();
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

    //对文案操作
    public void stopRecordOrPlay(boolean isNeedDialog) {
        getUi().hideHint();
        if(timer != null){
            timer.cancel();
        }
        if (isNeedDialog) {
            getUi().showDialog();
        }
        if (player != null) {
            player.stop();
        }
    }

    public boolean record(String srcFileName, String armFileName) {
        AudioService audioService = AudioService.getInstance(context);
        if (FileUtil.checkFileExist(new File(audioService.MUSIC + File.separator + armFileName + ".wav"))) {
            Toast.makeText(context, "文件已存在", Toast.LENGTH_SHORT).show();
            return false;
        }
        getUi().showLoading();
        if (srcFileName == null) {
            Observable.create(subscriber -> {
                audioService.produceAudio(armFileName, RecodeModel.getInstance().getList(), startTime);
                subscriber.onNext(null);
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(o -> {
                        getUi().dismissLoading();
                        RecodeModel.getInstance().getList().clear();
                    });
        } else {
            Observable.create(subscriber -> {
                audioService.handleAudio(srcFileName, armFileName, RecodeModel.getInstance().getList(), startTime);
                subscriber.onNext(null);
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(o -> {
                        getUi().dismissLoading();
                        RecodeModel.getInstance().getList().clear();
                    });
        }
        return true;
    }


    public double getSeekBarPercent() {
        return seekBarPercent;
    }

    public void playAudioAndRecord(String fileName) {
        if (player != null) {
            player.stop();
        }
        startRecordOrPlay(false);
        getUi().hideHint();
        try {
            player = new MediaPlayer();
            player.setDataSource(AudioService.getInstance(context).MUSIC + File.separator + fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setOnCompletionListener(mp -> stopRecordOrPlay(false));
    }

    public void playAudio(String fileName) {
        if (player != null) {
            player.stop();
        }
        startRecordOrPlay(true);
        try {
            player = new MediaPlayer();
            player.setDataSource(AudioService.getInstance(context).MUSIC + File.separator + fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setOnCompletionListener(mp -> stopRecordOrPlay(false));
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
