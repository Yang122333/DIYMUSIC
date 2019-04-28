package com.example.yang.diymusic.test;

import com.example.yang.diymusic.R;
import com.example.yang.diymusic.view.BaseActivity;

public class TestActivity extends BaseActivity<TestPresenter<TestUI>,TestUI>  implements TestUI {
    @Override
    public int getLayout() {
        return R.layout.piano_playing_layout;
    }

    @Override
    public TestPresenter<TestUI> createPresenter() {
        return new TestPresenter<>();
    }

    @Override
    public void haha() {
    }

    @Override
    public void hsfa() {

    }

    @Override
    public void show() {

    }

    @Override
    public void cancel() {

    }
}
