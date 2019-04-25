package com.example.yang.diymusic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
//    private Button mButton01, mButton02, mButton03;
//    private SoundPool sp;//声明一个SoundPool
//    private int music1, music2, music3;//定义一个整型用load（）；来设置suondID
//    private static boolean isRun = false;
    public static final int START = 8;
    public static final int END = 92;
    public static final float RATIO = (float) 1.0/(100 - START - END)*100;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        SeekBar seekBar = findViewById(R.id.timeline);
        seekBar.setProgress(8);
//        x/84 *100          8   == 0        92 == 100
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("seekBar : ", (progress - 8)*RATIO +"");
                Log.i("seekBar : ", RATIO+"onProgressChanged: ");
                System.out.print(RATIO);
                if(progress <= START)
                    seekBar.setProgress(START);
                if(progress >=END)
                    seekBar.setProgress(END);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//        seekBar.setAlpha(0.5f);

//        init();
//        addEvent();
    }

//    private void addEvent() {
//        addListener(mButton01, music1);
//        addListener(mButton02, music2);
//        addListener(mButton03, music3);
//    }
//    public void addListener(Button btn, final int music) {
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sp.play(music, 1, 1, 0, 0, 1);
//
//            }
//        });
//    }
//    public void init() {
//        mButton01 = (Button) findViewById(R.id.mButton01);
//        mButton02 = (Button) findViewById(R.id.mButton02);
//        mButton03 = (Button) findViewById(R.id.mButton03);
//        sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//        music1 = sp.load(this, R.raw.piano78, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
//        music2 = sp.load(this, R.raw.piano83, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
//        music3 = sp.load(this, R.raw.piano87, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
//
//    }

}