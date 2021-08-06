package com.show.weather.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebHistoryItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.location.AMapLocation
import com.google.android.material.appbar.AppBarLayout
import com.show.kclock.MILLIS_DAY
import com.show.kcore.adapter.divider.RecycleViewDivider
import com.show.kcore.base.BaseActivity
import com.show.kcore.base.Transition
import com.show.kcore.extras.display.dp
import com.show.kcore.extras.gobal.gone
import com.show.kcore.extras.gobal.read
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
import com.show.weather.ui.adapter.QualityAdapter
import com.show.weather.ui.adapter.QualityItem
import com.show.weather.ui.vm.MainViewModel
import com.show.weather.utils.WeatherFilter

@Transition
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {


    override fun getViewId(): Int = R.layout.activity_main

    override fun onBundle(bundle: Bundle) {

    }

    override fun observerUI() {

        viewModel.weatherQuality.asLiveData()
            .read(this) {
                it?.apply {
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
            sun.updateSunRise(astro.sr,astro.ss,"日出时间",R.drawable.sun)
            moon.updateSunRise(astro.mr,astro.ms,"月出时间",R.drawable.moon)
        }
    }

    private fun initQuality(quality: WeatherQuality) {
        val list = ObservableArrayList<QualityItem>()
        val city = quality.result.heWeather5[0].aqi.city
        list.add(QualityItem("${city.pm25}μg/m^3", R.drawable.ic_pm2_5, "PM2.5"))
        list.add(QualityItem("${city.pm10}μg/m^3", R.drawable.ic_pm1_0, "PM1.0"))
        list.add(QualityItem(city.qlty, R.drawable.ic_air_qu, "空气质量"))
        list.add(QualityItem("${city.o3}μg/m^3", R.drawable.ic_o3, "O3"))
        list.add(QualityItem("${city.co}mg/m^3", R.drawable.ic_co, "CO"))
        list.add(QualityItem("${city.so2}μg/m^3", R.drawable.ic_so2, "SO2"))
        val adapter = QualityAdapter(this@MainActivity, list)
        binding {
            rvQuality.adapter = adapter
            rvQuality.layoutManager = GridLayoutManager(this@MainActivity, 2)
            rvQuality.addItemDecoration(
                RecycleViewDivider(
                    RecyclerView.VERTICAL,
                    dividerColor = ContextCompat.getColor(this@MainActivity, R.color.colorPrimary),
                    dividerHeight = 0.5f.dp.toInt(), padding = 15f.dp
                )
            )
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        statusBar {
            uiFullScreen()
        }
        binding.refresh.setColorSchemeResources(R.color.colorPrimary)
        requestPermissionWhenNeed()
    }

    /**
     * 请求定位权限
     */
    private fun requestPermissionWhenNeed() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
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
                             * 跳转系统
                             */
                            Toast.makeText(this, "跳转系统界面，打开定位权限", Toast.LENGTH_SHORT).show()
                        } else {
                            /**
                             * toast
                             */
                            Toast.makeText(this, "无法获取定位", Toast.LENGTH_SHORT).show()
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
                val address = it?.city.builder?.append("-${it?.district}") ?: "无法获取"
                tvAddress.text = address
            }
            it?.apply {
                requestWeather(city ?: "北京")
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
            tvTemp.text = weather.tmp.builder?.append("°C")

            if(WeatherFilter.getWeatherRain(weather.cond.txt)){
                rainView.visible()
            }else{
                rainView.gone()
            }

            binding.weather.apply {
                updateReportTime(reportTime)
                resetIcons(*WeatherFilter.getWeatherByName(weather.cond.txt))
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

    override fun initListener() {
        binding {
            appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                refresh.isEnabled = verticalOffset >= 0
            })


            refresh.setOnRefreshListener {
                requestWeather(Stores.getString(StoreConstant.REQUEST_LOCATION_ADDRESS, "") ?: "北京")
            }


        }


    }
}