package com.show.weather.api

import com.show.weather.entity.WeatherQuality
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Main {


    @GET("/he/freeweather?appkey=81fe66eff019009278d16e15777d96f7")
    suspend fun getWeatherQuality(@Query("city") city:String) : Response<WeatherQuality>

}