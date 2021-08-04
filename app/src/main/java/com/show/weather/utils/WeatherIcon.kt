package com.show.weather.utils

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.show.kcore.base.AppContext
import com.show.weather.R
import java.text.SimpleDateFormat
import java.util.*

object WeatherIcon {

    private val instant = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    private val calendar by lazy {
        Calendar.getInstance()
    }

    fun getTypeByIcon(icon: Icon, reportTime: String): Type {
        return when (icon) {
            Icon.WIND -> Type(
                icon,
                ContextCompat.getDrawable(AppContext.getContext(), R.drawable.ic_wind),
                Behavior.FADE
            )
            Icon.RAIN, Icon.RAIN_1, Icon.RAIN_2 -> Type(
                icon,
                ContextCompat.getDrawable(AppContext.getContext(), R.drawable.ic_rain),
                Behavior.WAVE
            )
            Icon.SUN -> {
                var behavior = Behavior.ROTATE
                val drawable = instant.parse(reportTime)?.let {
                    calendar.timeInMillis = it.time
                    val hour = calendar[Calendar.HOUR_OF_DAY]
                    if(hour in 18..24 || hour in 0 .. 5){
                        behavior = Behavior.JUMP
                        ContextCompat.getDrawable(AppContext.getContext(), R.drawable.ic_moon)
                    }else{
                        ContextCompat.getDrawable(AppContext.getContext(), R.drawable.ic_sun)
                    }
                }
                return Type(
                    icon,
                    drawable,
                    behavior
                )
            }
            Icon.CLOUD_1, Icon.CLOUD_2 -> Type(
                icon,
                ContextCompat.getDrawable(
                    AppContext.getContext(),
                    R.drawable.ic_cloud
                )?.apply {
                    colorFilter = PorterDuffColorFilter(
                        Color.WHITE, PorterDuff.Mode.SRC_IN
                    )
                }, Behavior.WAVE
            )
        }
    }
}

class Type(val icon: Icon, val logo: Drawable?, val behavior: Behavior)

enum class Behavior {
    ROTATE, WAVE, FADE,JUMP
}


enum class Icon {
    RAIN, RAIN_1, RAIN_2, WIND, SUN, CLOUD_1, CLOUD_2
}