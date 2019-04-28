package com.example.yang.diymusic.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.yang.diymusic.presenter.BasePresenter;

public abstract class BaseActivity<P extends BasePresenter<V>, V> extends AppCompatActivity {
    public P mPresenter;
    public static final String BASETAG = "-----enter-----";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(getLayout());
        Log.i(BASETAG, this.getLocalClassName() + " : onCreate");
        mPresenter = createPresenter();
        mPresenter.attachView((V) this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(BASETAG, this.getLocalClassName() + " : onDestroy");
        mPresenter.disAttachView();
    }

    public abstract int getLayout();

    public abstract P createPresenter();
}
