package com.show.weather.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.show.kInject.core.ext.androidContextNotNull
import com.show.kclock.MILLIS_MINUTE
import com.show.kcore.base.AppContext
import com.show.kcore.extras.log.Logger
import com.show.weather.widget.provider.WeatherWidget
import com.show.weather.widget.provider.WeatherWidgetClient.Companion.ACTION_REFRESH
import java.util.concurrent.TimeUnit

class WorkJob {

    companion object {
        private val job by lazy { WorkJob() }
        fun getManager() = job
    }
    private val alarmManager by lazy { androidContextNotNull().getSystemService(AlarmManager::class.java) }

    fun runNextJob() {
        Logger.dLog("WorkJob","runNextJob")
        val intent = Intent()
        intent.action = ACTION_REFRESH
        val pendingIntent = PendingIntent.getBroadcast(androidContextNotNull(),
            100,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        val nextTime = System.currentTimeMillis() + 15 * MILLIS_MINUTE
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,nextTime,pendingIntent)
    }

}
