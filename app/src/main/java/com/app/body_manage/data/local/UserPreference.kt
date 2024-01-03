package com.app.body_manage.data.local

import androidx.annotation.StringRes
import com.app.body_manage.R
import com.app.body_manage.data.model.BodyMeasureModel
import com.app.body_manage.domain.BMICalculator
import com.app.body_manage.domain.FatCalculator
import com.app.body_manage.extension.age
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.pow

data class UserPreference(
    val name: String,
    val gender: Gender,
    val birth: LocalDate,
    val tall: Float?,
    val fat: Float?,
    val weight: Float?,
    val goalWeight: Float?,
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

    val healthyDuration: String
        get() {
            if (tall == null) return "-"
            val tallCalc = (tall / 100F).toDouble().pow(2.0)
            val min = (18.5F * tallCalc * 100).toInt() / 100F
            val max = (24.9F * tallCalc * 100).toInt() / 100F
            return "${min}kg - ${max}kg"
        }

    val goodWeight: String
        get() {
            if (tall == null) return "-"
            val doubleTall = (tall / 100F).toDouble().pow(2.0)
            val result = doubleTall * 22
            return "${(result * 100).toInt() / 100F} kg"
        }

    val progress: Float
        get() {
            if (weight == null || goalWeight == null) {
                return 0F
            }
            return goalWeight / weight
        }

    val progressText: String
        get() {
            return "${(progress * 100).toInt()} %"
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
