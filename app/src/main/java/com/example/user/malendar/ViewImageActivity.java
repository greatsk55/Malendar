package com.example.user.malendar;

import android.app.Activity;
import android.graphics.Color;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by user on 16. 2. 11.
 */

//TODO 이미지에 해당 년도도 나오게하기, 액티비티 타이틀바에 해당 월일 나오게하기
public class ViewImageActivity extends Activity {

    public RecyclerView recyclerView;
    public ImageRecyclerAdapter contactAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ArrayList<ImageList> list = (ArrayList<ImageList>) getIntent().getSerializableExtra("list");

        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).
                color(Color.LTGRAY).sizeResId(R.dimen.divider).marginResId(R.dimen.leftmargin, R.dimen.rightmargin).build());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        contactAdapter = new ImageRecyclerAdapter(this,
                list, (LinearLayoutManager) recyclerView.getLayoutManager());
        recyclerView.setAdapter(contactAdapter);

        try {
            ExifInterface exifInterface = new ExifInterface(list.get(0).path);
            String tmp = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);

            setTitle(tmp.substring(5,7)+"월 "+ tmp.substring(8,10)+"일");

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
