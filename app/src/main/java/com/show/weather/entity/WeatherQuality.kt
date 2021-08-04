package com.show.weather.entity
import com.show.kcore.http.JsonResult
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
open class JsonQualityData : JsonResult {

    var code = ""

    override fun isLegal(): Boolean = code == "10000"

}


@JsonClass(generateAdapter = true)
data class WeatherQuality(
    @Json(name = "charge")
    val charge: Boolean,
    @Json(name = "msg")
    val msg: String,
    @Json(name = "requestId")
    val requestId: String,
    @Json(name = "result")
    val result: Result
):JsonQualityData()

@JsonClass(generateAdapter = true)
data class Result(
    @Json(name = "HeWeather5")
    val heWeather5: List<HeWeather5>
)

@JsonClass(generateAdapter = true)
data class HeWeather5(
    @Json(name = "aqi")
    val aqi: Aqi,
    @Json(name = "basic")
    val basic: Basic,
    @Json(name = "daily_forecast")
    val dailyForecast: List<DailyForecast>,
    @Json(name = "hourly_forecast")
    val hourlyForecast: List<HourlyForecast>,
    @Json(name = "now")
    val now: Now,
    @Json(name = "status")
    val status: String,
    @Json(name = "suggestion")
    val suggestion: Suggestion
)

@JsonClass(generateAdapter = true)
data class Aqi(
    @Json(name = "city")
    val city: City
)

@JsonClass(generateAdapter = true)
data class Basic(
    @Json(name = "city")
    val city: String,
    @Json(name = "cnty")
    val cnty: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "lat")
    val lat: String,
    @Json(name = "lon")
    val lon: String,
    @Json(name = "update")
    val update: Update
)

@JsonClass(generateAdapter = true)
data class DailyForecast(
    @Json(name = "astro")
    val astro: Astro,
    @Json(name = "cloud")
    val cloud: String,
    @Json(name = "cond")
    val cond: Cond,
    @Json(name = "date")
    val date: String,
    @Json(name = "hum")
    val hum: String,
    @Json(name = "pcpn")
    val pcpn: String,
    @Json(name = "pop")
    val pop: String,
    @Json(name = "pres")
    val pres: String,
    @Json(name = "tmp")
    val tmp: Tmp,
    @Json(name = "uv")
    val uv: String,
    @Json(name = "vis")
    val vis: String,
    @Json(name = "wind")
    val wind: Wind
)

@JsonClass(generateAdapter = true)
data class HourlyForecast(
    @Json(name = "cond")
    val cond: CondX,
    @Json(name = "date")
    val date: String,
    @Json(name = "hum")
    val hum: String,
    @Json(name = "pop")
    val pop: String,
    @Json(name = "pres")
    val pres: String,
    @Json(name = "tmp")
    val tmp: String,
    @Json(name = "wind")
    val wind: WindX
)

@JsonClass(generateAdapter = true)
data class Now(
    @Json(name = "cond")
    val cond: CondXX,
    @Json(name = "dew")
    val dew: String,
    @Json(name = "fl")
    val fl: String,
    @Json(name = "hum")
    val hum: String,
    @Json(name = "pcpn")
    val pcpn: String,
    @Json(name = "pres")
    val pres: String,
    @Json(name = "tmp")
    val tmp: String,
    @Json(name = "vis")
    val vis: String,
    @Json(name = "wind")
    val wind: WindXX
)

@JsonClass(generateAdapter = true)
data class Suggestion(
    @Json(name = "air")
    val air: Air,
    @Json(name = "comf")
    val comf: Comf,
    @Json(name = "cw")
    val cw: Cw,
    @Json(name = "drsg")
    val drsg: Drsg,
    @Json(name = "flu")
    val flu: Flu,
    @Json(name = "sport")
    val sport: Sport,
    @Json(name = "trav")
    val trav: Trav,
    @Json(name = "uv")
    val uv: Uv
)

@JsonClass(generateAdapter = true)
data class City(
    @Json(name = "aqi")
    val aqi: String,
    @Json(name = "co")
    val co: String,
    @Json(name = "no2")
    val no2: String,
    @Json(name = "o3")
    val o3: String,
    @Json(name = "pm10")
    val pm10: String,
    @Json(name = "pm25")
    val pm25: String,
    @Json(name = "qlty")
    val qlty: String,
    @Json(name = "so2")
    val so2: String
)

@JsonClass(generateAdapter = true)
data class Update(
    @Json(name = "loc")
    val loc: String,
    @Json(name = "utc")
    val utc: String
)

@JsonClass(generateAdapter = true)
data class Astro(
    @Json(name = "mr")
    val mr: String,
    @Json(name = "ms")
    val ms: String,
    @Json(name = "sr")
    val sr: String,
    @Json(name = "ss")
    val ss: String
)

@JsonClass(generateAdapter = true)
data class Cond(
    @Json(name = "code_d")
    val codeD: String,
    @Json(name = "code_n")
    val codeN: String,
    @Json(name = "txt_d")
    val txtD: String,
    @Json(name = "txt_n")
    val txtN: String
)

@JsonClass(generateAdapter = true)
data class Tmp(
    @Json(name = "max")
    val max: String,
    @Json(name = "min")
    val min: String
)

@JsonClass(generateAdapter = true)
data class Wind(
    @Json(name = "deg")
    val deg: String,
    @Json(name = "dir")
    val dir: String,
    @Json(name = "sc")
    val sc: String,
    @Json(name = "spd")
    val spd: String
)

@JsonClass(generateAdapter = true)
data class CondX(
    @Json(name = "code")
    val code: String,
    @Json(name = "txt")
    val txt: String
)

@JsonClass(generateAdapter = true)
data class WindX(
    @Json(name = "deg")
    val deg: String,
    @Json(name = "dir")
    val dir: String,
    @Json(name = "sc")
    val sc: String,
    @Json(name = "spd")
    val spd: String
)

@JsonClass(generateAdapter = true)
data class CondXX(
    @Json(name = "code")
    val code: String,
    @Json(name = "txt")
    val txt: String
)

@JsonClass(generateAdapter = true)
data class WindXX(
    @Json(name = "deg")
    val deg: String,
    @Json(name = "dir")
    val dir: String,
    @Json(name = "sc")
    val sc: String,
    @Json(name = "spd")
    val spd: String
)

@JsonClass(generateAdapter = true)
data class Air(
    @Json(name = "brf")
    val brf: String,
    @Json(name = "txt")
    val txt: String
)

@JsonClass(generateAdapter = true)
data class Comf(
    @Json(name = "brf")
    val brf: String,
    @Json(name = "txt")
    val txt: String
)

@JsonClass(generateAdapter = true)
data class Cw(
    @Json(name = "brf")
    val brf: String,
    @Json(name = "txt")
    val txt: String
)

@JsonClass(generateAdapter = true)
data class Drsg(
    @Json(name = "brf")
    val brf: String,
    @Json(name = "txt")
    val txt: String
)

@JsonClass(generateAdapter = true)
data class Flu(
    @Json(name = "brf")
    val brf: String,
    @Json(name = "txt")
    val txt: String
)

@JsonClass(generateAdapter = true)
data class Sport(
    @Json(name = "brf")
    val brf: String,
    @Json(name = "txt")
    val txt: String
)

@JsonClass(generateAdapter = true)
data class Trav(
    @Json(name = "brf")
    val brf: String,
    @Json(name = "txt")
    val txt: String
)

@JsonClass(generateAdapter = true)
data class Uv(
    @Json(name = "brf")
    val brf: String,
    @Json(name = "txt")
    val txt: String
)