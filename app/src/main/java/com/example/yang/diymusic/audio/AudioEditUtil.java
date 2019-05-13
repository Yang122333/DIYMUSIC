package com.example.yang.diymusic.audio;

import java.io.IOException;
import java.io.RandomAccessFile;

public class AudioEditUtil {
    public static int WAVE_HEAD_SIZE = 44;
    public static int MAX_ELEMENT_AUDIO_TIME = 9 * 1000;

    /**
     * 裁剪音频
     *
     * @param audio        音频信息
     * @param cutStartTime 裁剪开始时间
     */
    public static void compoundAudio(Audio audio, String elementAudioPath, float cutStartTime) {

        String srcWavePath = audio.getWavPath();
        int sampleRate = audio.getSampleRate();
        int channels = audio.getChannel();
        int bitNum = audio.getBitNum();
        long duration = audio.getDuration();
        //如果
        if (cutStartTime + MAX_ELEMENT_AUDIO_TIME > duration)
            return;
        RandomAccessFile srcFis = null;
        RandomAccessFile newFos = null;
        try {
            //创建输入流
            srcFis = new RandomAccessFile(srcWavePath, "rw");
            newFos = new RandomAccessFile(elementAudioPath, "rw");
            //源文件开始读取位置，结束读取文件，读取数据的大小
            final int cutStartPos = getPositionFromWave(cutStartTime, sampleRate, channels, bitNum);
            //移动到文件开始读取处
            int currentPos = WAVE_HEAD_SIZE + cutStartPos;
            srcFis.seek(currentPos);
            newFos.seek(WAVE_HEAD_SIZE);
            int len;
            byte[] srcWavBuffer = new byte[FileUtil.CACHE_SIZE];
            byte[] elementAudioBuffer = new byte[FileUtil.CACHE_SIZE];
            byte[][] bytes = new byte[2][];
            byte[] cacheBuffer;
            while ((len = newFos.read(srcWavBuffer)) != -1) {
                srcFis.read(elementAudioBuffer, 0, len);
                bytes[0] = srcWavBuffer;
                bytes[1] = elementAudioBuffer;
                srcFis.seek(currentPos);
                cacheBuffer = mixRawAudioBytes(bytes, len);
                srcFis.write(cacheBuffer, 0, len);
                currentPos += len;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;

        } finally {
            //关闭输入流
            if (srcFis != null) {
                try {
                    srcFis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (newFos != null) {
                try {
                    newFos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取wave文件某个时间对应的数据位置
     *
     * @param time       时间(s)
     * @param sampleRate 采样率
     * @param channels   声道数
     * @param bitNum     采样位数
     * @return
     */
    private static int getPositionFromWave(float time, int sampleRate, int channels, int bitNum) {
        int byteNum = bitNum / 8;
        int position = (int) (time / 1000 * sampleRate * channels * byteNum);

        //这里要特别注意，要取整（byteNum * channels）的倍数
        position = position / (byteNum * channels) * (byteNum * channels);

        return position;
    }

    public static byte[] mixRawAudioBytes(byte[][] bMulRoadAudios, int len) {

        if (bMulRoadAudios == null || bMulRoadAudios.length == 0)
            return null;

        byte[] realMixAudio = bMulRoadAudios[0];
        if (realMixAudio == null) {
            return null;
        }

        final int row = bMulRoadAudios.length;

        //单路音轨
        if (bMulRoadAudios.length == 1)
            return realMixAudio;

        //不同轨道长度要一致，不够要补齐
//        for (int rw = 0; rw < bMulRoadAudios.length; ++rw) {
//            if (bMulRoadAudios[rw] == null || bMulRoadAudios[rw].length != realMixAudio.length) {
//                return null;
//            }
//        }
        /**
         * 精度为 16位
         */
        int col = len / 2;
        short[][] sMulRoadAudios = new short[row][col];

        for (int r = 0; r < row; ++r) {
            for (int c = 0; c < col; ++c) {
                sMulRoadAudios[r][c] = (short) ((bMulRoadAudios[r][c * 2] & 0xff) | (bMulRoadAudios[r][c * 2 + 1] & 0xff) << 8);
            }
        }

        short[] sMixAudio = new short[col];
        int mixVal;
        int sr = 0;
        for (int sc = 0; sc < col; ++sc) {
            mixVal = 0;
            sr = 0;
            for (; sr < row; ++sr) {
                mixVal += sMulRoadAudios[sr][sc];
            }
            sMixAudio[sc] = (short) (mixVal / row);
        }

        for (sr = 0; sr < col; ++sr) {
            realMixAudio[sr * 2] = (byte) (sMixAudio[sr] & 0x00FF);
            realMixAudio[sr * 2 + 1] = (byte) ((sMixAudio[sr] & 0xFF00) >> 8);
        }
        return realMixAudio;
    }
}