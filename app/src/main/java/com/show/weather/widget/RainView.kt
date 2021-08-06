package com.show.weather.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.show.kcore.extras.display.dp
import kotlin.random.Random

class RainView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val generate = LineGenerate()


    init {
        setBackgroundColor(Color.parseColor("#8f000000"))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        generate.updateBounds(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        generate.drawLine(canvas)
        postInvalidate()
    }


    inner class LineGenerate {

        private val linePaint by lazy {
            Paint().apply {
                color = Color.WHITE
                isAntiAlias = true
                strokeWidth = 1.5f.dp
                style = Paint.Style.FILL_AND_STROKE
            }
        }

        private val colors = arrayOf(getColor("#BBFFFF"),getColor("#53868B")
            ,getColor("#79CDCD"),getColor("#6495ED"),getColor("#4682B4"))
        private val line = ArrayList<Line>()
        private var width = 0
        private var height = 0
        private val lineSize = 65f.dp
        private val random = Random(System.currentTimeMillis())
        private val maxSize = 4
        private val minSize = 6

        private val lineMaxLength = 60
        private val boundSize = 50

        private fun getColor(color:String) = Color.parseColor(color)

        fun updateBounds(width: Int, height: Int) {
            this.width = width
            this.height = height
        }

        private fun generateNew() {
            if (line.isEmpty()) {
                createLine((lineMaxLength * 0.5f).toInt())
                postDelayed({
                    createLine((lineMaxLength * 0.5f).toInt())
                },1000)
            }
            val it = line.listIterator()
            while (it.hasNext()) {
                val line = it.next()
                if (line.fromY >= height * 0.9f) {
                    it.remove()
                }
            }
            if (line.size < boundSize) {
                createLine(lineMaxLength - boundSize)
            }
        }

        private fun createLine(size: Int){
            for (i in 0 until size) {
                val fromX = random.nextInt(0, width).toFloat()
                val fromY = random.nextFloat() * height / 2f
                val color = colors[random.nextInt(0,colors.size -1)]
                line.add(Line(fromX, fromY, lineSize,color))
            }
        }

        fun drawLine(canvas: Canvas) {
            generateNew()

            line.forEach {
                linePaint.color = it.color
                canvas.drawLine(it.fromX, it.fromY, it.fromX, it.endY, linePaint)
                it.fromY = it.fromY + 25f
            }


        }
    }

    private class Line(val fromX: Float, var fromY: Float, var offset: Float, val color: Int) {
        val endY get() = fromY + offset
    }

}