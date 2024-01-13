package com.app.body_manage.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mealAndFood")
data class MealFoodCrossRef(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val mealId: Long,
    val foodId: Long,
    val number: Long,
)
