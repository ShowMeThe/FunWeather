package com.show.weather.location

import android.util.ArrayMap
import android.util.Log
import androidx.lifecycle.*
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.show.kcore.base.AppContext
import com.show.kcore.extras.log.Logger
import com.show.kcore.rden.Stores
import com.show.weather.const.StoreConstant

class Location {

    companion object {
        private val instant by lazy { Location() }
        fun get() = instant
        private const val TAG = "Location"
    }

    private var isInit = false
    private lateinit var client: AMapLocationClient
    private lateinit var option: AMapLocationClientOption


    private fun init() {
        client = AMapLocationClient(AppContext.get().context.applicationContext)
        option = AMapLocationClientOption()
        option.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.Sport
        option.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        option.isGpsFirst = false
        option.isLocationCacheEnable = true
        option.isNeedAddress = true
        option.isOnceLocation = true
        client.setLocationOption(option)
        client.setLocationListener(listener)
        isInit = true
    }


    private val listener = AMapLocationListener { location ->
        if (location?.errorCode == 0) {
            client.stopLocation()
            Stores.put(StoreConstant.REQUEST_LOCATION_ADDRESS, location.city)
        }
        Logger.dLog(TAG, "stopLocation and result is OK")
        val it = onChange.iterator()
        while (it.hasNext()) {
            val change = it.next()
            val key = change.key
            val value = change.value
            if (key.lifecycleOwner?.lifecycle?.currentState != Lifecycle.State.DESTROYED) {
                value?.invoke(location)
            } else {
                it.remove()
            }
        }
        singleOnChange?.invoke(location)
    }

    private var singleOnChange: ((location: AMapLocation?) -> Unit)? = null
    fun getFinalLocation(onChange: ((location: AMapLocation?) -> Unit)? = null) {
        if (!isInit) {
            init()
        }
        client.startLocation()
        singleOnChange = onChange
    }


    private val onChange: ArrayMap<LifeWrapper, ((location: AMapLocation?) -> Unit)> = ArrayMap()
    fun getFinalLocation(
        lifecycleOwner: LifecycleOwner,
        onChange: ((location: AMapLocation?) -> Unit)? = null
    ) {
        if (!isInit) {
            init()
        }
        client.startLocation()
        Logger.dLog(TAG, "startLocation")
        this.onChange[LifeWrapper(lifecycleOwner)] = onChange
    }

    private inner class LifeWrapper(var lifecycleOwner: LifecycleOwner?) {

        init {
            lifecycleOwner?.lifecycle?.addObserver(LifecycleEventObserver{ ource, event->
                if(event == Lifecycle.Event.ON_DESTROY){
                    onChange.remove(this@LifeWrapper)
                    Log.e("222222","ON_DESTROY ${this}")
                }
            })
        }
    }


}