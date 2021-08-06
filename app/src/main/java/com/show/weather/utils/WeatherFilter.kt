package com.show.weather.utils

import com.show.weather.R


object WeatherFilter {

    fun getWeatherByName(weather: String): Array<Icon> {
        return when (weather) {
            "晴","平静" -> arrayOf(Icon.SUN)
            "晴间多云", "阴","少云" -> arrayOf(Icon.SUN, Icon.CLOUD_1)
            "多云" -> arrayOf(Icon.SUN, Icon.CLOUD_1, Icon.CLOUD_2)
            "有风","微风","和风","清风" -> arrayOf(Icon.SUN, Icon.WIND)
            "小雨", "中雨","大雨" -> arrayOf(Icon.RAIN, Icon.RAIN_1)
            "暴雨","大暴雨","特大暴雨" -> arrayOf(Icon.RAIN, Icon.RAIN_1, Icon.RAIN_2, Icon.WIND)
            else -> arrayOf(Icon.SUN)
        }
    }


    fun getWeatherIconByName(weather: String): Int {
        return when (weather) {
            "晴","平静" -> R.drawable.ic_sun
            "晴间多云", "阴","少云","多云" -> R.drawable.ic_cloud_icon
            "有风","微风","和风","清风" -> R.drawable.ic_wind_icon
            "小雨", "中雨","大雨" -> R.drawable.ic_rain_icon
            "暴雨","大暴雨","特大暴雨" ->  R.drawable.ic_rain_heavy_icon
            else -> R.drawable.ic_sun
        }
    }

    fun getWeatherRain(weather: String): Boolean {
        return when (weather) {
            "暴雨","大暴雨","特大暴雨","小雨", "中雨","大雨" -> true
            else -> false
        }
    }

}