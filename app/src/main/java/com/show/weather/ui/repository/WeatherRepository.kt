package com.show.weather.ui.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.show.kInject.core.ext.single
import com.show.kcore.base.BaseRepository
import com.show.kcore.http.coroutines.KResult
import com.show.kcore.http.coroutines.callResult
import com.show.weather.api.Main
import com.show.weather.entity.Weather
import com.show.weather.entity.WeatherForecast
import com.show.weather.entity.WeatherQuality
import kotlinx.coroutines.flow.MutableSharedFlow

class WeatherRepository(viewModel: ViewModel?) : BaseRepository(viewModel) {

    private val  main : Main by single()

    fun getForecastWeather(adcode:String,call: MutableSharedFlow<KResult<WeatherForecast>>){
        androidScope {
            callResult {
                hold(call) {  main.getForecastWeather("7d3e011abe9f333691a3e9bc88829c83",adcode) }
            }
        }
    }

    fun getNowWeather(adcode:String,call: MutableSharedFlow<KResult<Weather>>){
        androidScope {
            callResult {
                hold(call) {  main.getNowWeather("7d3e011abe9f333691a3e9bc88829c83",adcode) }
            }
        }
    }

    fun getWeatherQuality(adcode:String,call: MutableSharedFlow<KResult<WeatherQuality>>){
        androidScope {
            callResult {
                hold(call) {  main.getWeatherQuality(adcode) }
            }
        }
    }

}