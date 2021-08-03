package com.show.weather.location

import android.util.ArrayMap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.show.kcore.base.AppContext
import com.show.kcore.rden.Stores
import com.show.weather.const.StoreConstant

class Location {

    companion object {
        private val instant by lazy { Location() }
        fun get() = instant
    }

    private var isInit = false
    private lateinit var client: AMapLocationClient
    private lateinit var option: AMapLocationClientOption


    private fun init() {
        client = AMapLocationClient(AppContext.get().context.applicationContext)
        option = AMapLocationClientOption()
        option.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.Sport
        option.locationMode = AMapLocationClientOption.AMapLocationMode.Battery_Saving
        option.isGpsFirst = true
        option.isLocationCacheEnable = true
        option.isNeedAddress = true
        option.isOnceLocation = true
        client.setLocationOption(option)
        client.setLocationListener(listener)
        isInit = true
    }


    private val listener = AMapLocationListener { location ->
        if(location?.errorCode == 0){
            client.stopLocation()
            Stores.put(StoreConstant.REQUEST_LOCATION_ADDRESS,location.adCode)
        }
        onChange.forEach {
            val key = it.key
            val value = it.value
            if (key.lifecycleOwner?.lifecycle?.currentState != Lifecycle.State.DESTROYED) {
                value?.invoke(location)
            }
        }

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
        this.onChange[LifeWrapper(lifecycleOwner)] = onChange
    }

    private inner class LifeWrapper(var lifecycleOwner: LifecycleOwner?) : LifecycleObserver {

        init {
            lifecycleOwner?.lifecycle?.addObserver(this)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            lifecycleOwner?.lifecycle?.removeObserver(this)
            onChange.remove(this@LifeWrapper)
            lifecycleOwner = null
        }
    }


}