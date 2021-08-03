package com.show.weather.api

import com.show.weather.entity.Weather
import com.show.weather.entity.WeatherForecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Main {


    @GET("/v3/weather/weatherInfo?extensions=all")
    suspend fun getForecastWeather(@Query("key") key:String, @Query("city") city:String) : Response<WeatherForecast>

    @GET("/v3/weather/weatherInfo?extensions=base")
    suspend fun getNowWeather(@Query("key") key:String, @Query("city") city:String) : Response<Weather>

}