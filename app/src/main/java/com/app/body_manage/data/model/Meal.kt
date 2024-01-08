package com.app.body_manage.data.model

import androidx.annotation.StringRes
import com.app.body_manage.R
import com.app.body_manage.data.entity.MealEntity
import java.time.LocalDateTime

data class Meal(
    val id: Id,
    val timing: Timing,
    val dateTime: LocalDateTime,
    val foods: List<Food>,
) {
    data class Id(val value: Int)
    enum class Timing(@StringRes val textResourceId: Int) {
        BREAKFAST(R.string.label_breakfast),
        LUNCH(R.string.label_lunch),
        DINNER(R.string.label_dinner),
        SNACK(R.string.label_snack),
    }

    companion object {
        fun init() = Meal(
            id = Meal.Id(0),
            timing = Meal.Timing.BREAKFAST,
            dateTime = LocalDateTime.now(),
            foods = emptyList(),
        )
    }
}

fun Meal.toEntity() = MealEntity(
    mealId = this.id.value,
    timing = this.timing.name,
    dateTime = this.dateTime,
)
