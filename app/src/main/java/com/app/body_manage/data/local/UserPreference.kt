package com.app.body_manage.data.local

import com.app.body_manage.domain.BMICalculator
import com.app.body_manage.domain.FatCalculator
import com.app.body_manage.extension.age
import com.app.body_manage.extension.toKcal
import java.time.LocalDate
import kotlin.math.pow

data class UserPreference(
    val name: String,
    val gender: Gender,
    val birth: LocalDate,
    val startWeight: Float?,
    val goalWeight: Float?,
    val goalKcal: Long?,
    val alarm: Boolean?,
    val optionFeature: OptionFeature
) {
    data class OptionFeature(
        val meal: Boolean?,
        val training: Boolean?,
    )

    fun bim(tall: Float, weight: Float): String = BMICalculator().calculate(tall, weight)

    fun calcFat(tall: Float, weight: Float): String = FatCalculator().calculate(
        tall = tall,
        weight = weight,
        age = birth.age(),
        gender = gender
    )

    fun healthyDuration(tall: Float): String {
        val tallCalc = (tall / 100F).toDouble().pow(2.0)
        val min = (18.5F * tallCalc * 100).toInt() / 100F
        val max = (24.9F * tallCalc * 100).toInt() / 100F
        return "${min}kg - ${max}kg"
    }

    fun progressWeight(weight: Float): Float {
        if (goalWeight == null) {
            return 0F
        }
        return goalWeight / weight
    }

    fun progressKcal(todayKcal: Long): Float {
        if (goalKcal == null) {
            return 0F
        }
        return todayKcal.toFloat() / goalKcal.toFloat()
    }

    fun progressWeightText(tall: Float): String = "${(progressWeight(tall) * 100).toInt()} %"

    fun progressKcalText(totalKcal: Long): String = "${(progressKcal(totalKcal) * 100).toInt()} %"

    fun basicConsumeEnergy(inputTall: Float, inputWeight: Float): String {
        val height = inputTall * 100 / 100F
        val weight = inputWeight * 100 / 100F
        val age = birth.age()
        val consumedEnergy = (
                when (gender) {
                    Gender.MALE -> {
                        (0.0481 * weight + 0.0234 * height - 0.0138 * age - 0.4235) * 1000 / 4.186
                    }

                    Gender.FEMALE -> {
                        (0.0481 * weight + 0.0234 * height - 0.0138 * age - 0.9708) * 1000 / 4.186
                    }
                } * 100
                ).toInt() / 100F

        return consumedEnergy.toKcal()
    }
}

enum class Gender(val value: Int) {
    MALE(0), FEMALE(1)
}
