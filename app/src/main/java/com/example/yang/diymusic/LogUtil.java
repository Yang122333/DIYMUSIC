package com.example.yang.diymusic;

import android.util.Log;

public class LogUtil {
    private static final String TAG = "Log information :";
    public static void i(String data){
        Log.i(TAG, data);

    }
    public static void e(String data){
        Log.e(TAG, data);
    }
}
