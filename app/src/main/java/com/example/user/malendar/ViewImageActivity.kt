package com.example.user.malendar;

import android.app.Activity;
import android.graphics.Color;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity
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
public class ViewImageActivity : AppCompatActivity() {


    private val recyclerView by lazy {
        findViewById(R.id.list) as RecyclerView
    }
    public var contactAdapter : ImageRecyclerAdapter ?= null

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        val list :  ArrayList<ImageList>  =  (getIntent().getSerializableExtra("list")) as ArrayList<ImageList>
        val month = (getIntent().getStringExtra("month"))
        val day = (getIntent().getStringExtra("day"))

        recyclerView.addItemDecoration( HorizontalDividerItemDecoration.Builder(this).
                color(Color.LTGRAY).sizeResId(R.dimen.divider).marginResId(R.dimen.leftmargin, R.dimen.rightmargin).build());
        recyclerView.setLayoutManager(LinearLayoutManager(this));
        recyclerView.setItemAnimator(DefaultItemAnimator());

        contactAdapter = ImageRecyclerAdapter(this,
                list, recyclerView.getLayoutManager() as (LinearLayoutManager) );
        recyclerView.setAdapter(contactAdapter);

        try {
            setTitle(month+"월 "+ day+"일");

        }catch(e : IOException ){
            e.printStackTrace();
        }
    }
}


