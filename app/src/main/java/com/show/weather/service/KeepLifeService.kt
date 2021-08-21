package com.show.weather.service

import android.content.Intent
import androidx.lifecycle.LifecycleService

/**
 *  com.show.weather.service
 *  2021/8/21
 *  10:26
 *  ShowMeThe
 */
class KeepLifeService : LifecycleService() {

    override fun onCreate() {
        super.onCreate()

    }


    override fun onDestroy() {
        super.onDestroy()
        kotlin.runCatching {
            val intent = Intent(applicationContext,AlarmService::class.java)
            applicationContext.startService(intent)
        }
    }
}