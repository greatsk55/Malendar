package com.example.user.malendar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by user on 16. 2. 11.
 */
public class ViewImageActivity extends Activity {

    public RecyclerView recyclerView;
    public ImageRecyclerAdapter contactAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = (RecyclerView) findViewById(R.id.list);

        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).
                color(Color.LTGRAY).sizeResId(R.dimen.divider).marginResId(R.dimen.leftmargin, R.dimen.rightmargin).build());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ArrayList<ImageList> list = (ArrayList<ImageList>) getIntent().getSerializableExtra("list");

        contactAdapter = new ImageRecyclerAdapter(this,
                list, (LinearLayoutManager) recyclerView.getLayoutManager());
        recyclerView.setAdapter(contactAdapter);
    }
}
