package com.app.body_manage.data.local

import com.app.body_manage.data.model.BodyMeasureModel
import java.time.LocalDateTime

data class UserPreference(
    val tall: Float?,
    val fat: Float?,
    val weight: Float?,
    val alarm: Boolean?,
)

fun UserPreference.toBodyMeasureForAdd() = BodyMeasureModel(
    id = BodyMeasureModel.Id(0),
    capturedLocalDateTime = LocalDateTime.now(),
    weight = weight ?: 50F,
    fat = fat ?: 20F,
    photoUri = null,
    tall = tall ?: 150F,
)
