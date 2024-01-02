package com.app.body_manage.data.local

import com.app.body_manage.data.model.BodyMeasureModel
import java.time.LocalDate
import java.time.LocalTime

data class UserPreference(
    val tall: Float?,
    val fat: Float?,
    val weight: Float?,
    val alarm: Boolean?,
)

fun UserPreference.toBodyMeasureForAdd(date: LocalDate) = BodyMeasureModel(
    id = BodyMeasureModel.Id(0),
    capturedLocalDateTime = date.atTime(LocalTime.now()), // その日付の現在時刻
    weight = weight ?: 50F,
    fat = fat ?: 20F,
    photoUri = null,
    tall = tall ?: 150F,
)
