package com.show.weather.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.core.view.marginTop
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.show.kcore.adapter.DataBindBaseAdapter
import com.show.kcore.extras.display.dp
import com.show.weather.R
import com.show.weather.databinding.ItemForecastBinding
import com.show.weather.entity.ForecastItem

class WeatherForecastView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {


    private val rvView by lazy { RecyclerView(context) }
    private val forecasts = ObservableArrayList<ForecastItem>()
    private val layoutManager by lazy { GridLayoutManager(context, 4) }
    private val adapter by lazy { ForecastAdapter(context, forecasts) }


    init {
        initView()
    }

    private fun initView() {
        orientation = LinearLayout.VERTICAL
        addViewInLayout(
            rvView,
            -1,
            LayoutParams(LayoutParams.MATCH_PARENT, 100f.dp.toInt()).apply {
                topMargin = 30f.dp.toInt()
            }, false
        )
        initAdapter()
    }

    private fun initAdapter() {
        rvView.adapter = adapter
        rvView.layoutManager = layoutManager
    }


    fun updateForecast(list: List<ForecastItem>) {
        forecasts.clear()
        forecasts.addAll(list)
    }

}


class ForecastAdapter(context: Context, data: ObservableArrayList<ForecastItem>) :
    DataBindBaseAdapter<ForecastItem, ItemForecastBinding>(context, data) {
    override fun getItemLayout(): Int = R.layout.item_forecast

    override fun bindItems(binding: ItemForecastBinding?, item: ForecastItem, position: Int) {
        binding?.apply {
            bean = item
            executePendingBindings()

        }
    }
}