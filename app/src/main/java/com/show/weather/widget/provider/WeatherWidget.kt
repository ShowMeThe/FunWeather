package com.show.weather.widget.provider

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
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
import com.show.kclock.dateTime
import com.show.kclock.format
import com.show.kclock.yyyy_MM_dd_HHmmss
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
import com.show.weather.ui.MainActivity
import com.show.weather.ui.SplashActivity
import com.show.weather.utils.WorkJob
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import java.io.Closeable
import java.util.*
import kotlin.coroutines.CoroutineContext

class WeatherWidget : AppWidgetProvider() {

    companion object {
        private val client by lazy { WeatherWidgetClient.getClient() }
    }



    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        client.onEnabled(context)
    }


    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        client.onDeleted(context,appWidgetIds)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        client.onDisabled(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray
    ) {
        client.onUpdate(context, appWidgetManager, appWidgetIds)
    }

}