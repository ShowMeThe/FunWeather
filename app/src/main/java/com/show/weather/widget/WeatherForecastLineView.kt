package com.show.weather.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.show.kcore.extras.display.dp
import kotlin.math.abs

class WeatherForecastLineView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private val upperData = ArrayList<Int>()
    private val lowerData = ArrayList<Int>()
    private var mPartHeight = 0f
    private var mPartWidth = 0f
    private var mPoints = ArrayList<Points>()
    private val mLinePaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#7f5677fc")
            strokeWidth = 1.5f.dp
            style = Paint.Style.STROKE
        }
    }
    private val mCirclePaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#5677fc")
            style = Paint.Style.FILL
        }
    }
    private val mTextPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#5677fc")
            textAlign = Paint.Align.CENTER
            textSize = 15f.dp
        }
    }
    private val path = Path()

    fun updateData(up: List<Int>, low: List<Int>) {
        upperData.clear()
        lowerData.clear()
        upperData.addAll(up)
        lowerData.addAll(low)
        initData()
        postInvalidate()
    }

    private fun initData() {
        mPartWidth = measuredWidth / upperData.size.toFloat()
        val mPartDivider = mPartWidth / 2f
        val contentH = mPartHeight * 0.35f
        mPoints.clear()
        val upMax = upperData.maxOrNull() ?: 0
        val upMin = upperData.minOrNull() ?: 0
        val upDp = contentH / abs(upMax - upMin)

        val lowMax = lowerData.maxOrNull() ?: 0
        val lowMin = lowerData.minOrNull() ?: 0
        val lowDp = contentH / abs(lowMax - lowMin)

        upperData.forEachIndexed { index, value ->

            /**
             * 第一个值的y永远在中间开始出发
             */
            val x = index * mPartWidth + mPartDivider
            val offset = upMax - value
            val y = mPartHeight / 2f + upDp * offset
            mPoints.add(Points(x, y,value))
        }
        lowerData.forEachIndexed { index, value ->
            /**
             * 第一个值的y永远在中间开始出发
             */
            val x = index * mPartWidth + mPartDivider
            val offset = lowMax - value
            val y = mPartHeight + mPartHeight / 2f + lowDp * offset
            mPoints.add(Points(x, y,value))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        mPartHeight = heightSize / 2f

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        path.reset()
        mPoints.forEachIndexed { index, it ->
            if (index == 0 || index == ((mPoints.size / 2f)).toInt()) {
                path.moveTo(0f, it.y)
            }
            path.lineTo(it.x, it.y)

            if (index == ((mPoints.size / 2f) - 1).toInt() || index == (mPoints.size - 1)) {
                path.lineTo(measuredWidth.toFloat(), it.y)
            }

            canvas.drawCircle(it.x,it.y,2f.dp,mCirclePaint)
            canvas.drawText("${it.value}°C",it.x - 5f.dp,it.y - 11f.dp,mTextPaint)
        }

        canvas.drawPath(path, mLinePaint)
        canvas.restore()
    }


    data class Points(val x: Float, val y: Float,val value:Int)

}