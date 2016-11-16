package com.example.user.malendar

/**
 * Created by sk on 2016. 11. 16..
 */


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView

/*****************************
 * 사용법
 *
 *
 * 레이아웃 xml에
 * com.fourspeak.columbooks.ui.ViewTouchImage
 * 추가
 *
 *
 * 액티비티에서 ViewTouchImage 변수명;
 * 변수명 = (ViewTouchImage) findViewById(R.id.아이디명);
 * 변수명.setImageBitmap(비트맵객체);
 */

class ViewTouchImage @JvmOverloads constructor(context: Context, attrs: AttributeSet ?= null, defStyle: Int = 0) : ImageView(context, attrs, defStyle), View.OnTouchListener {

    private var _matrix = Matrix()
    private var savedMatrix = Matrix()
    private var savedMatrix2 = Matrix()
    private var mode = NONE

    private val start = PointF()
    private val mid = PointF()
    private var oldDist = 1f

    private var isInit = false


    /***********************************
     * 클릭시 툴바 사라지게끔 콜백 메소드 구현
     */
    interface Command {
        fun hide()

        fun show()
    }

    var mCommand: Command? = null
    var isDrag = false
    private var btnPressTime: Long = 0

    init {

        setOnTouchListener(this)
        scaleType = ImageView.ScaleType.MATRIX
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (D) Log.i(TAG, "onLayout")
        super.onLayout(changed, left, top, right, bottom)
        if (isInit == false) {
            init()
            isInit = true
        }
    }

    override fun setImageBitmap(bm: Bitmap) {
        if (D) Log.i(TAG, "setImageBitmap")
        super.setImageBitmap(bm)
        isInit = false
        init()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        if (D) Log.i(TAG, "setImageDrawable")
        super.setImageDrawable(drawable)
        isInit = false
        init()
    }

    override fun setImageResource(resId: Int) {
        if (D) Log.i(TAG, "setImageResource")
        super.setImageResource(resId)
        isInit = false
        init()
    }

    protected fun init() {
        _matrix = Matrix()
        savedMatrix = Matrix()
        savedMatrix2 = Matrix()
        matrixTurning(_matrix, this)
        imageMatrix = _matrix
        setImagePit()
    }

