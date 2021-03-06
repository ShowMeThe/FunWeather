package com.show.weather.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.show.kcore.adapter.DataBindBaseAdapter
import com.show.kcore.extras.display.dp
import com.show.weather.R
import com.show.weather.databinding.ItemForecastBinding
import com.show.weather.entity.DailyForecast
import com.show.weather.entity.ForecastItem

class WeatherForecastView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {


    private val lineView by lazy { WeatherForecastLineView(context) }
    private val rvView by lazy { RecyclerView(context).apply {
        overScrollMode = OVER_SCROLL_NEVER
    } }
    private val forecasts = ObservableArrayList<ForecastItem>()
    private val layoutManager by lazy { GridLayoutManager(context, 7) }
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
                topMargin = 20f.dp.toInt()
            }, false
        )
        addViewInLayout(
            lineView,
            -1,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT), false
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

    fun updateTemp(list : List<DailyForecast>){
        val up = ArrayList<Int>()
        val low = ArrayList<Int>()
        list.forEach {
            up.add(it.tmp.max.toInt())
            low.add(it.tmp.min.toInt())
        }
        lineView.updateData(up,low)
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