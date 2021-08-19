package com.show.weather.utils

import android.content.Context
import android.content.Intent
import androidx.work.*
import com.show.kInject.core.ext.androidContextNotNull
import com.show.kcore.base.AppContext
import com.show.kcore.extras.log.Logger
import com.show.weather.widget.provider.WeatherWidget
import java.util.concurrent.TimeUnit

class WorkJob {

    companion object {
        private val job by lazy { WorkJob() }
        fun getManager() = job
    }

    fun runJob() {
        val periodicWorkRequest = PeriodicWorkRequestBuilder<BackJobWork>(30, TimeUnit.MINUTES)
            .setConstraints(Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build())
            .build()
        WorkManager.getInstance(androidContextNotNull())
            .enqueueUniquePeriodicWork(
                "WorkJob",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )
    }

    fun cancel() {
        WorkManager.getInstance(AppContext.getContext())
            .cancelAllWork()
    }

}


class BackJobWork(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        val intent = Intent()
        intent.action = WeatherWidget.ACTION_REFRESH
        applicationContext.sendBroadcast(intent)
        Logger.dLog("WorkJob","doWork")
        return Result.success()
    }

}