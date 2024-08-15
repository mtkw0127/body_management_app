package com.app.body_manage.extension

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun LocalDateTime.toTimeText(): String {
    if (Locale.getDefault().language == "ja") {
        val hour = String.format(Locale.getDefault(), "%02d", hour)
        val minute = String.format(Locale.getDefault(), "%02d", minute)
        return "${hour}時${minute}分"
    } else {
        return DateTimeFormatter.ofPattern("HH:mm a").format(this)
    }
}
