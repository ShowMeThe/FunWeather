package com.show.weather.entity

import com.show.kcore.http.JsonResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherForecast(
    @Json(name = "count")
    val count: String,
    @Json(name = "forecasts")
    val forecasts: List<Forecast>,
    @Json(name = "info")
    val info: String,
    @Json(name = "infocode")
    val infocode: String,
) : JsonData()

@JsonClass(generateAdapter = true)
data class Forecast(
    @Json(name = "adcode")
    val adcode: String,
    @Json(name = "casts")
    val casts: List<Cast>,
    @Json(name = "city")
    val city: String,
    @Json(name = "province")
    val province: String,
    @Json(name = "reporttime")
    val reporttime: String
)

@JsonClass(generateAdapter = true)
data class Cast(
    @Json(name = "date")
    val date: String,
    @Json(name = "daypower")
    val daypower: String,
    @Json(name = "daytemp")
    val daytemp: String,
    @Json(name = "dayweather")
    val dayweather: String,
    @Json(name = "daywind")
    val daywind: String,
    @Json(name = "nightpower")
    val nightpower: String,
    @Json(name = "nighttemp")
    val nighttemp: String,
    @Json(name = "nightweather")
    val nightweather: String,
    @Json(name = "nightwind")
    val nightwind: String,
    @Json(name = "week")
    val week: String
)


@JsonClass(generateAdapter = true)
open class JsonData : JsonResult {

    var status = ""

    override fun isLegal(): Boolean = status == "1"

}