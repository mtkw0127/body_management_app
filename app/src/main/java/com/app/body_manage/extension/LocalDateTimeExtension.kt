package com.app.body_manage.extension

import java.time.LocalDateTime

fun LocalDateTime.toJapaneseTime(): String {
    val hour = String.format("%02d", hour)
    val minute = String.format("%02d", minute)
    return "${hour}時${minute}分"
}