package com.show.weather.widget.provider

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.RemoteViews
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.show.kInject.core.ext.single
import com.show.kcore.extras.log.Logger
import com.show.kcore.http.coroutines.Coroutines
import com.show.kcore.http.coroutines.KResult
import com.show.kcore.http.coroutines.SuccessResult
import com.show.kcore.http.coroutines.callResult
import com.show.kcore.rden.Stores
import com.show.weather.R
import com.show.weather.api.Main
import com.show.weather.const.StoreConstant
import com.show.weather.entity.WeatherQuality
import com.show.weather.ui.SplashActivity
import com.show.weather.utils.WorkJob
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext


interface WeatherWidgetClientImp {

    fun onEnabled(context: Context?)

    fun onDeleted(context: Context?, appWidgetIds: IntArray?)

    fun onDisabled(context: Context?)

    fun onUpdate(context: Context, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray)

}

class WeatherWidgetClient : WeatherWidgetClientImp, LifecycleOwner {

    companion object {
        private const val TAG = "WeatherWidgetClient"
        private val singleInstant by lazy { WeatherWidgetClient() }
        fun getClient() = singleInstant

        const val ACTION_REFRESH = "WeatherWidget.REFRESH"
    }

    private val registry by lazy { LifecycleRegistry(this) }
    private val main: Main by single()


    private val scope = WidgetCoroutineScope()
    private val calendar by lazy { Calendar.getInstance(Locale.getDefault()) }


    private var receiver: TimeReceiver? = null
    private var refreshReceiver: RefreshReceiver? = null
    private val appWidgetIdList = ArrayList<Int>()

    private val responseFlow by lazy {
        MutableSharedFlow<KResult<WeatherQuality>>(replay = 1).apply {
            Stores.getObject<WeatherQuality>(StoreConstant.REQUEST_WEATHER, null)?.also { it ->
                Logger.dLog(TAG, "tryEmit")
                tryEmit(SuccessResult.create(it))
            }

        }
    }

    private val updateTime by lazy {
        MutableSharedFlow<Long>(replay = 1).apply {
            tryEmit(System.currentTimeMillis())
        }
    }

    override fun onEnabled(context: Context?) {

    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        appWidgetIds?.forEach {
            appWidgetIdList.remove(it)
        }
        if(appWidgetIdList.isEmpty()){
            Logger.dLog(TAG,"close all job")
            kotlin.runCatching {
                registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                scope.cancel()
                WorkJob.getManager().cancel()
                if (receiver != null) {
                    context?.applicationContext?.unregisterReceiver(receiver)
                    receiver = null
                }
                if (refreshReceiver != null) {
                    context?.applicationContext?.unregisterReceiver(refreshReceiver)
                    refreshReceiver = null
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    override fun onDisabled(context: Context?) {}


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray
    ) {
        Logger.dLog(TAG, "onUpdate appWidgetIds = ${appWidgetIds[0]}")
        registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        appWidgetIdList.add(appWidgetIds[0])

        if (receiver == null) {
            receiver = TimeReceiver()
            context.applicationContext
                .registerReceiver(receiver, IntentFilter().apply {
                    addAction(Intent.ACTION_TIME_TICK)
                })
            refreshReceiver = RefreshReceiver()
            context.applicationContext
                .registerReceiver(refreshReceiver, IntentFilter().apply {
                    addAction(ACTION_REFRESH)
                })
            WorkJob.getManager().runJob()
        }


        val views = RemoteViews(context.packageName, R.layout.weather_layout)
        val pendingIntent = PendingIntent.getActivity(
            context, 1000,
            Intent(context, SplashActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.mainContainer, pendingIntent)
        views.setTextViewText(R.id.tvWeather, "")

        scope.launch(Dispatchers.Main.immediate) {
            updateTime.collect {
                Logger.dLog(TAG, "Update Time")

                calendar.timeInMillis = it
                val month = calendar[Calendar.MONTH] + 1
                val day = calendar[Calendar.DAY_OF_MONTH]
                val dayInWeak = when (calendar[Calendar.DAY_OF_WEEK]) {
                    1 -> "周日"
                    2 -> "周一"
                    3 -> "周二"
                    4 -> "周三"
                    5 -> "周四"
                    6 -> "周五"
                    7 -> "周六"
                    else -> "周日"
                }
                views.setTextViewText(R.id.tvDate, "${month}月${day}日,${dayInWeak}")
                appWidgetManager?.updateAppWidget(appWidgetIds, views)
            }
        }

        scope.launch(Dispatchers.Main) {
            responseFlow
                .collect {
                    it.response?.apply {
                        Logger.dLog(TAG, "onUpdate getLive = ${this.hashCode()}")

                        val now = this.result.heWeather5[0].now
                        views.setTextViewText(R.id.tvWeather, "${now.cond.txt},${now.tmp}°C")


                        appWidgetManager?.updateAppWidget(appWidgetIds, views)
                    }
                }
        }
    }

    override fun getLifecycle(): Lifecycle = registry


    private class WidgetCoroutineScope : CoroutineScope {
        @ObsoleteCoroutinesApi
        override val coroutineContext: CoroutineContext
            get() = SupervisorJob() + newFixedThreadPoolContext(2, "Weather")

    }


    private fun requestLocation() {
        getWeather(Stores.getString(StoreConstant.REQUEST_LOCATION_ADDRESS, "北京") ?: "北京")
    }

    private fun getWeather(city: String) {
        val closeable = Coroutines(scope)
        closeable.callResult {
            hold(responseFlow) { main.getWeatherQuality(city) }
                .success {
                    Logger.dLog(TAG, "getWeather success")
                    Stores.putObject(StoreConstant.REQUEST_WEATHER, this.response)
                }
        }
    }


    inner class TimeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == Intent.ACTION_TIME_TICK) {
                updateTime.tryEmit(System.currentTimeMillis())
            }
        }
    }

    inner class RefreshReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == ACTION_REFRESH) {
                Logger.dLog(TAG, "RefreshReceiver = ${intent.action}")
                requestLocation()
            }
        }
    }


}