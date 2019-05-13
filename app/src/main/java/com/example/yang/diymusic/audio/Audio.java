package com.example.yang.diymusic.audio;

import android.media.AudioFormat;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.File;
import java.io.FileInputStream;

/**
 * 音频信息
 */
public class Audio {
    public String getPcmPath() {
        return pcmPath;
    }

    public void setPcmPath(String path) {
        this.pcmPath = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getBitNum() {
        return bitNum;
    }

    public void setBitNum(int bitNum) {
        this.bitNum = bitNum;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    private String pcmPath;

    public String getWavPath() {
        return wavPath;
    }

    public void setWavPath(String armPath) {
        this.wavPath = armPath;
    }

    private String wavPath;
    private String name;
    private float volume = 1f;
    private int channel = 2;
    private int sampleRate = 44100;
    private int bitNum = 16;
    private long duration;

    public static Audio createAudioFromFile(File inputFile) throws Exception {
        if (!inputFile.exists())
            return null;
        MediaExtractor extractor = new MediaExtractor();
        MediaFormat format = null;
        int i;

        try {
            extractor.setDataSource(inputFile.getPath());
        } catch (Exception ex) {
            ex.printStackTrace();
            extractor.setDataSource(new FileInputStream(inputFile).getFD());
        }

        int numTracks = extractor.getTrackCount();
        for (i = 0; i < numTracks; i++) {
            format = extractor.getTrackFormat(i);
            if (format.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                extractor.selectTrack(i);
                break;
            }
        }
        if (i == numTracks) {
            throw new Exception("No audio track found in " + inputFile);
        }
        Audio audio = new Audio();
        audio.name = inputFile.getName();
        audio.wavPath = inputFile.getAbsolutePath();
        audio.sampleRate = format.containsKey(MediaFormat.KEY_SAMPLE_RATE) ?
                format.getInteger(MediaFormat.KEY_SAMPLE_RATE) : 44100;
        audio.channel = format.containsKey(MediaFormat.KEY_CHANNEL_COUNT) ?
                format.getInteger(MediaFormat.KEY_CHANNEL_COUNT) : 1;
        audio.duration = (long) ((format.getLong(MediaFormat.KEY_DURATION) / 1000.f));

        //根据pcmEncoding编码格式，得到采样精度，MediaFormat.KEY_PCM_ENCODING这个值不一定有
        int pcmEncoding = format.containsKey(MediaFormat.KEY_PCM_ENCODING) ?
                format.getInteger(MediaFormat.KEY_PCM_ENCODING) : AudioFormat.ENCODING_PCM_16BIT;
        switch (pcmEncoding) {
            case AudioFormat.ENCODING_PCM_FLOAT:
                audio.bitNum = 32;
                break;
            case AudioFormat.ENCODING_PCM_8BIT:
                audio.bitNum = 8;
                break;
            case AudioFormat.ENCODING_PCM_16BIT:
            default:
                audio.bitNum = 16;
                break;
        }

        extractor.release();

        return audio;
    }

    @Override
    public String toString() {
        return "path:" + wavPath +
                "\n name:" + name +
                "\n channel:" + channel +
                "\n sampleRate:" + sampleRate +
                "\n bitNum" + bitNum +
                "\n duration" + duration;
    }
}
