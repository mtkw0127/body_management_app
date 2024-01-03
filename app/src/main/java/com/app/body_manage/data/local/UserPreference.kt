package com.app.body_manage.data.local

import androidx.annotation.StringRes
import com.app.body_manage.R
import com.app.body_manage.data.model.BodyMeasureModel
import java.time.LocalDate
import java.time.LocalTime

data class UserPreference(
    val name: String,
    val gender: Gender,
    val birth: LocalDate,
    val tall: Float?,
    val fat: Float?,
    val weight: Float?,
    val alarm: Boolean?,
)

enum class Gender(@StringRes labelResource: Int) {
    MALE(R.string.gender_male), FEMALE(R.string.gender_female)
}

fun UserPreference.toBodyMeasureForAdd(date: LocalDate) = BodyMeasureModel(
    id = BodyMeasureModel.Id(0),
    capturedLocalDateTime = date.atTime(LocalTime.now()), // その日付の現在時刻
    weight = weight ?: 50F,
    fat = fat ?: 20F,
    photoUri = null,
    tall = tall ?: 150F,
    memo = "",
)
