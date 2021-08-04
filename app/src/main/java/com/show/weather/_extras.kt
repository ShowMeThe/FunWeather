package com.show.weather

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("loadDrawable")
fun ImageView.loadDrawable(drawable: Drawable?){
    setImageDrawable(drawable)
}