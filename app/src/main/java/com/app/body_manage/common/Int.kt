package com.app.body_manage.common

fun Int?.toKcal(): String = if (this != null) "$this kcal" else "-"
fun Int?.toNumber(): String = "${this ?: 0} 個"
