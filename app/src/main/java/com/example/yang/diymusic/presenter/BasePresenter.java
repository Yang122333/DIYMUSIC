package com.example.yang.diymusic.presenter;

import java.lang.ref.WeakReference;

public  class BasePresenter<V>  {
    protected WeakReference<V> reference;

    public void attachView(V view){
        reference = new WeakReference<V>(view);
    }
    public V getUi(){
        return reference.get();
    }
    public boolean isViewAttached(){
        return reference != null && reference.get() != null;
    }
    public void disAttachView(){
        if(reference != null){
            reference.clear();
            reference = null;
        }
    }

}
