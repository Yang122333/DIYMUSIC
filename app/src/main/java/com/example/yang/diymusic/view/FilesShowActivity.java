package com.example.yang.diymusic.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.yang.diymusic.R;
import com.example.yang.diymusic.audio.AudioService;
import com.example.yang.diymusic.audio.FileUtil;

import java.io.File;
import java.util.ArrayList;

public class FilesShowActivity extends Activity {
    public static final int FILESSHOWACTIVITY_RESULT = 2;
    public static String FILE_NAME = "fileName";
    private ListView mlistView;

    public static void start(Context context) {
        Intent intent = new Intent(context, FilesShowActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musicfilelist_layout);
        mlistView = findViewById(R.id.file_list);
        File file = new File(AudioService.getInstance(this).MUSIC);
        ArrayList<String> lists = FileUtil.showFileList(file);
        if(lists == null){
            return;
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, lists);
        mlistView.setAdapter(arrayAdapter);
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(FILE_NAME,lists.get(position));
                setResult(FILESSHOWACTIVITY_RESULT,intent);
                FilesShowActivity.this.finish();
            }
        });
    }

}