    /**
     * 이미지 핏
     */
    fun setImagePit() {

        // 매트릭스 값
        val value = FloatArray(9)
        this._matrix.getValues(value)

        value[4] = 500f
        value[0] = value[4]

        // 뷰 크기
        val width = this.width
        val height = this.height

        // 이미지 크기
        val d = this.drawable ?: return
        val imageWidth = d.intrinsicWidth
        val imageHeight = d.intrinsicHeight
        var scaleWidth = (imageWidth * value[0]).toInt()
        var scaleHeight = (imageHeight * value[4]).toInt()

        // 이미지가 바깥으로 나가지 않도록.

        value[2] = 0f
        value[5] = 0f

        if (imageWidth > width || imageHeight > height) {
            var target = WIDTH
            if (imageWidth < imageHeight) target = HEIGHT

            if (target == WIDTH) value[4] = width.toFloat() / imageWidth
            value[0] = value[4]
            if (target == HEIGHT) value[4] = height.toFloat() / imageHeight
            value[0] = value[4]

            scaleWidth = (imageWidth * value[0]).toInt()
            scaleHeight = (imageHeight * value[4]).toInt()

            if (scaleWidth > width) value[4] = width.toFloat() / imageWidth
            value[0] = value[4]
            if (scaleHeight > height) value[4] = height.toFloat() / imageHeight
            value[0] = value[4]
        }

        while (imageWidth * value[0] > width || imageHeight * value[4] > height) {
            value[0] = value[0] - 0.01f
            value[4] = value[4] - 0.01f
        }

        // 그리고 가운데 위치하도록 한다.
        scaleWidth = (imageWidth * value[0]).toInt()
        scaleHeight = (imageHeight * value[4]).toInt()
        if (scaleWidth < width) {
            value[2] = width.toFloat() / 2 - scaleWidth.toFloat() / 2
        }
        if (scaleHeight < height) {
            value[5] = height.toFloat() / 2 - scaleHeight.toFloat() / 2
        }

        _matrix.setValues(value)

        imageMatrix = _matrix
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val view = v as ImageView

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(_matrix)
                start.set(event.x, event.y)
                mode = DRAG
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    savedMatrix.set(_matrix)
                    midPoint(mid, event)
                    mode = ZOOM
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
                if (System.currentTimeMillis() > btnPressTime + 300) {
                    btnPressTime = System.currentTimeMillis()
                    if (!isDrag) {
                        mCommand!!.hide()
                        isDrag = true
                    } else {
                        mCommand!!.show()
                        isDrag = false
                    }
                } else {

                }
            }
            MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                _matrix.set(savedMatrix)
                _matrix.postTranslate(event.x - start.x, event.y - start.y)
            } else if (mode == ZOOM) {
                val newDist = spacing(event)
                if (newDist > 10f) {
                    _matrix.set(savedMatrix)
                    val scale = newDist / oldDist
                    _matrix.postScale(scale, scale, mid.x, mid.y)
                }
            }
        }

        // 매트릭스 값 튜닝.
        matrixTurning(_matrix, view)

        view.imageMatrix = _matrix

        return true
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }

    private fun matrixTurning(matrix: Matrix, view: ImageView) {
        // 매트릭스 값
        val value = FloatArray(9)
        matrix.getValues(value)
        val savedValue = FloatArray(9)
        savedMatrix2.getValues(savedValue)

        // 뷰 크기
        val width = view.width
        val height = view.height

        // 이미지 크기
        val d = view.drawable ?: return
        val imageWidth = d.intrinsicWidth
        val imageHeight = d.intrinsicHeight
        var scaleWidth = (imageWidth * value[0]).toInt()
        var scaleHeight = (imageHeight * value[4]).toInt()

        // 이미지가 바깥으로 나가지 않도록.
        if (value[2] < width - scaleWidth) value[2] = (width - scaleWidth).toFloat()
        if (value[5] < height - scaleHeight) value[5] = (height - scaleHeight).toFloat()
        if (value[2] > 0) value[2] = 0f
        if (value[5] > 0) value[5] = 0f

        // 10배 이상 확대 하지 않도록
        if (value[0] > 100 || value[4] > 100) {
            value[0] = savedValue[0]
            value[4] = savedValue[4]
            value[2] = savedValue[2]
            value[5] = savedValue[5]
        }

        // 화면보다 작게 축소 하지 않도록
        if (imageWidth > width || imageHeight > height) {
            if (scaleWidth < width && scaleHeight < height) {
                var target = WIDTH
                if (imageWidth < imageHeight) target = HEIGHT

                if (target == WIDTH) value[4] = width.toFloat() / imageWidth
                value[0] = value[4]
                if (target == HEIGHT) value[4] = height.toFloat() / imageHeight
                value[0] = value[4]

                scaleWidth = (imageWidth * value[0]).toInt()
                scaleHeight = (imageHeight * value[4]).toInt()

                if (scaleWidth > width) value[4] = width.toFloat() / imageWidth
                value[0] = value[4]
                if (scaleHeight > height) value[4] = height.toFloat() / imageHeight
                value[0] = value[4]
            }
        } else {
            if (value[0] < 1) value[0] = 1f
            if (value[4] < 1) value[4] = 1f
        }// 원래부터 작은 얘들은 본래 크기보다 작게 하지 않도록

        // 그리고 가운데 위치하도록 한다.
        scaleWidth = (imageWidth * value[0]).toInt()
        scaleHeight = (imageHeight * value[4]).toInt()
        if (scaleWidth < width) {
            value[2] = width.toFloat() / 2 - scaleWidth.toFloat() / 2
        }
        if (scaleHeight < height) {
            value[5] = height.toFloat() / 2 - scaleHeight.toFloat() / 2
        }

        matrix.setValues(value)
        savedMatrix2.set(matrix)
    }

    companion object {
        // 디버깅 정보
        private val TAG = "ViewTouchImage"
        private val D = false

        private val NONE = 0
        private val DRAG = 1
        private val ZOOM = 2

        private val WIDTH = 0
        private val HEIGHT = 1
    }
}