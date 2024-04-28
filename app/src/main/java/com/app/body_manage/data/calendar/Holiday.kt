package com.app.body_manage.data.calendar

import java.time.LocalDate

data class Holiday(
    override val value: LocalDate,
    override val hasMorning: Boolean = false,
    override val hasLunch: Boolean = false,
    override val hasDinner: Boolean = false,
    override val hasMiddle: Boolean = false,
    override val kcal: Long,
    override val weight: Float?,
    override val training: Boolean,
    val name: String // 休日の名前
) : Day
