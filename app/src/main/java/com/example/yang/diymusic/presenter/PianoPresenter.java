package com.example.yang.diymusic.presenter;

public class PianoPresenter<V> extends BasePresenter<V> {
    public static final int START = 150;
    private static final int END = 850;
    public static final int FULL = 1000;
    public static final double TOPERCENT = 1.0 / FULL;
    private static final double RATIO =  1.0/(FULL - START - (FULL - END))*FULL;
    private double seekBarPercent = 0;
    public int handleProgress(int progress){
        if(progress < START){
            return START;
        } else if(progress >END){
            return END;
        } else {
            seekBarPercent = (progress - START) * RATIO * TOPERCENT;
            return progress;
        }
    }

    public double getSeekBarPercent() {
        return seekBarPercent ;
    }
}
