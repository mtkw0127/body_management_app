package com.app.body_manage.data.repository

import androidx.room.Transaction
import com.app.body_manage.data.dao.MealFoodsDao
import com.app.body_manage.data.entity.MealFoodCrossRef
import com.app.body_manage.data.entity.toModel
import com.app.body_manage.data.model.Food
import com.app.body_manage.data.model.Meal
import com.app.body_manage.data.model.toEntity
import java.time.LocalDate

class MealRepository(
    private val mealFoodsDao: MealFoodsDao
) {
    suspend fun getMealsByDate(date: LocalDate): List<Meal> {
        val from = date.atTime(0, 0)
        val to = date.atTime(23, 59, 59)
        return mealFoodsDao.getMeals(from, to).map { it.toModel() }
    }

    suspend fun getFoods(text: String): List<Food> {
        return mealFoodsDao.getFoods(text).map { it.toModel() }
    }

    @Transaction
    suspend fun saveMeal(meal: Meal) {
        val mealId = mealFoodsDao.saveMeal(meal.toEntity())
        val foodIds = mealFoodsDao.saveFoods(meal.foods.map { it.toEntity() })
        foodIds.forEach { foodId ->
            mealFoodsDao.saveMealFoods(MealFoodCrossRef(mealId, foodId))
        }
    }
}
