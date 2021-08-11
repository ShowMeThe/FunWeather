package com.show.weather.widget.provider

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_TIME_TICK
import android.content.IntentFilter
import android.util.Log
import android.widget.RemoteViews
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
import com.show.weather.location.Location
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import java.io.Closeable
import java.util.*
import kotlin.coroutines.CoroutineContext

class WeatherWidget : AppWidgetProvider() {

    companion object {
        private const val TAG = "WeatherWidget"
    }

    private val main: Main by single()

    private val result = MutableSharedFlow<KResult<WeatherQuality>>(replay = 1).apply {
        Stores.getObject<WeatherQuality>(StoreConstant.REQUEST_WEATHER, null)?.also {
            tryEmit(SuccessResult.create(it))
        }
    }

    private val scope = WidgetCoroutineScope()

    private val calendar by lazy { Calendar.getInstance(Locale.getDefault()) }

    private val updateTime by lazy {
        MutableSharedFlow<Long>(replay = 1).apply {
            tryEmit(System.currentTimeMillis())
        }
    }

    private var receiver: TimeReceiver? = null

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        kotlin.runCatching {
            scope.cancel()
        }.onFailure {
            it.printStackTrace()
        }
    }


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        if (receiver == null) {
            receiver = TimeReceiver()
            context.applicationContext
                .registerReceiver(receiver, IntentFilter().apply {
                    addAction(ACTION_TIME_TICK)
                })
        }

        val views = RemoteViews(context.packageName, R.layout.weather_layout)
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
            result.collect {
                it.response?.apply {
                    val now = this.result.heWeather5[0].now
                    views.setTextViewText(R.id.tvWeather, "${now.cond.txt},${now.tmp}°C")


                    appWidgetManager?.updateAppWidget(appWidgetIds, views)
                }
            }
        }

        scope.launch(Dispatchers.Main) {
            requestLocation()
        }
    }

    private fun requestLocation() {
        Location.get().getFinalLocation {
            it?.apply {
                getWeather(city ?: "北京")
            }
        }
    }

    private fun getWeather(city: String) {
        val closeable = Coroutines(scope)
        closeable.callResult {
            hold(result) { main.getWeatherQuality(city) }
                .success {
                    Stores.putObject(StoreConstant.REQUEST_WEATHER, this.response)
                }
        }
    }


    inner class TimeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == ACTION_TIME_TICK) {
                updateTime.tryEmit(System.currentTimeMillis())
            }
        }
    }

    private class WidgetCoroutineScope : CoroutineScope {

        @ObsoleteCoroutinesApi
        override val coroutineContext: CoroutineContext
            get() = SupervisorJob() + newFixedThreadPoolContext(2, "Weather")

    }

}