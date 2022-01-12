package com.show.weather.widget.provider

import android.app.PendingIntent
import android.app.WallpaperManager
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.ArrayMap
import android.util.Log
import android.widget.RemoteViews
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.palette.graphics.Palette
import com.show.kInject.core.ext.androidContext
import com.show.kInject.core.ext.androidContextNotNull
import com.show.kInject.core.ext.single
import com.show.kclock.dateTime
import com.show.kclock.format
import com.show.kclock.yyyy_MM_dd_HHmmss
import com.show.kcore.extras.log.Logger
import com.show.kcore.http.coroutines.Coroutines
import com.show.kcore.http.coroutines.KResult
import com.show.kcore.http.coroutines.SuccessResult
import com.show.kcore.http.coroutines.callResult
import com.show.kcore.http.jsonToClazz
import com.show.kcore.rden.RoomBean
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
import okio.appendingSink
import okio.buffer
import java.io.File
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

    private var manager: WallpaperManager? = null
    private var receiver: TimeReceiver? = null
    private var refreshReceiver: RefreshReceiver? = null
    private val appWidgetIdList = ArrayList<Int>()

    private val localLiveData by lazy {
        Stores.getLive<WeatherQuality>(
            this,
            StoreConstant.REQUEST_WEATHER
        )
    }
    private val observers by lazy { ArrayMap<Int, Observer<in RoomBean?>>() }

    private val responseFlow by lazy {
        MutableSharedFlow<KResult<WeatherQuality>>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_LATEST
        )
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
            val observer = observers[it]
            if (observer != null) {
                localLiveData.removeObserver(observer)
            }
        }
        if (appWidgetIdList.isEmpty()) {
            Logger.dLog(TAG, "close all job")
            kotlin.runCatching {
                registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                scope.cancel()
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
            manager = WallpaperManager.getInstance(context)

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
            requestLocation()
            WorkJob.getManager().runNextJob()
        }


        val views = RemoteViews(context.packageName, R.layout.weather_layout)
        val pendingIntent = PendingIntent.getActivity(
            context, 1000,
            Intent(context, SplashActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.mainContainer, pendingIntent)
        views.setTextViewText(R.id.tvWeather, "")
        val color = findTextColor(manager?.drawable)
        views.setTextColor(R.id.tvWeather,color)
        views.setTextColor(R.id.tvDate,color)
        views.setTextColor(R.id.timer,color)

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

        val observer: Observer<in RoomBean?> = Observer<RoomBean?> {
            val data = it?.stringValue
            val out = data?.jsonToClazz<WeatherQuality>()
            out?.apply {
                Logger.dLog(TAG, "onUpdate getLive from DataBase = ${this.hashCode()}")

                val now = this.result.heWeather5[0].now
                views.setTextViewText(R.id.tvWeather, "${now.cond.txt},${now.tmp}°C")
                appWidgetManager?.updateAppWidget(appWidgetIds, views)
            }
        }
        localLiveData.observe(this, observer)
        observers[appWidgetIds[0]] = observer

        /*scope.launch(Dispatchers.Main.immediate) {
            responseFlow.collect {
                it.response?.apply {
                    Logger.dLog(TAG, "onUpdate data from Network = ${this.hashCode()}")

                    val now = this.result.heWeather5[0].now
                    views.setTextViewText(R.id.tvWeather, "${now.cond.txt},${now.tmp}°C")
                    appWidgetManager?.updateAppWidget(appWidgetIds, views)

                }
            }
        }*/
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
            hold { main.getWeatherQuality(city) }
                .success {
                    Stores.putObject(StoreConstant.REQUEST_WEATHER, this.response)
                    Logger.dLog(TAG, "getWeather success")
                }
        }
    }

    private fun writeLocalLog(result: Boolean) {
        kotlin.runCatching {
            val context = androidContextNotNull()
            val cacheDir = context.externalCacheDir!!.path + "/log"
            val dirFile = File(cacheDir)
            if (dirFile.exists().not()) {
                dirFile.mkdirs()
            }
            val cacheFile = File(dirFile.path + File.separator + "log.txt")
            if (cacheFile.exists().not()) {
                cacheFile.createNewFile()
            }
            val sink = cacheFile.appendingSink().buffer()
            val newLine = if (cacheFile.length() <= 0L) {
                ""
            } else {
                "\n"
            }
            val newStr =
                "${
                    System.currentTimeMillis()
                        .dateTime.format(yyyy_MM_dd_HHmmss)
                } result = $result $newLine"
            sink.write(newStr.encodeToByteArray())
            sink.flush()
            sink.close()
        }.onFailure {
            it.printStackTrace()
        }
    }

    private fun findTextColor(drawable: Drawable?): Int {
        if (drawable == null) return Color.WHITE
        val bitmap = (drawable as BitmapDrawable).bitmap
        return Palette.from(bitmap).generate().getLightVibrantColor(Color.WHITE)
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
                WorkJob.getManager().runNextJob()
            }
        }
    }


}