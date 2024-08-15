package com.app.body_manage.extension

import java.time.LocalTime

fun LocalTime.toTimeText() = "${hour}時${minute}分"
