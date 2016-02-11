package com.example.user.malendar;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.*;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.squareup.timessquare.CalendarPickerView;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.MONTH, 1);

        CalendarPickerView calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        Date today = new Date();
        calendar.init(today, nextYear.getTime())
                .withSelectedDate(today);

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {

                ArrayList<ImageList> result = new ArrayList<ImageList>();
                Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String[] projection = { MediaColumns.DATA, MediaColumns.DISPLAY_NAME };

                Cursor cursor = getContentResolver().query(uri, projection, null, null, MediaColumns.DATE_ADDED + " desc");
                int columnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
                int columnDisplayname = cursor.getColumnIndexOrThrow(MediaColumns.DISPLAY_NAME);

                int lastIndex;
                while (cursor.moveToNext())
                {
                    String absolutePathOfImage = cursor.getString(columnIndex);
                    String nameOfFile = cursor.getString(columnDisplayname);
                    lastIndex = absolutePathOfImage.lastIndexOf(nameOfFile);
                    lastIndex = lastIndex >= 0 ? lastIndex : nameOfFile.length() - 1;

                    if (!TextUtils.isEmpty(absolutePathOfImage))
                    {
                        try {
                            ExifInterface exifInterface = new ExifInterface(absolutePathOfImage);
                            String tmp = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                            if(tmp==null) continue;
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)+1);

                            if(cal.get(Calendar.MONTH)==Integer.parseInt(tmp.substring(5, 7)) &&
                                    cal.get(Calendar.DATE)==Integer.parseInt(tmp.substring(8, 10))){
                                Log.i("dat2", absolutePathOfImage);
                                result.add(new ImageList(absolutePathOfImage,nameOfFile));
                            }

                        }catch(IOException e){
                            e.printStackTrace();
                        }
                        //result.add(absolutePathOfImage);
                    }
                }
                if( result.size()>0) openActivity(result);
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
    }

    public void openActivity(ArrayList<ImageList> list){
        Intent it = new Intent(this, ViewImageActivity.class);
        it.putExtra("list", list);
        startActivity(it);
    }
}
