package com.app.body_manage.domain

import kotlin.math.ceil
import kotlin.math.pow

class BMICalculator {
    fun calculate(tall: Float, weight: Float): Double {
        val tall2 = (tall.toDouble() / 100).pow(2)
        return ceil(weight / tall2 * 100) / 100
    }
}