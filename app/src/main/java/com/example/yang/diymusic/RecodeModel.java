package com.example.yang.diymusic;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RecodeModel {
    List<Integer> recodeList = new ArrayList<>();
    Map<Long,Integer> lists = new HashMap<>();
    public  List getList(){
        return recodeList;
    }
    static RecodeModel recodeModel;
    private RecodeModel(){
    }
    public static RecodeModel getInstance(){
        if(recodeModel == null){
            recodeModel = new RecodeModel();
        }
        return recodeModel;
    }

    public void addEvent(int keyWhite, int index) {

        lists.put(System.currentTimeMillis(),index);
        Log.i("id", index+"");
    }
}
