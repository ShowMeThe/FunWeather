package com.show.weather.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.show.kcore.http.coroutines.KResult
import com.show.weather.entity.Weather
import com.show.weather.entity.WeatherForecast
import com.show.weather.ui.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableSharedFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {


    private val repository by lazy { WeatherRepository(this) }

    val weather =  MutableSharedFlow<KResult<Weather>>()
    val weatherForecast =  MutableSharedFlow<KResult<WeatherForecast>>()

    fun getNowWeather(adCode:String){
        repository.getNowWeather(adCode,weather)
    }

    fun getForecastWeather(adCode:String){
        repository.getForecastWeather(adCode,weatherForecast)
    }

}