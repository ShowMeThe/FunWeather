package com.show.weather.app

import android.app.Application
import android.content.Context
import androidx.annotation.Keep
import androidx.work.Configuration
import androidx.work.WorkManager
import com.show.kInject.core.initScope
import com.show.kcore.extras.display.dp
import com.show.kcore.http.Http
import com.show.kcore.http.http
import com.show.launch.Initializer
import com.show.slideback.SlideRegister
import com.show.weather.api.Main
import com.show.weather.utils.WorkJob
import kotlinx.coroutines.CancellableContinuation


@Keep
class MainInitializer : Initializer<Boolean> {

    override fun onCreate(
        context: Context,
        isMainProcess: Boolean,
        continuation: CancellableContinuation<Boolean>?
    ) {
        SlideRegister.config {
            shadowWidth = 30f.dp.toInt()
            maxSideLength = 25f.dp
        }

        context.http{
            config {
                baseUrl = "https://way.jd.com/"
            }
        }
        initScope {
            androidContext(context as Application)
            single { Http.createApi(Main::class.java) }
        }
        WorkManager.initialize(context, Configuration.Builder().build())
    }
}