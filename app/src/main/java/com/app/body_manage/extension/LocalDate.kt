package com.app.body_manage.extension

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

fun LocalDate.toMMDD(): String {
    return if (Locale.getDefault().language == "ja") {
        "${monthValue}月${dayOfMonth}日"
    } else {
        DateTimeFormatter.ofPattern("MM/dd").format(this)
    }
}

fun LocalDate.toMMDDEE(): String {
    if (Locale.getDefault().language == "ja") {
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
    } else {
        val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH)
        val formattedDate = this.format(formatter)
        return formattedDate
    }
}

fun LocalDate.age(): Int {
    val currentDate = LocalDate.now()
    return Period.between(this, currentDate).years
}
