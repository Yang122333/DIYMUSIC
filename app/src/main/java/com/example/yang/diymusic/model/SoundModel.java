package com.example.yang.diymusic.model;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.example.yang.diymusic.audio.AudioService;

public class SoundModel {
    private SoundPool mSoundPool;
    private static int[] musicIds = new int[88];
    /**
     * 白键标识
     */
    private final static int KEY_WHITE = 0;
    /**
     * 黑键标识
     */
    private final static int KEY_BLACK = 1;

    private SoundModel() {
    }

    private SoundModel(Context context) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                SoundPool sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
                AudioService audioService = AudioService.getInstance(context);
                for (int i = 0; i < musicIds.length; i++) {
                    musicIds[i] = sp.load(context, audioService.getPianoIds()[i], 1);
                }
                mSoundPool = sp;
            }
        }.start();
        //第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量

    }

    private static SoundModel singleton;

    public static SoundModel getInstance(Context context) {
        if (singleton == null) {
            synchronized (AudioService.class) {
                if (singleton == null) {
                    singleton = new SoundModel(context);
                }
            }
        }
        return singleton;
    }

    public static int findId(int type, int index) {
        int postion = 0;
//        1 0 1 1 0     1 0 1 1 0
//        0 1 2 3 4     5 6 7 8 9
        switch (type) {
            case KEY_WHITE: {
                if (index % 3 == 0) {
                    postion = index / 3 * 5 + index % 3;
                } else {
                    postion = index / 3 * 5 + index % 3 + 1;
                }
                break;
            }
            case KEY_BLACK: {
                if (index % 2 == 0) {
                    postion = index / 2 * 5 + index % 2 + 1;
                } else {
                    postion = index / 2 * 5 + index % 2 + 3;
                    //第35个黑色有毒
                    if (index == 35) {
                        postion -= 2;
                    }
                }
                break;
            }
        }
        return postion;
    }

    public void play(int type, int index) {
        if (mSoundPool != null){
            mSoundPool.play(getId(findId(type, index)), 1, 1, 0, 0, 1);
        }
    }

    private int getId(int index) {
        return musicIds[index];
    }
}
