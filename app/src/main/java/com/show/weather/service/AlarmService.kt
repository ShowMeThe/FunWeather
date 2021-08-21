package com.show.weather.service

import android.content.Intent
import androidx.lifecycle.LifecycleService

/**
 *  com.show.weather.service
 *  2021/8/21
 *  10:24
 *  ShowMeThe
 */
class AlarmService : LifecycleService() {

    override fun onCreate() {
        super.onCreate()
        kotlin.runCatching {
            val intent = Intent(applicationContext,KeepLifeService::class.java)
            applicationContext.startService(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        kotlin.runCatching {
            val intent = Intent(applicationContext,KeepLifeService::class.java)
            applicationContext.startService(intent)
        }
    }
}