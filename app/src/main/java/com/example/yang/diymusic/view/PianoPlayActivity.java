package com.example.yang.diymusic.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yang.diymusic.DrawableUtils;
import com.example.yang.diymusic.model.PreferenceService;
import com.example.yang.diymusic.R;
import com.example.yang.diymusic.presenter.PianoPresenter;
import com.example.yang.diymusic.view.ui.PianoUI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PianoPlayActivity extends BaseActivity<PianoPresenter, PianoUI> implements PianoUI,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener, DrawerLayout.DrawerListener {
    //当前activity的request标志
    public static final int PIANOPLAYACTIVITY_REQUEST = 1;
    //抽屉主界面
    private DrawerLayout drawerLayout;
    //钢琴主界面
    private LinearLayout linearLayout;
    //进度条
    private SeekBar seekBar;
    //自定义钢琴界面
    private PianoView mPianoView;
    //开始录音按钮
    private ImageView recordBtn,
    //停止录音或播放
    stopRecordBtn,
    //进度条左移一个屏幕
    leftArrowsBtn,
    //进度条左移一格
    leftArrowBtn,

    rightArrowBtn,

    rightArrowsBtn,
    //录音时闪烁的红点
    recordingImage,
    //播放时闪烁的绿点
    playingImage,
    //菜单按钮
    menuBtn,
    //按键标签按钮
    labelBtn;
    //录音提示文字
    private TextView hint,
    //选择音频文件按钮
    musicFileBtn;
    //进度条百分比
    private int mProgress = 122;
    //是否正在播放音频
    private boolean isPlaying = false;

    private boolean isRecording = false;
    //钢琴基础配置
    private PreferenceService pf;
    //按键标签id
    private int defaultId;

    private String srcFileName = null;


    /**
     * 静态启动方法
     *
     * @param context 启动此activity的环境
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, PianoPlayActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    /**
     * 初始化钢琴界面
     */
    private void init() {
        findView();
        seekBar.setMax(PianoPresenter.FULL);
        seekBar.setProgress(mPresenter.start);
        mPianoView.setProgress(mPresenter.getSeekBarPercent());
        Log.i("width", px2dp(this, seekBar.getThumb().getMinimumWidth()) + "");
//        配置piano
        pf = new PreferenceService();
        mPianoView.setPf(pf);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    //  页面中按键的个数
    private void setThumb(int number) {
        // dp
        int totalWidth = seekBar.getWidth();
        //52 个键
        int thumberWidth = (int) (1.0 * totalWidth / PianoView.WHITE_PIANO_KEY_COUNT * number);
        Drawable drawable = seekBar.getThumb();
        Drawable drawable1 = DrawableUtils.zoomDrawable(drawable, thumberWidth, drawable.getMinimumHeight());
        seekBar.setThumb(drawable1);
        int offset = (int) (thumberWidth / 2.0 / totalWidth * PianoPresenter.FULL);
        mPresenter.setBarStart(number, offset);
        mPianoView.setNumber(number);
    }

    /**
     * 实例化按键
     */
    private void findView() {
        drawerLayout = findViewById(R.id.drawer_layout);
        linearLayout = findViewById(R.id.piano_keyboard);
        seekBar = findViewById(R.id.overview);
        mPianoView = findViewById(R.id.piano_view);
        recordBtn = findViewById(R.id.record);
        stopRecordBtn = findViewById(R.id.record_stop);
        leftArrowBtn = findViewById(R.id.left_arrow);
        leftArrowsBtn = findViewById(R.id.left_arrows);
        rightArrowBtn = findViewById(R.id.right_arrow);
        rightArrowsBtn = findViewById(R.id.right_arrows);
        recordingImage = findViewById(R.id.recording);
        hint = findViewById(R.id.record_text);
        menuBtn = findViewById(R.id.action_bar_menu);
        musicFileBtn = findViewById(R.id.music_file);
        playingImage = findViewById(R.id.playing);
        labelBtn = findViewById(R.id.action_bar_choose_label);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListenter();
    }

    /**
     * 设置监听
     */
    private void setListenter() {
        seekBar.setOnSeekBarChangeListener(this);
        leftArrowBtn.setOnClickListener(this);
        leftArrowsBtn.setOnClickListener(this);
        rightArrowsBtn.setOnClickListener(this);
        rightArrowBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
        stopRecordBtn.setOnClickListener(this);
        menuBtn.setOnClickListener(this);
        musicFileBtn.setOnClickListener(this);
        drawerLayout.addDrawerListener(this);
        labelBtn.setOnClickListener(this);
    }

    @Override
    public int getLayout() {
        return R.layout.piano_playing_layout;
    }

    @Override
    public PianoPresenter createPresenter() {
        return new PianoPresenter(this);
    }

    @Override
    public void show() {
    }

    @Override
    public void cancel() {
    }

    /**
     * 录音动作
     *
     * @param text 正在录音提示文案
     */
    @Override
    public void showRecordHint(String text) {
        //显示暂停按钮
        if (recordBtn.getVisibility() == View.VISIBLE) {
            recordBtn.setVisibility(View.GONE);
            stopRecordBtn.setVisibility(View.VISIBLE);
        }
        if (playingImage.getVisibility() == View.VISIBLE) {
            playingImage.setVisibility(View.GONE);
        }
        if (recordingImage.getVisibility() == View.VISIBLE) {
            recordingImage.setVisibility(View.INVISIBLE);
        } else {
            recordingImage.setVisibility(View.VISIBLE);
        }
        if (hint.getVisibility() == View.INVISIBLE) {
            hint.setVisibility(View.VISIBLE);
        }
        hint.setText(text);
    }

    /**
     * 播放动作
     *
     * @param text 正在播放提示文案
     */
    @Override
    public void showPlayHint(String text) {
        mPianoView.setRecord(false);
        if (recordBtn.getVisibility() == View.VISIBLE) {
            recordBtn.setVisibility(View.GONE);
            stopRecordBtn.setVisibility(View.VISIBLE);
        }
        if (playingImage.getVisibility() == View.VISIBLE) {
            playingImage.setVisibility(View.INVISIBLE);
        } else {
            playingImage.setVisibility(View.VISIBLE);
        }
        if (hint.getVisibility() == View.INVISIBLE) {
            hint.setVisibility(View.VISIBLE);
        }
        hint.setText(text);
    }

    /**
     * 隐藏提示文案和闪烁图片
     */
    @Override
    public void hideHint() {
        recordBtn.setVisibility(View.VISIBLE);
        stopRecordBtn.setVisibility(View.GONE);
        recordingImage.setVisibility(View.GONE);
        playingImage.setVisibility(View.GONE);
        hint.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示创建文件弹窗
     */
    @Override
    public void showDialog() {
        final EditText editText = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入文件名").setView(editText);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", null).create();
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Pattern p = Pattern.compile("^[a-z][a-zA-Z0-9]*");
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String armFileName = editText.getText().toString();
            Matcher matcher = p.matcher(armFileName);
            if (matcher.matches()) {
                    isRecording = false;
                    if (mPresenter.record(srcFileName, armFileName)) {
                        alertDialog.dismiss();
                    }

            } else {
                Toast.makeText(PianoPlayActivity.this, "输入文件名不合法！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ZProgressHUD progressHUD;

    /**
     * 显示正在加载loading
     */
    @Override
    public void showLoading() {
        progressHUD = ZProgressHUD.getInstance(this);
        progressHUD.show();
    }

    /**
     * 取消正在加载loading
     */
    @Override
    public void dismissLoading() {
        progressHUD.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record: {
                isPlaying = false;
                mPianoView.setRecord(true);
                mPresenter.startRecordOrPlay(false);
                break;
            }
            case R.id.record_stop: {
                mPianoView.setRecord(false);
                //todo
                if(isPlaying){
                    mPresenter.stopRecordOrPlay(isRecording);
                } else {
                    mPresenter.stopRecordOrPlay(true);
                }
                isPlaying = false;
                isRecording = false;
                break;
            }
            case R.id.left_arrows: {
                seekBar.setProgress(mProgress - mPresenter.screen_rect);
                break;
            }
            case R.id.left_arrow: {
                seekBar.setProgress(mProgress - mPresenter.one_rect);
                break;
            }
            case R.id.right_arrow: {
                seekBar.setProgress(mProgress + mPresenter.one_rect);
                break;
            }
            case R.id.right_arrows: {
                seekBar.setProgress(mProgress + mPresenter.screen_rect);
                break;
            }
            case R.id.action_bar_menu: {
                drawerLayout.openDrawer(Gravity.RIGHT);
                break;
            }
            case R.id.music_file: {
                Intent intent = new Intent(this, FilesShowActivity.class);
                startActivityForResult(intent, PIANOPLAYACTIVITY_REQUEST);
                break;
            }
            case R.id.action_bar_choose_label: {
                chooseLabel();
                break;
            }
            default:
                break;
        }
    }

    /**
     * 选择按键标签样式
     */
    private void chooseLabel() {
        String[] single_list = {"在按鍵上显示'C4','D4'", "在按键上显示'Do','Re'", "关闭"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("显示按键标签");
        builder.setSingleChoiceItems(single_list, defaultId, (dialog, which) -> {
            defaultId = which;
            switch (which) {
                case 0: {
                    pf.setTone(PreferenceService.D4);
                    mPianoView.setPf(pf);
                    break;
                }
                case 1: {
                    pf.setTone(PreferenceService.DO);
                    mPianoView.setPf(pf);
                    break;
                }
                default: {
                    pf.setTone(PreferenceService.BLANK);
                    mPianoView.setPf(pf);
                    break;
                }
            }
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PIANOPLAYACTIVITY_REQUEST: {
                switch (resultCode) {
                    case FilesShowActivity.PLAY: {
                        String fileName = data.getStringExtra(FilesShowActivity.FILE_NAME);
                        mPresenter.playAudio(fileName);
                        isPlaying = true;
                        drawerLayout.closeDrawer(Gravity.RIGHT);
                        break;
                    }
                    case FilesShowActivity.RECORD: {
                        srcFileName = data.getStringExtra(FilesShowActivity.FILE_NAME);
                        mPresenter.playAudioAndRecord(srcFileName);
                        isRecording = true;
                        isPlaying = true;
                        mPianoView.setRecord(true);
                        drawerLayout.closeDrawer(Gravity.RIGHT);
                    }
                    default: {
                        break;
                    }
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mProgress = progress;
        seekBar.setProgress(mPresenter.handleProgress(progress));
        Log.i("progress", "realProgress: " + progress);
        Log.i("precent", "precent: " + mPresenter.getSeekBarPercent());
        mPianoView.setProgress(mPresenter.getSeekBarPercent());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        linearLayout.setX(-slideOffset * drawerView.getWidth());
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }
}
