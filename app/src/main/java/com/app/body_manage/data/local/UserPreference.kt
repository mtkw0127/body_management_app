package com.app.body_manage.data.local

import androidx.annotation.StringRes
import com.app.body_manage.R
import com.app.body_manage.data.model.BodyMeasureModel
import com.app.body_manage.domain.BMICalculator
import com.app.body_manage.domain.FatCalculator
import com.app.body_manage.extension.age
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
) {
    val bim: String
        get() {
            return BMICalculator().calculate(tall, checkNotNull(weight))
        }

    val calcFat: String
        get() {
            return FatCalculator().calculate(
                checkNotNull(tall),
                checkNotNull(weight),
                birth.age(),
                gender
            )
        }
}

enum class Gender(@StringRes labelResource: Int, val value: Int) {
    MALE(R.string.gender_male, 0), FEMALE(R.string.gender_female, 1)
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
