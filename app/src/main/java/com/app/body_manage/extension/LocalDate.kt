package com.app.body_manage.extension

import java.time.DayOfWeek
import java.time.LocalDate

fun LocalDate.toMMDD(): String = "${monthValue}月${dayOfMonth}日"

fun LocalDate.toMMDDEE(): String {
    val japaneseDayOfWeek = when (dayOfWeek) {
        DayOfWeek.SUNDAY -> "日"
        DayOfWeek.MONDAY -> "月"
        DayOfWeek.TUESDAY -> "火"
        DayOfWeek.WEDNESDAY -> "水"
        DayOfWeek.THURSDAY -> "木"
        DayOfWeek.FRIDAY -> "金"
        DayOfWeek.SATURDAY -> "土"
    }
    return "${monthValue}月${dayOfMonth}日($japaneseDayOfWeek)"
}
