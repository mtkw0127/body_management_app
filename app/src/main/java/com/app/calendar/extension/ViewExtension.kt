package com.app.calendar.extension

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("isExist")
fun View.setIsExist(isExist: Boolean) {
    visibility = if (isExist) View.VISIBLE else View.GONE
}