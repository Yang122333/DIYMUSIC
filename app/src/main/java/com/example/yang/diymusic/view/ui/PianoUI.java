package com.example.yang.diymusic.view.ui;

public interface PianoUI extends BaseUI {
    void showRecordHint(String text);
    void showPlayHint(String text);
    void hideHint();
    void showDialog();
    void dismissLoading();
    void showLoading();
}
