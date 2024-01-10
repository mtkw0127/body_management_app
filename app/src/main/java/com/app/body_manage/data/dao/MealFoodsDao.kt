package com.app.body_manage.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.body_manage.data.entity.FoodEntity
import com.app.body_manage.data.entity.MealEntity
import com.app.body_manage.data.entity.MealFoodCrossRef
import com.app.body_manage.data.entity.MealFoodsEntity
import com.app.body_manage.data.entity.MealPhotoEntity
import java.time.LocalDateTime

@Dao
interface MealFoodsDao {

    @Transaction
    @Query("SELECT * FROM MEALS WHERE dateTime BETWEEN :from AND :to")
    suspend fun getMeals(from: LocalDateTime, to: LocalDateTime): List<MealFoodsEntity>

    @Transaction
    @Query("SELECT * FROM MEALS WHERE mealId = :id")
    suspend fun getMeal(id: Long): MealFoodsEntity?

    @Insert
    suspend fun saveMeal(mealFoods: MealEntity): Long

    @Update
    suspend fun updateMeal(mealFoods: MealEntity)

    @Delete
    suspend fun deleteMeal(mealFoods: MealEntity)

    @Insert
    suspend fun saveFoods(foods: List<FoodEntity>): List<Long>

    @Delete
    suspend fun deleteFoods(foods: List<FoodEntity>)

    @Insert
    suspend fun saveMealFoods(mealFood: MealFoodCrossRef)

    @Query("DELETE FROM mealAndFood WHERE mealId = :mealId")
    suspend fun deleteMealFoods(mealId: Long)

    @Query(
        "SELECT * FROM foods " +
            "WHERE name LIKE '%' || :name || '%' OR " +
            "name_jp LIKE '%' || :name || '%' OR " +
            "name_kana LIKE '%' || :name || '%'"
    )
    suspend fun getFoods(name: String): List<FoodEntity>

    @Update
    suspend fun updateFoods(foods: List<FoodEntity>)

    @Insert
    suspend fun insertMealPhoto(mealPhotos: List<MealPhotoEntity>)

    @Query("SELECT * FROM meal_photos WHERE meal_id = :mealId")
    suspend fun getMealPhotos(mealId: Long): List<MealPhotoEntity>
}
