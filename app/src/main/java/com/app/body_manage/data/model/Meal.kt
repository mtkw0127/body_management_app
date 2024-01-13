package com.app.body_manage.data.model

import androidx.annotation.StringRes
import com.app.body_manage.R
import com.app.body_manage.data.entity.MealEntity
import java.io.Serializable
import java.time.LocalDateTime

data class Meal(
    val id: Id,
    val timing: Timing,
    override val time: LocalDateTime,
    val foods: List<Food>,
    val photos: List<MealPhoto>,
) : Measure {
    data class Id(val value: Int) : Serializable
    enum class Timing(@StringRes val textResourceId: Int) {
        BREAKFAST(R.string.label_breakfast),
        LUNCH(R.string.label_lunch),
        DINNER(R.string.label_dinner),
        SNACK(R.string.label_snack),
    }

    val totalKcal: Int
        get() {
            return foods.sumOf {
                it.kcal * it.number
            }.toInt()
        }

    companion object {
        fun init() = Meal(
            id = Id(0),
            timing = Timing.BREAKFAST,
            time = LocalDateTime.now(),
            foods = emptyList(),
            photos = emptyList(),
        )
    }
}

fun Meal.toEntity() = MealEntity(
    mealId = this.id.value,
    timing = this.timing.name,
    dateTime = this.time,
)
