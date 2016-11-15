package com.example.user.malendar;

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.media.ExifInterface
import android.os.Bundle
import android.provider.MediaStore.MediaColumns
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.squareup.timessquare.CalendarPickerView
import java.io.File
import java.io.IOException
import java.util.*


public class MainActivity : Activity() {

    private val calendar by lazy {
        findViewById(R.id.calendar_view) as CalendarPickerView
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        val lastMonth = Calendar.getInstance();
        val thisMonth = Calendar.getInstance();
        thisMonth.set(Calendar.DATE,1);
        lastMonth.set(Calendar.DATE, lastMonth.getMaximum(Calendar.DAY_OF_MONTH));

        val today = thisMonth.getTime();
        calendar.init( today, lastMonth.getTime())
                .withSelectedDate(today);

        calendar.setOnDateSelectedListener( object :  CalendarPickerView.OnDateSelectedListener {
            override fun onDateSelected(date : Date) {

                TedPermission(applicationContext)
                        .setPermissionListener(object : PermissionListener {
                            override fun onPermissionGranted() {
                                val result = ArrayList<ImageList>()
                                val uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                val projection = arrayOf(MediaColumns.DATA, MediaColumns.DISPLAY_NAME)

                                val cursor = contentResolver.query(uri, projection, null, null, MediaColumns.DATE_ADDED + " desc")
                                val columnIndex = cursor!!.getColumnIndexOrThrow(MediaColumns.DATA)
                                val columnDisplayname = cursor.getColumnIndexOrThrow(MediaColumns.DISPLAY_NAME)

                                var month : String = "0"
                                var day : String  = "0"

                                var lastIndex: Int

                                while (cursor.moveToNext()) {
                                    val absolutePathOfImage = cursor.getString(columnIndex)
                                    val nameOfFile = cursor.getString(columnDisplayname)
                                    lastIndex = absolutePathOfImage.lastIndexOf(nameOfFile)
                                    lastIndex = if (lastIndex >= 0) lastIndex else nameOfFile.length - 1

                                    if (!TextUtils.isEmpty(absolutePathOfImage)) {
                                        try {
                                            val exifInterface = ExifInterface(absolutePathOfImage)
                                            val tmp = exifInterface.getAttribute(ExifInterface.TAG_DATETIME) ?: continue
                                            val cal = Calendar.getInstance()
                                            cal.time = date
                                            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1)

                                            month = tmp.substring(5, 7)
                                            day = tmp.substring(8, 10)

                                            if (cal.get(Calendar.MONTH) === Integer.parseInt(tmp.substring(5, 7))
                                                    && cal.get(Calendar.DATE) === Integer.parseInt(tmp.substring(8, 10))) {
                                                Log.i("dat2", absolutePathOfImage)
                                                result.add(ImageList(File(absolutePathOfImage), nameOfFile))
                                            }

                                        } catch (e: IOException) {
                                            e.printStackTrace()
                                        }

                                        //result.add(absolutePathOfImage);
                                    }
                                }
                                if (result.size > 0) openActivity(result, month, day)
                            }

                            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                                Toast.makeText(this@MainActivity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
                            }


                        })
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();

            }
            override fun onDateUnselected(date : Date ) {

            }
        });


    }

    fun openActivity(list: ArrayList<ImageList>, month: String, day : String) {
        val it = Intent(this, ViewImageActivity::class.java)
        it.putExtra("list", list);
        it.putExtra("month", month);
        it.putExtra("day", day);
        startActivity(it)
    }
}
