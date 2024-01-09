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

    suspend fun getMeal(id: Meal.Id): Meal? {
        return mealFoodsDao.getMeal(id.value.toLong())?.toModel()
    }

    suspend fun getFoods(text: String): List<Food> {
        return mealFoodsDao.getFoods(text).map { it.toModel() }
    }

    @Transaction
    suspend fun saveMeal(meal: Meal) {
        val mealId = mealFoodsDao.saveMeal(meal.toEntity())
        val newFoodIds = mealFoodsDao.saveFoods(
            // NEW_IDのものを新規登録する
            meal.foods.filter { it.id == Food.NEW_ID }.map { it.toEntity() }
        )
        val registeredFoodIds =
            meal.foods.filterNot { it.id == Food.NEW_ID }.map { it.id.value.toLong() }

        (newFoodIds + registeredFoodIds).forEach { foodId ->
            mealFoodsDao.saveMealFoods(MealFoodCrossRef(mealId, foodId))
        }
    }

    @Transaction
    suspend fun updateMeal(meal: Meal) {
        mealFoodsDao.deleteMeal(meal.toEntity())
        val mealId = mealFoodsDao.saveMeal(meal.toEntity())

        // 食事と食べ物の関係性を一度削除する
        mealFoodsDao.deleteMealFoods(mealId)

        // 新しい食事を新規登録する
        val newFoodIds = mealFoodsDao.saveFoods(
            meal.foods.filter { it.id == Food.NEW_ID }.map { it.toEntity() }
        )

        // 再登録する
        val registeredFoods = meal.foods.filterNot { it.id == Food.NEW_ID }
        val registeredFoodIds = registeredFoods.map { it.id.value.toLong() }

        // 関係性を再構築する
        (newFoodIds + registeredFoodIds).forEach { foodId ->
            mealFoodsDao.saveMealFoods(MealFoodCrossRef(mealId, foodId))
        }

        // 食べ物のカロリーが更新されている可能性があるので更新
        if (registeredFoods.isNotEmpty()) {
            mealFoodsDao.updateFoods(registeredFoods.map { it.toEntity() })
        }
    }

    @Transaction
    suspend fun deleteMeal(meal: Meal) {
        mealFoodsDao.deleteMeal(meal.toEntity())
        mealFoodsDao.deleteMealFoods(meal.id.value.toLong())
    }
}
