package com.example.yang.diymusic.audio;

import android.content.Context;

import com.example.yang.diymusic.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    public static final int CACHE_SIZE = 1024 * 4;

    /**
     * @param context
     * @param resId
     * @param filePath
     * @return 是否复制成功
     */
    public static boolean writeToFileFromRaw(Context context, int resId, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            LogUtil.i("文件已存在");
            return false;
        }
        InputStream input = context.getResources().openRawResource(resId);
        BufferedInputStream bis = new BufferedInputStream(input);
        FileOutputStream fos;
        byte[] buffer = new byte[CACHE_SIZE];
        int len;
        try {
            fos = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            input.close();
            fos.close();
        } catch (IOException e) {
            LogUtil.e("文件读取失败");
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @param path
     * @return
     */
    public static boolean writeEmptyAudio(String path, Audio audio) {
        long time1 = System.currentTimeMillis();
        File file = new File(path);
        if (file.exists()) {
            LogUtil.i("文件已存在");
            return false;
        }
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        byte[] buffer = new byte[CACHE_SIZE]; //缓冲
        //字节长度 = time(s)*sampleRate* channelCount *bitNum/8(位)
        long totalDataLen = audio.getDuration() / 1000 * audio.getSampleRate() *
                audio.getChannel() * audio.getBitNum() / 8;
        try {
            fos = new FileOutputStream(path);
            bos = new BufferedOutputStream(fos);
            long index = 0;
            /**
             * total 7 index 0
             * 01   2   4
             * 23   4   6
             * 45   6   8
             * 67   8   10
             */
            while (index <= totalDataLen) {
                bos.write(buffer, 0, CACHE_SIZE);
                index += CACHE_SIZE;
                if (index + CACHE_SIZE > totalDataLen) {
                    bos.write(buffer, 0, (int) (index + CACHE_SIZE - totalDataLen));
                    break;
                }
            }
//            for (int i = 0; i < totalDataLen; i+= CACHE_SIZE) {
//                bos.write(buffer,0,CACHE_SIZE);
//            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bos != null) {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long time2 = System.currentTimeMillis();
        LogUtil.i(time2-time1+"");
        return true;
    }

    public static boolean checkFileExist(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }

}
