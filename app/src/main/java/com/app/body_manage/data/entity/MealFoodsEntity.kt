package com.app.body_manage.data.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.app.body_manage.data.model.Meal

data class MealFoodsEntity(
    @Embedded val meal: MealEntity,
    @Relation(
        parentColumn = "mealId",
        entityColumn = "foodId",
        associateBy = Junction(MealFoodCrossRef::class)
    )
    val foods: List<FoodEntity>,
)

fun MealFoodsEntity.toModel() = Meal(
    id = Meal.Id(meal.mealId),
    timing = checkNotNull(Meal.Timing.entries.find { it.name == this.meal.timing }),
    time = this.meal.dateTime,
    foods = this.foods.map { it.toModel() }
)
