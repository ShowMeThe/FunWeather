package com.show.weather.widget

import android.content.Context
import android.graphics.*
import android.icu.util.Calendar
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter
import com.show.kcore.extras.display.dp
import kotlin.math.cos
import kotlin.math.sin


class SunriseView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mUp = ""
    private var mDown = ""
    private var mBitmap: Bitmap? = null
    private var baseLine = 0f
    private var mPadding = 10f.dp
    private var mTitle = ""
    private val srcRect = Rect()
    private val destRect = RectF()
    private val mBSize = 10f.dp.toInt()
    private var mProgressRad = 0.0

    private val instant by lazy {
        Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() }
    }

    private val mBitmapPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
        }
    }

    private val mTextPaint by lazy {
        TextPaint().apply {
            isAntiAlias = true
            color = Color.BLACK
            textSize = 13f.dp
            textAlign = Paint.Align.CENTER
        }
    }

    private val mLinePaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#5677fc")
            strokeWidth = 1f.dp
            style = Paint.Style.STROKE
            pathEffect = DashPathEffect(floatArrayOf(3f.dp, 3f.dp), 0f)
        }
    }

    fun updateSunRise(
        up: String,
        down: String,
        title: String,
        mIcon: Int
    ) {
        mUp = up
        mDown = down
        mTitle = title
        mBitmap = BitmapFactory.decodeResource(context.resources, mIcon)
        srcRect.set(0, 0, mBitmap!!.width, mBitmap!!.height)
        initTime()
        postInvalidate()
    }

    private fun initTime() {
        val starts = mUp.split(":")
        val startTime = starts[0].toInt() * 60 + starts[1].toInt()

        val ends = mDown.split(":")
        val endTime = ends[0].toInt() * 60 + ends[1].toInt()

        if(startTime > endTime){

        }

        val nowTime = instant[Calendar.HOUR_OF_DAY] * 60 + instant[Calendar.MINUTE]

        mProgressRad = nowTime / (endTime - startTime).toFloat() * -180.0 // 角度

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        baseLine = heightSize - 30f.dp

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = measuredWidth / 2f
        val centerY = baseLine
        val radius = baseLine - mPadding

        val left = centerX - radius
        val right = centerX + radius


        canvas.drawArc(
            left, centerY - radius, right,
            centerY + radius, 0f, -180f, false, mLinePaint
        )

        mTextPaint.typeface = Typeface.DEFAULT_BOLD
        mTextPaint.textAlign = Paint.Align.CENTER
        canvas.drawText(mTitle, measuredWidth / 2f, baseLine - mPadding / 2f, mTextPaint)

        mTextPaint.typeface = Typeface.DEFAULT
        mTextPaint.textAlign = Paint.Align.LEFT
        canvas.drawText(mUp, left, baseLine + mPadding, mTextPaint)
        mTextPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText(mDown, right, baseLine + mPadding, mTextPaint)


        mBitmap?.apply {
            val x = centerX + cos(Math.toRadians(mProgressRad)) * radius
            val y = centerY + sin(Math.toRadians(mProgressRad)) * radius
            destRect.set((x - mBSize).toFloat(), (y - mBSize).toFloat(), (x + mBSize).toFloat(),
                (y + mBSize).toFloat()
            )
            canvas.drawBitmap(this, null, destRect, mBitmapPaint)
        }

    }


}