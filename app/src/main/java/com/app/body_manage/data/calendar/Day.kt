package com.app.body_manage.data.calendar

import java.time.LocalDate

sealed interface Day {
    val value: LocalDate
    val hasMorning: Boolean
    val hasLunch: Boolean
    val hasDinner: Boolean
    val hasMiddle: Boolean
    val kcal: Int
    val weight: Float?
    fun hasSomeMeal(): Boolean = hasMorning || hasLunch || hasDinner || hasMiddle
    fun hasSomething(): Boolean = hasSomeMeal() || weight != null
}
