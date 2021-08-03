package com.show.weather

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.show.kcore.base.AppContext

object WeatherIcon {

    fun getTypeByIcon(icon: Icon): Type {
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
            Icon.SUN -> Type(
                icon,
                ContextCompat.getDrawable(AppContext.getContext(), R.drawable.ic_sun),
                Behavior.ROTATE
            )
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
    ROTATE, WAVE, FADE
}


enum class Icon {
    RAIN,RAIN_1,RAIN_2, WIND, SUN, CLOUD_1, CLOUD_2
}