package com.show.weather.ui.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.show.kcore.http.coroutines.KResult
import com.show.kcore.http.coroutines.SuccessResult
import com.show.kcore.rden.Stores
import com.show.weather.const.StoreConstant
import com.show.weather.entity.WeatherQuality
import com.show.weather.ui.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {


    private val repository by lazy { WeatherRepository(this) }

    val weatherQuality =  MutableSharedFlow<KResult<WeatherQuality>>(replay = 1).apply {
        viewModelScope.launch(Dispatchers.IO) {
            Stores.getObject<WeatherQuality>(StoreConstant.REQUEST_WEATHER,null)?.also{
                emit(SuccessResult.create(it))
            }
        }
    }


    fun getWeatherQuality(adCode:String){
        repository.getWeatherQuality(adCode,weatherQuality)
    }

}