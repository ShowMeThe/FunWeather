package com.show.weather.ui.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.databinding.ObservableArrayList
import com.show.kcore.adapter.DataBindBaseAdapter
import com.show.weather.R
import com.show.weather.databinding.ItemQualityBinding

class QualityAdapter(context: Context, data: ObservableArrayList<QualityItem>) :
    DataBindBaseAdapter<QualityItem, ItemQualityBinding>(context, data) {
    override fun getItemLayout(): Int = R.layout.item_quality

    override fun bindItems(binding: ItemQualityBinding?, item: QualityItem, position: Int) {
        binding?.apply {
            bean = item
            executePendingBindings()
        }
    }


}


class QualityItem(val data: String, val logo: Int,val logoTitle:String)