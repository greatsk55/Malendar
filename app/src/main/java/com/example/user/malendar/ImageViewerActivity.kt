package com.example.user.malendar

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.transition.Fade
import android.transition.Transition
import android.view.View
import android.widget.ImageView
import java.io.File

class ImageViewerActivity : AppCompatActivity() {
    /***********************

     * Model

     */

    var thumbnail: ImageView by lazy{
        thumbnail = findViewById(R.id.thumbnail) as ImageView
    }
    var image: ViewTouchImage

    private val TOOLBAR_SHOW = 0
    private val TOOLBAR_SIZE = -400
    private val DELAY = 2500
    private var toolbar: Toolbar? = null
    private var isAnimated = false

    private var url: String? = null
    private var bmp: Bitmap? = null
    private var bm: Bitmap? = null


    //이미지 트랜지션 애니메이션이 끝나고 이미지 로드가 끝났는지를 체크하기 위한 변수
    private var isLoad: Boolean = false

    /***********************

     * View

     */
    private fun init() {
        toolbar = findViewById(R.id.toolbar) as Toolbar
        image = findViewById(R.id.image) as ViewTouchImage


        setSupportActionBar(toolbar)
        supportActionBar!!.setShowHideAnimationEnabled(true)

        isLoad = false

        uiDrawer = Handler(Looper.getMainLooper())
        getIntentData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val w = window
            w.statusBarColor = ContextCompat.getColor(this, R.color.exam_image_theme)
            w.enterTransition = Fade()

            //애니메이션 종료시점을 받아오기 위한 리스너.
            w.enterTransition.addListener(object : Transition.TransitionListener {
                override fun onTransitionStart(transition: Transition) {
                }

                override fun onTransitionEnd(transition: Transition) {
                    thumbnail.post {
                        //이미지 로드가 끝났다면 이미지를 보이게하고 썸네일을 감춘다.
                        if (isLoad) {
                            image.visibility = View.VISIBLE
                            thumbnail.alpha = 0f
                        } else {
                            //먼저 애니메이션이끝나면 끝났음을 알린다.
                            isLoad = true
                        }
                    }
                }

                override fun onTransitionCancel(transition: Transition) {
                }

                override fun onTransitionPause(transition: Transition) {
                }

                override fun onTransitionResume(transition: Transition) {
                }
            })
        }

        setContentView(R.layout.activity_exam_image_viewer)
        init()

        setListener()
    }

    public override fun onDestroy() {
        super.onDestroy()

        //image transition을 위해 썸네일을 보여준다.
        thumbnail.alpha = 1f
        val d1 = image.drawable
        image.setImageResource(0)
        if (d1 is BitmapDrawable) {
            val bitmap = d1.bitmap
            bitmap.recycle()
        }
    }

    override fun onBackPressed() {
        //image transition을 위해 썸네일을 보여준다.
        thumbnail.alpha = 1f
        super.onBackPressed()
    }


    // 화면 전환시 액티비티를 재생성하지 않게하기위해 오버라이드해준다.
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        //이미지를 다시불러오게 처리한다.
        when (newConfig.orientation) {
            Configuration.ORIENTATION_PORTRAIT, Configuration.ORIENTATION_LANDSCAPE -> if (url != null && url!!.startsWith("http")) {
                SimpleImageLoader(object : SimpleImageLoader.OnCompletedListener() {
                    fun onCompleted(bitmap: Bitmap?) {
                        if (bitmap != null) {
                            image.setImageBitmap(bitmap)

                            //이미지 로드가 끝났다면 이미지를 보이게하고 썸네일을 감춘다.
                            if (isLoad || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                image.visibility = View.VISIBLE
                                thumbnail.alpha = 0f
                            } else {
                                isLoad = true
                            }
                        }
                    }
                }).execute(url)
            } else {
                val task = ImageLoadTask()
                task.execute()
            }
        }

    }

    /***********************

     * Presenter

     */
    private fun getIntentData() {
        val intent = intent
        url = intent.getStringExtra("image")
        bmp = ObjectBusProvider.getInstance().get("img") as Bitmap

        thumbnail.setImageBitmap(bmp)


        //url이
        if (url != null && url!!.startsWith("http")) {
            SimpleImageLoader(object : SimpleImageLoader.OnCompletedListener() {
                fun onCompleted(bitmap: Bitmap?) {
                    if (bitmap != null) {
                        image.setImageBitmap(bitmap)

                        if (isLoad || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            image.visibility = View.VISIBLE
                            thumbnail.alpha = 0f
                        } else {
                            isLoad = true
                        }
                    }
                }
            }).execute(url)
        } else {
            val task = ImageLoadTask()
            task.execute()
        }
    }


    private fun setListener() {
        image.mCommand = object : ViewTouchImage.Command() {
            fun show() {
                if (isAnimated) {
                    supportActionBar!!.show()
                    toolbar!!.animate().translationY(TOOLBAR_SHOW.toFloat()).setDuration(400L).start()
                    isAnimated = false
                    mHandler.removeCallbacks(touchCallback)
                    mHandler.postDelayed(touchCallback, DELAY.toLong())
                }
            }

            fun hide() {
                if (!isAnimated) {
                    isAnimated = true
                    toolbar!!.animate().translationY(TOOLBAR_SIZE.toFloat()).setDuration(400L)
                            .withEndAction { supportActionBar!!.hide() }.start()
                }
            }
        }

    }

    fun onClicked(v: View) {
        when (v.id) {
            R.id.toolbarBtnClose -> finish()
        }
    }

    private val mHandler = Handler()
    private val touchCallback = Runnable {
        if (!isAnimated) {
            isAnimated = true
            toolbar!!.animate().translationY(TOOLBAR_SIZE.toFloat()).setDuration(300L)
                    .withEndAction { supportActionBar!!.hide() }.start()
        }
        image.isDrag = true
    }

    private var uiDrawer: Handler? = null
    private val uiCallback = Runnable {
        image.setImageBitmap(bm)

        //이미지 로드가 끝났다면 이미지를 보이게하고 썸네일을 감춘다.
        if (isLoad || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            image.visibility = View.VISIBLE
            thumbnail.alpha = 0f
        } else {
            isLoad = true
        }
    }

    private inner class ImageLoadTask : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void): Void? {

            //url이 file로 시작하거나 http일 경우
            if (url != null && !url!!.startsWith("http") && (url!!.startsWith("file") || url!!.startsWith("File"))) {
                try {
                    bm = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(url))
                } catch (e: Exception) {
                    if (Logr.DEBUG) e.printStackTrace()
                    ToastUtil.show(getString(R.string.str_exam_photo_error))
                    isLoad = true
                }

            } else if (url != null) {
                try {
                    bm = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(File(url!!)))
                } catch (e: Exception) {
                    if (Logr.DEBUG) e.printStackTrace()
                    ToastUtil.show(getString(R.string.str_exam_photo_error))
                    isLoad = true
                }

            }

            bm = getOrientationBitmap(Uri.parse(url), bm)
            uiDrawer!!.post(uiCallback)


            return null
        }
    }
}
