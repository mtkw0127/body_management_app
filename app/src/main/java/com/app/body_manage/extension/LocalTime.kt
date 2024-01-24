package com.app.body_manage.extension

import java.time.LocalTime

fun LocalTime.toJapaneseTime() = "${hour}時${minute}分"
