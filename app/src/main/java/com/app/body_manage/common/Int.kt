package com.app.body_manage.common

fun Long?.toKcal(): String = if (this != null) "$this kcal" else "-"
fun Int?.toNumber(): String = "${this ?: 0} 個"
fun Long?.toKg(): String = "$this kg"
fun Long?.toCount(): String = "$this 回"
fun Long?.toSet(): String = "$this セット"
