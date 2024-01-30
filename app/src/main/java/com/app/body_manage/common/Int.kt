package com.app.body_manage.common

fun Int?.toKcal(): String = if (this != null) "$this kcal" else "-"
fun Int?.toNumber(): String = "${this ?: 0} 個"
fun Int?.toKg(): String = "$this kg"
fun Int?.toCount(): String = "$this 回"
fun Int?.toSet(): String = "$this セット"