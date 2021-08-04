package com.show.weather

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.show.kcore.extras.display.dp

@BindingAdapter("loadDrawable")
fun ImageView.loadDrawable(drawable: Drawable?){
    setImageDrawable(drawable)
}

@BindingAdapter("loadDrawable")
fun TextView.loadDrawable(int: Int){
    val drawable = ContextCompat.getDrawable(context,int)
    drawable?.setBounds(0,0, 25f.dp.toInt(), 25f.dp.toInt())
    setCompoundDrawables(drawable,null,null,null)
}