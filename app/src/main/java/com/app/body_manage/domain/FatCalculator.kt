package com.app.body_manage.domain

import com.app.body_manage.data.local.Gender

class FatCalculator {
    fun calculate(
        tall: Float,
        weight: Float,
        age: Int,
        gender: Gender,
    ): String {
        val bmi = BMICalculator().calculate(tall, weight)
        try {
            bmi.toFloat()
        } catch (_: Throwable) {
            return "-"
        }
        val fat = 1.2F * bmi.toFloat() + 0.23F * age - 5.4 - 10.8 * when (gender) {
            Gender.MALE -> 1
            Gender.FEMALE -> 0
        }
        return ((fat * 100).toInt() / 100F).toString()
    }
}