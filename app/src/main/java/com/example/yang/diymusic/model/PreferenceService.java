package com.example.yang.diymusic.model;

public class PreferenceService {
    public static final String DO = "Do";
    public static final String D4 = "D4";
    public static final String BLANK = "bLank";
    private String mTone ;
    private int mMode;
    private int mKeyNumber;
    public PreferenceService() {
        mKeyNumber = 8;
        mTone = D4;
        mMode = 1;
    }
    public void setKeyNumber(int keyNumber){
        mKeyNumber = keyNumber;
    }
    public int getKeyNumber() {
    return mKeyNumber;
    }
    public void setMode(int mode){
        mMode = mode;
    }
    public int getMode() {
        return mMode;
    }
    public void setTone(String tone){
      mTone = tone;
    }
    public String getTone() {
        return mTone;
    }
}
