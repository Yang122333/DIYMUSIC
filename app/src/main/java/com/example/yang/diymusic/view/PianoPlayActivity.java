package com.example.yang.diymusic.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.SeekBar;

import com.example.yang.diymusic.PianoView;
import com.example.yang.diymusic.PreferenceService;
import com.example.yang.diymusic.R;
import com.example.yang.diymusic.presenter.PianoPresenter;
import com.example.yang.diymusic.presenter.ui.PianoUI;

public class PianoPlayActivity extends BaseActivity<PianoPresenter<PianoUI>, PianoUI> implements PianoUI {

    private SeekBar seekBar;
    private PianoView mPianoView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        }

    private void init(){
        seekBar = findViewById(R.id.overview);
        mPianoView = findViewById(R.id.piano_view);
        seekBar.setMax(PianoPresenter.FULL);
        Log.i("width",  seekBar.getThumb().getMinimumWidth()+"");
        seekBar.setProgress(PianoPresenter.START);
        PreferenceService pf = new PreferenceService();
        pf.setKeyNumber(20);
        pf.setMode(2);
        pf.setTone(PreferenceService.D4);
        mPianoView.setPf(pf);
    }
    @Override
    protected void onResume() {
        super.onResume();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.setProgress(mPresenter.handleProgress(progress));
                Log.i("progress", "realProgress: "+progress);
                Log.i("precent", "precent: "+mPresenter.getSeekBarPercent());
                mPianoView.setProgress(mPresenter.getSeekBarPercent());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public int getLayout() {
        return R.layout.piano_playing_layout;
    }

    @Override
    public PianoPresenter<PianoUI> createPresenter() {
        return new PianoPresenter<>();
    }

    @Override
    public void show() {

    }

    @Override
    public void cancel() {

    }
}
