package com.example.yang.diymusic.audio;

import android.content.Context;

import com.example.yang.diymusic.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FileUtil {
    static final int CACHE_SIZE = 1024 * 4;

    /**
     * @param context  环境
     * @param resId  资源文件
     * @param filePath 文件路径
     * @return 是否复制成功
     */
    public static void writeToFileFromRaw(Context context, int resId, String filePath) {
        if (checkFileExist(new File(filePath))) {
            return;
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
    }

    /**
     * @param audio 音频具体信息
     * @return
     */
    public static boolean writeEmptyAudio(Audio audio) {
        int sampleRate = audio.getSampleRate();
        int channels = audio.getChannel();
        int bitNum = audio.getBitNum();
        String path = audio.getPcmPath();
        if (checkFileExist(new File(path))) {
            return false;
        }
        long duration = audio.getDuration();
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        byte[] buffer = new byte[CACHE_SIZE]; //缓冲
        //字节长度 = time(s)*sampleRate* channelCount *bitNum/8(位)
        long totalDataLen = duration / 1000 * sampleRate *
                channels * bitNum / 8;
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
        return true;
    }

    public static boolean checkFileExist(File file) {
        if (file.exists()) {
            LogUtil.i("文件已存在");
            return true;
        }
        return false;
    }

    // 判断文件夹是否存在，不存在则创建
    public static boolean judeAndCreateDirExists(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                return true;
            } else {
                return false;
            }
        } else {
            file.mkdir();
            return false;
        }
    }
    public static ArrayList<String> showFileList(File file){
        if(file == null){
            return null;
        }
        if(file.isDirectory()){
            File[] filesArray = file.listFiles();
            if(filesArray != null){
                ArrayList mLists = new ArrayList();
                for (File aFilesArray : filesArray) {
                    mLists.add(aFilesArray.getName());
                }
                return mLists;
            }
        }
        return null;
    }
    public static void copyFile(String srcPath, String armPath) {
        if (checkFileExist(new File(armPath))) {
            return;
        }
        FileInputStream fis;
        FileOutputStream fos;
        byte[] buffer = new byte[CACHE_SIZE];
        int len;
        try {
            fis = new FileInputStream(srcPath);
            fos = new FileOutputStream(armPath);
            BufferedInputStream bis = new BufferedInputStream(fis);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            fis.close();
            fos.close();
        } catch (IOException e) {
            LogUtil.e("文件读取失败");
            e.printStackTrace();
        }
    }
}
