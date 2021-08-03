package com.show.weather.app

import android.content.Context
import androidx.annotation.Keep
import com.show.kInject.core.initScope
import com.show.kcore.extras.display.dp
import com.show.kcore.http.Http
import com.show.kcore.http.http
import com.show.launch.Initializer
import com.show.slideback.SlideRegister
import com.show.weather.api.Main
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
                baseUrl = "https://restapi.amap.com/"
            }
        }

        initScope {
            single { Http.createApi(Main::class.java) }
        }
    }
}