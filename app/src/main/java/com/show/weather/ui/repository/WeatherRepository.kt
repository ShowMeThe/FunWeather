package com.show.weather.ui.repository

import android.util.Log
import androidx.lifecycle.ViewModel
import com.show.kInject.core.ext.single
import com.show.kcore.base.BaseRepository
import com.show.kcore.http.clazzToJson
import com.show.kcore.http.clazzToJsonValue
import com.show.kcore.http.coroutines.KResult
import com.show.kcore.http.coroutines.callResult
import com.show.kcore.rden.Stores
import com.show.weather.api.Main
import com.show.weather.const.StoreConstant.REQUEST_WEATHER
import com.show.weather.entity.WeatherQuality
import kotlinx.coroutines.flow.MutableSharedFlow

class WeatherRepository(viewModel: ViewModel?) : BaseRepository(viewModel) {

    private val  main : Main by single()

    fun getWeatherQuality(adcode:String,call: MutableSharedFlow<KResult<WeatherQuality>>){
        androidScope {
            callResult {
                hold(call) {  main.getWeatherQuality(adcode) }
                    .success {
                        Stores.putObject(REQUEST_WEATHER,this.response)
                    }
            }
        }
    }

}