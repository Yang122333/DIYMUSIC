package com.example.yang.diymusic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class TestActivity extends Activity {


    final String tag = "        event :      ";


    LinearLayout keys;
    private View button[];//按钮数组
    private View btn;
    //    private MyMusicUtils utils;//工具类
    private int buttonId[];//按钮id
    private boolean havePlayed[];//是否已经播放了声音，当手指在同一个按钮内滑动，且已经发声，就为true
    private int currentKey;//手指当前所在按钮
    private int lastKey = -1;//上一个按钮

    private int mMargin;
    private int left, right, top, bottom, totalWidth, width;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            init();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testlayout);
        keys = findViewById(R.id.keys);
        buttonId = new int[7];
        buttonId[0] = R.id.button1;
        buttonId[1] = R.id.button2;
        buttonId[2] = R.id.button3;
        buttonId[3] = R.id.button4;
        buttonId[4] = R.id.button5;
        buttonId[5] = R.id.button6;
        buttonId[6] = R.id.button7;
        button = new Button[7];
        havePlayed = new boolean[7];

        //获取按钮对象
        for (int i = 0; i < button.length; i++) {
            button[i] = findViewById(buttonId[i]);
            button[i].setClickable(false);
            havePlayed[i] = false;
        }
        btn = button[0];
    }

    void init() {
        mMargin = ((LinearLayout.LayoutParams) btn.getLayoutParams()).rightMargin;
        left = keys.getPaddingLeft() + btn.getLeft();
        right = button[button.length - 1].getRight();
        top = btn.getTop();
        bottom = btn.getBottom();
        totalWidth = btn.getWidth() + 2 * mMargin;
        width = btn.getWidth();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onResume() {
        super.onResume();

        keys.setOnTouchListener(new View.OnTouchListener() {
            float curX;
            int index;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        currentKey = -1;
                        curX = event.getX();

                        index = inWhich(curX, event.getY());
                        Log.i(tag, "down"+index);
                        if (index != -1) {
                            currentKey = index;
                        }
                        if (currentKey != -1) {
                            if (!havePlayed[currentKey]) {
                                // play
                                Log.i(tag,  "down :"+currentKey );
                                havePlayed[currentKey] = true;
                                button[currentKey].setBackgroundColor(Color.WHITE);
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        curX = event.getX();
                        lastKey = currentKey;
                        index = inWhich(curX,event.getY());
                        if(index == -1){
                            break;
                        }
                        currentKey = index;

                        if(lastKey != currentKey&&lastKey != -1){
                            havePlayed[lastKey] = false;
                            button[lastKey].setBackgroundColor(Color.BLACK);
                        }
                        if(!havePlayed[currentKey]){
                            havePlayed[currentKey] = true;
                            Log.i(tag, "move : "+currentKey);
                            button[currentKey].setBackgroundColor(Color.WHITE);
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (currentKey == -1) {
                            break;
                        }
                        button[currentKey].setBackgroundColor(Color.BLACK);
                        havePlayed[currentKey] = false;
                        Log.i(tag, "up: ");
                        break;
                    }
                    default: {
                        if (currentKey == -1) {
                            break;
                        }
                        button[currentKey].setBackgroundColor(Color.BLACK);
                        havePlayed[currentKey] = false;
                        Log.i(tag, "default: ");
                        break;
                    }
                }
                return true;
            }
        });
    }

    private int inWhich(float x, float y) {
        if (x > left && x < right && y > top && y < bottom) {
            float weizhi = (x - left) % totalWidth;
            if (weizhi > mMargin && weizhi < mMargin + width) {
                mMargin = (int) (x - left) / totalWidth;
                return mMargin;
            }
        }
        return -1;
    }
}