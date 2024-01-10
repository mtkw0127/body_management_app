package com.app.body_manage.data.repository

import androidx.room.Transaction
import com.app.body_manage.data.dao.MealFoodsDao
import com.app.body_manage.data.entity.MealFoodCrossRef
import com.app.body_manage.data.entity.MealPhotoEntity
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
        // 食事と写真を紐付けたモデルを返す
        return mealFoodsDao.getMeals(from, to).map { mealPhotoEntity ->
            val mealPhotos = mealFoodsDao.getMealPhotos(mealPhotoEntity.meal.mealId.toLong())
                .map { it.toModel() }
            mealPhotoEntity.toModel(mealPhotos)
        }
    }

    suspend fun getMeal(id: Meal.Id): Meal? {
        val photos = mealFoodsDao.getMealPhotos(id.value.toLong()).map { it.toModel() }
        return mealFoodsDao.getMeal(id.value.toLong())?.toModel(photos)
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
        // 写真の紐付け
        meal.photos.map { photo ->
            MealPhotoEntity(id = 0, mealId = mealId.toInt(), photoUri = photo.uri.toString())
        }.let {
            mealFoodsDao.insertMealPhoto(it)
        }
    }

    @Transaction
    suspend fun updateMeal(meal: Meal) {
        mealFoodsDao.updateMeal(meal.toEntity())

        val mealId = meal.id.value.toLong()

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

        // 写真の紐付け
        meal.photos.filter { photo ->
            // 新しい写真を新規で追加
            photo.id.value == 0
        }.map { photo ->
            MealPhotoEntity(id = 0, mealId = mealId.toInt(), photoUri = photo.uri.toString())
        }.let {
            mealFoodsDao.insertMealPhoto(it)
        }
    }

    @Transaction
    suspend fun deleteMeal(meal: Meal) {
        mealFoodsDao.deleteMeal(meal.toEntity())
        mealFoodsDao.deleteMealFoods(meal.id.value.toLong())
    }
}
