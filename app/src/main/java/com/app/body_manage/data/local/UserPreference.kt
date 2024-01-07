package com.app.body_manage.data.local

import com.app.body_manage.data.model.BodyMeasureModel
import com.app.body_manage.domain.BMICalculator
import com.app.body_manage.domain.FatCalculator
import com.app.body_manage.extension.age
import com.app.body_manage.extension.toKcal
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

    val basicConsumeEnergy: String
        get() {
            val height = checkNotNull(tall) * 100 / 100F
            val weight = checkNotNull(weight) * 100 / 100F
            val age = birth.age()
            val consumedEnergy = (when (gender) {
                Gender.MALE -> {
                    (0.0481 * weight + 0.0234 * height - 0.0138 * age - 0.4235) * 1000 / 4.186
                }

                Gender.FEMALE -> {
                    (0.0481 * weight + 0.0234 * height - 0.0138 * age - 0.9708) * 1000 / 4.186
                }
            } * 100).toInt() / 100F

            return consumedEnergy.toKcal()
        }
}

enum class Gender(val value: Int) {
    MALE(0), FEMALE(1)
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
