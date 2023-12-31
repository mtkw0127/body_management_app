package com.app.body_manage.domain

import kotlin.math.ceil
import kotlin.math.pow

class BMICalculator {
    fun calculate(tall: Float?, weight: Float): String {
        if (tall == null) return "-"
        val tall2 = (tall.toDouble() / MAX).pow(ROOT)
        return (ceil(weight / tall2 * MAX) / MAX).toString()
    }
}

private const val MAX = 100
private const val ROOT = 2
