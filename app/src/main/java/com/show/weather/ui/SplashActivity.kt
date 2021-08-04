package com.show.weather.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.show.kcore.base.setUpTransition
import com.show.kcore.base.startActivity
import com.show.kcore.extras.status.statusBar
import com.show.weather.R
import kotlinx.coroutines.delay

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setUpTransition()

        statusBar {
            uiFullScreen(true)
        }

        lifecycleScope.launchWhenCreated {
            delay(1000)
            startActivity<MainActivity>(transition = false)
            finishAfterTransition()
        }


    }
}