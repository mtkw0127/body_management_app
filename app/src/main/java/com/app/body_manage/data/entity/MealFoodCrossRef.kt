package com.app.body_manage.data.entity

import androidx.room.Entity

@Entity(
    tableName = "mealAndFood",
    primaryKeys = ["mealId", "foodId"]
)
data class MealFoodCrossRef(
    val mealId: Long,
    val foodId: Long,
)
