package com.app.body_manage.common

import java.text.SimpleDateFormat
import java.util.Locale

fun String.toYearMonth(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(this)?.let {
        SimpleDateFormat("MM月dd日", Locale.JAPAN).format(it)
    }.orEmpty()
}