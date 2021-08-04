package com.show.weather.widget

import android.animation.Animator
import android.animation.ValueAnimator.RESTART
import android.animation.ValueAnimator.REVERSE
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.animation.*
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.children
import com.show.kcore.extras.display.dp
import com.show.weather.utils.Behavior
import com.show.weather.utils.Icon
import com.show.weather.utils.WeatherIcon
import java.util.*
import kotlin.collections.ArrayList

class WeatherView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val mDuration = 5000L
    private val mInterpolator = LinearInterpolator()
    private val iconStacks = ArrayList<Icon>()
    private val animatorsList = ArrayList<Animator>()
    private val defaultLargeSize = 65f.dp.toInt()
    private val defaultSize = 55f.dp.toInt()
    private val defaultSmallSize = 35f.dp.toInt()
    private var mReportTime = ""

    fun updateReportTime(time: String){
        mReportTime = time
    }

    fun resetIcons(vararg icon: Icon) {
        children.forEach {
            if(it is ImageView){
                it.clearAnimation()
            }
        }
        removeAllViews()
        iconStacks.clear()
        iconStacks.addAll(icon)
        resetLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(125f.dp.toInt(), 85f.dp.toInt())
    }

    private fun resetLayout() {
        val types = iconStacks.map { WeatherIcon.getTypeByIcon(it,mReportTime) }
        types.forEachIndexed { index, type ->
            when (type.icon) {
                Icon.SUN -> createAnimatorView(type.behavior, index, Gravity.CENTER, type.logo)
                Icon.CLOUD_1 -> createAnimatorView(
                    type.behavior,
                    index,
                    Gravity.BOTTOM or Gravity.CENTER,
                    type.logo,
                    width = defaultLargeSize,
                    height = defaultLargeSize
                )
                Icon.CLOUD_2 -> createAnimatorView(
                    type.behavior,
                    index,
                    Gravity.CENTER or Gravity.START,
                    type.logo, offsetX = 25
                )
                Icon.RAIN -> createAnimatorView(
                    type.behavior,
                    index,
                    Gravity.CENTER,
                    type.logo,
                    width = defaultSmallSize, height = defaultSmallSize,
                )
                Icon.RAIN_1 -> createAnimatorView(
                    type.behavior,
                    index,
                    Gravity.START or Gravity.TOP,
                    type.logo,
                    width = defaultSmallSize, height = defaultSmallSize, offsetX = 25
                )
                Icon.RAIN_2 -> createAnimatorView(
                    type.behavior,
                    index,
                    Gravity.END or Gravity.TOP,
                    type.logo,
                    width = defaultSmallSize, height = defaultSmallSize, offsetX = 25
                )
                Icon.WIND -> createAnimatorView(
                    type.behavior,
                    index,
                    Gravity.BOTTOM or Gravity.START,
                    type.logo,
                    width = defaultSmallSize, height = defaultSmallSize,
                    offsetX = 25
                )
            }
        }
        requestLayout()
    }

    private fun createAnimatorView(
        behavior: Behavior, index: Int,
        gravity: Int,
        drawable: Drawable?,
        width: Int = defaultSize, height: Int = defaultSize,
        offsetX: Int = 0, offsetY: Int = 0
    ) {
        val view = createIv(index, width, height, gravity, drawable, offsetX, offsetY)
        when (behavior) {
            Behavior.JUMP ->{
                val translateAnimation = TranslateAnimation(
                    0f,
                    0f, -25f, 0f
                )
                translateAnimation.apply {
                    duration = mDuration
                    interpolator = mInterpolator
                    repeatCount = -1
                    repeatMode = REVERSE
                }
                view.startAnimation(translateAnimation)
            }
            Behavior.ROTATE -> {
                val rotateAnimation = RotateAnimation(
                    0f, 360f, Animation.RELATIVE_TO_SELF,
                    0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                )
                rotateAnimation.apply {
                    duration = mDuration
                    interpolator = mInterpolator
                    repeatCount = -1
                }
                view.startAnimation(rotateAnimation)
            }
            Behavior.WAVE -> {
                val translateAnimation = TranslateAnimation(
                    -25f,
                    25f, 0f, 0f
                )
                translateAnimation.apply {
                    duration = 10000
                    interpolator = mInterpolator
                    repeatCount = -1
                    repeatMode = REVERSE
                }
                view.startAnimation(translateAnimation)
            }
            Behavior.FADE -> {
                val translateAnimation = TranslateAnimation(
                    0f,
                    45f.dp, 0f, 0f
                )
                translateAnimation.apply {
                    duration = mDuration
                    interpolator = mInterpolator
                    repeatCount = -1
                    repeatMode = RESTART
                }
                val fadeAnimation = AlphaAnimation(1f, 0.3f)
                translateAnimation.apply {
                    duration = 1000
                    interpolator = mInterpolator
                    repeatCount = -1
                    repeatMode = RESTART
                }
                val set = AnimationSet(false)
                set.addAnimation(translateAnimation)
                set.addAnimation(fadeAnimation)
                view.startAnimation(set)
            }
        }


    }

    private fun createIv(
        index: Int, width: Int, height: Int,
        gravity: Int,
        drawable: Drawable?, offsetX: Int = 0, offsetY: Int = 0
    ): ImageView {
        val view = ImageView(context)
        view.setImageDrawable(drawable)
        addViewInLayout(view, index, LayoutParams(width, height).also {
            it.gravity = gravity
            if(gravity and Gravity.END == Gravity.END){
                it.rightMargin = offsetX
            }else{
                it.leftMargin = offsetX
            }
            it.topMargin = offsetY
        }, true)
        return view
    }
}