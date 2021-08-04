package com.show.weather.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Weather(var lives: List<LivesItem>, var count: String = "",
                   var infocode: String = "",
                   var info: String = "") : JsonData()


@JsonClass(generateAdapter = true)
data class LivesItem(@Json(name = "province")
                     val province: String = "",
                     @Json(name = "city")
                     val city: String = "",
                     @Json(name = "adcode")
                     val adcode: String = "",
                     @Json(name = "windpower")
                     val windpower: String = "",
                     @Json(name = "weather")
                     val weather: String = "",
                     @Json(name = "temperature")
                     val temperature: String = "",
                     @Json(name = "humidity")
                     val humidity: String = "",
                     @Json(name = "reporttime")
                     val reporttime: String = "",
                     @Json(name = "winddirection")
                     val winddirection: String = "")

