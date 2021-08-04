package com.show.weather.entity

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import com.show.kclock.dateTime
import com.show.kcore.base.AppContext
import com.show.weather.utils.WeatherFilter.getWeatherIconByName
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.*

data class ForecastItem(val date: String?, val dateInWeek: String?, val logo: Drawable?)


private val instant = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

private val calendar by lazy {
    Calendar.getInstance()
}

fun Cast.toForecastItem(): ForecastItem {
    calendar.timeInMillis = System.currentTimeMillis()
    val todayDay = calendar[Calendar.DAY_OF_MONTH]
    val todayMonth = calendar[Calendar.MONTH] + 1

    val time = date.split("-")
    val month = time[1].toInt()
    val day = time[2].toInt()
    val dateIn = if (todayDay == day && todayMonth == month) {
        "今天"
    } else {
        "${month}月${day}日"
    }
    val dateInWeak = instant.parse("$date 00:00:00")?.let {
        calendar.timeInMillis = it.time
        when (calendar[Calendar.DAY_OF_WEEK]) {
            1 -> "周日"
            2 -> "周一"
            3 -> "周二"
            4 -> "周三"
            5 -> "周四"
            6 -> "周五"
            7 -> "周六"
            else -> "周日"
        }
    }

    return ForecastItem(dateIn, dateInWeak,ContextCompat.getDrawable(AppContext.getContext(),getWeatherIconByName(dayweather)))
}