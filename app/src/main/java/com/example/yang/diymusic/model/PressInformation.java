package com.example.yang.diymusic.model;

public class PressInformation {
    public long getPressTime() {
        return pressTime;
    }

    public void setPressTime(long pressTime) {
        this.pressTime = pressTime;
    }

    public int getButtonId() {
        return buttonId;
    }

    public void setButtonId(int buttonId) {
        this.buttonId = buttonId;
    }

    private long pressTime;
    private int buttonId;

}
