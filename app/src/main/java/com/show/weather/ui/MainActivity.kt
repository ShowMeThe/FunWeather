package com.show.weather.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.show.kclock.MILLIS_DAY
import com.show.kcore.adapter.divider.RecycleViewDivider
import com.show.kcore.base.BaseActivity
import com.show.kcore.base.Transition
import com.show.kcore.extras.display.dp
import com.show.kcore.extras.gobal.collect
import com.show.kcore.extras.gobal.gone
import com.show.kcore.extras.gobal.visible
import com.show.kcore.extras.status.statusBar
import com.show.kcore.extras.string.builder
import com.show.kcore.rden.Stores
import com.show.permission.PermissionFactory
import com.show.weather.R
import com.show.weather.const.StoreConstant
import com.show.weather.databinding.ActivityMainBinding
import com.show.weather.entity.*
import com.show.weather.location.Location
import com.show.weather.service.AlarmService
import com.show.weather.ui.adapter.QualityAdapter
import com.show.weather.ui.adapter.QualityItem
import com.show.weather.ui.vm.MainViewModel
import com.show.weather.utils.WeatherFilter
import kotlin.math.absoluteValue

@Transition
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private  val list = ObservableArrayList<QualityItem>()

    override fun getViewId(): Int = R.layout.activity_main

    override fun onBundle(bundle: Bundle) {

    }

    override fun observerUI() {

        viewModel.weatherQuality
            .collect(this) {
                it.response?.apply {
                    val weather5 = this.result.heWeather5[0]
                    initView(weather5.now, weather5.basic.update.loc)
                    initData(weather5.dailyForecast)
                    initSun(weather5.dailyForecast[0].astro)
                    initQuality(this)
                }
            }

    }

    private fun initSun(astro: Astro) {
        binding {
            sun.updateSunRise(astro.sr, astro.ss, "????????????", R.drawable.sun)
            moon.updateSunRise(astro.mr, astro.ms, "????????????", R.drawable.moon)
        }
    }

    private fun initQuality(quality: WeatherQuality) {
        list.clear()
        val city = quality.result.heWeather5[0].aqi.city
        list.add(QualityItem("${city.pm25}??g/m^3", R.drawable.ic_pm2_5, "PM2.5"))
        list.add(QualityItem("${city.pm10}??g/m^3", R.drawable.ic_pm1_0, "PM1.0"))
        list.add(QualityItem(city.qlty, R.drawable.ic_air_qu, "????????????"))
        list.add(QualityItem("${city.o3}??g/m^3", R.drawable.ic_o3, "O3"))
        list.add(QualityItem("${city.co}mg/m^3", R.drawable.ic_co, "CO"))
        list.add(QualityItem("${city.so2}??g/m^3", R.drawable.ic_so2, "SO2"))
    }

    override fun init(savedInstanceState: Bundle?) {
        statusBar {
            uiFullScreen()
        }
        binding.refresh.setColorSchemeResources(R.color.colorPrimary)
        requestPermissionWhenNeed()
        kotlin.runCatching {
            startService(Intent(this, AlarmService::class.java))
        }.onFailure { it.printStackTrace() }

    }

    /**
     * ??????????????????
     */
    private fun requestPermissionWhenNeed() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
        val requestTime = Stores.getLong(StoreConstant.REQUEST_LOCATION, 0L)
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && System.currentTimeMillis() - requestTime >= 1 * MILLIS_DAY
        ) {
            Stores.put(StoreConstant.REQUEST_LOCATION, System.currentTimeMillis())
            PermissionFactory.with(this)
                .requestAll(*permissions) { it, list ->
                    if (it) {
                        requestLocation()
                    } else {
                        val result = PermissionFactory.checkPermissionIsAlwaysFalse(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                        if (result.isNotEmpty()) {
                            /**
                             * ????????????
                             */
                            Toast.makeText(this, "???????????????????????????????????????", Toast.LENGTH_SHORT).show()
                        } else {
                            /**
                             * toast
                             */
                            Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        } else {
            requestLocation()
        }
    }

    private fun requestLocation() {
        Location.get().getFinalLocation(this) {
            binding {
                val address = it?.city.builder?.append("-${it?.district}") ?: "????????????"
                tvAddress.text = address
            }
            it?.apply {
                requestWeather(city ?: "??????")
            }
        }
    }

    private fun requestWeather(it: String) {
        viewModel.getWeatherQuality(it)
    }

    private fun initView(weather: Now, reportTime: String) {
        binding {
            refresh.isRefreshing = false

            tvWeather.text = weather.cond.txt
            tvHi.text = weather.hum
            tvWind.text = "${weather.wind.spd} -- ${weather.wind.dir}"
            tvTemp.text = weather.tmp.builder?.append("??C")

            if (WeatherFilter.getWeatherRain(weather.cond.txt)) {
                rainView.visible()
            } else {
                rainView.gone()
            }

            binding.weather.apply {
                updateReportTime(reportTime)
                resetIcons(*WeatherFilter.getWeatherByName(weather.cond.txt))
            }


            val adapter = QualityAdapter(this@MainActivity, list)
            binding {
                rvQuality.adapter = adapter
                rvQuality.layoutManager = GridLayoutManager(this@MainActivity, 2)
                if(rvQuality.itemDecorationCount > 0){
                    rvQuality.removeItemDecorationAt(0)
                }
                rvQuality.addItemDecoration(
                    RecycleViewDivider(
                        RecyclerView.VERTICAL,
                        dividerColor = ContextCompat.getColor(this@MainActivity, R.color.colorPrimary),
                        dividerHeight = 0.5f.dp.toInt(), padding = 15f.dp
                    )
                )
            }
        }
    }

    private fun initData(forecast: List<DailyForecast>) {
        binding {
            binding.forecast.updateForecast(forecast.map {
                it.toForecastItem()
            })
            binding.forecast.updateTemp(forecast)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        binding {

            appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                refresh.isEnabled = verticalOffset >= 0
            })


            refresh.setOnRefreshListener {
                requestWeather(Stores.getString(StoreConstant.REQUEST_LOCATION_ADDRESS, "") ?: "??????")
            }

          /*  coordinator.setOnTouchListener { v, event ->

                if(event.action == MotionEvent.ACTION_MOVE){
                    maskQuality.startAnimation()
                }else if(event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL){
                    maskQuality.stopAnimation()

                }
                false
            }


            nest.setOnTouchListener { v, event ->

                if(event.action == MotionEvent.ACTION_MOVE){
                    maskQuality.startAnimation()
                }else if(event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL){
                    maskQuality.stopAnimation()
                }
                false
            }

            rvQuality.setOnTouchListener { v, event ->

                if(event.action == MotionEvent.ACTION_MOVE){
                    maskQuality.startAnimation()
                }else if(event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL){
                    maskQuality.stopAnimation()
                }
                false
            }*/

        }


    }
}