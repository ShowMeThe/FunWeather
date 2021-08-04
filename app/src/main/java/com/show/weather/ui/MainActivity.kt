package com.show.weather.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.lifecycle.asLiveData
import com.show.kclock.MILLIS_DAY
import com.show.kcore.base.BaseActivity
import com.show.kcore.base.Transition
import com.show.kcore.extras.gobal.read
import com.show.kcore.extras.status.statusBar
import com.show.kcore.extras.string.builder
import com.show.kcore.rden.Stores
import com.show.permission.PermissionFactory
import com.show.weather.R
import com.show.weather.const.StoreConstant
import com.show.weather.databinding.ActivityMainBinding
import com.show.weather.entity.Weather
import com.show.weather.entity.WeatherForecast
import com.show.weather.entity.toForecastItem
import com.show.weather.location.Location
import com.show.weather.ui.vm.MainViewModel
import com.show.weather.utils.WeatherFilter

@Transition
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {


    override fun getViewId(): Int = R.layout.activity_main

    override fun onBundle(bundle: Bundle) {

    }

    override fun observerUI() {

        viewModel.weather.asLiveData()
            .read(this){
                it?.apply {
                    initView(this)
                }
            }

        viewModel.weatherForecast.asLiveData()
            .read(this) {
                it?.apply {
                    initData(this)
                }
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
                        } else {
                            /**
                             * toast
                             */
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
            val adcode = it?.adCode ?: "110101"
            requestWeather(adcode)
        }
    }

    private fun requestWeather(adcode: String) {
        viewModel.getNowWeather(adcode)
        viewModel.getForecastWeather(adcode)
    }

    private fun initView(weather: Weather) {
        binding {
            refresh.isRefreshing = false

            val today = weather.lives[0]
            today.apply {
                tvWeather.text = this.weather
                tvHi.text = humidity
                tvWind.text = "$winddirection -- ${windpower}"
                tvTemp.text = temperature.builder?.append("°C")
            }

            binding.weather.apply {
                updateReportTime(today.reporttime)
                resetIcons(*WeatherFilter.getWeatherByName(today.weather))
            }
        }
    }

    private fun initData(forecast: WeatherForecast) {
        binding {

            val casts = forecast.forecasts[0].casts
            binding.forecast.updateForecast(casts.map {
                it.toForecastItem()
            })
            binding.forecast.updateTemp(casts)
        }
    }

    override fun initListener() {

        binding {

            refresh.setOnRefreshListener {
                requestWeather(
                    Stores.getString(StoreConstant.REQUEST_LOCATION_ADDRESS, "110101") ?: "110101"
                )
            }


        }


    }
}