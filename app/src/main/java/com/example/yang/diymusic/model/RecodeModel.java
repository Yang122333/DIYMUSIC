package com.example.yang.diymusic.model;

import java.util.ArrayList;
import java.util.List;

public class RecodeModel {
    List<PressInformation> recodeList = new ArrayList<>();
    public List getList(){
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
    public void addEvent(int key, int index) {
        PressInformation pi = new PressInformation();
        pi.setPressTime(System.currentTimeMillis());
        pi.setButtonId(SoundModel.findId(key,index));
        recodeList.add(pi);
    }
}
