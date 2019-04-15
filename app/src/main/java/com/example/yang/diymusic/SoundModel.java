package com.example.yang.diymusic;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

class SoundModel {
    private SoundPool sp;
    private static int[] musicIds = new int[10];
    private static int[] musicSourses = new int[]{
            R.raw.agrand_a4m,
            R.raw.agrand_c4,
            R.raw.agrand_d4,
            R.raw.agrand_e4,
            R.raw.agrand_f4,
            R.raw.agrand_g4m
    };
    int[] MusicSourses1 = new int[]{
            R.raw.piano031,
            R.raw.piano032,
            R.raw.piano033,
            R.raw.piano034,
            R.raw.piano035,
            R.raw.piano036,
            R.raw.piano037,
            R.raw.piano038,
            R.raw.piano039,
            R.raw.piano040,
    };

    public SoundModel(Context context) {
        sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量

        for (int i = 0; i < musicIds.length; i++) {
            musicIds[i] = sp.load(context, MusicSourses1[i], 1);
        }
    }

    void play(int type, int index) {
        sp.play(getId(index), 1, 1, 0, 0, 1);
    }

    private int getId(int index) {
        return musicIds[index];
    }
}
