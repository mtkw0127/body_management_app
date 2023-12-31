package com.app.body_manage.domain

import kotlin.math.ceil
import kotlin.math.pow

class BMICalculator {
    fun calculate(tall: Float, weight: Float): Double {
        val tall2 = (tall.toDouble() / MAX).pow(ROOT)
        return ceil(weight / tall2 * MAX) / MAX
    }
}

private const val MAX = 100
private const val ROOT = 2
