package com.app.body_manage.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.app.body_manage.data.entity.FoodEntity
import com.app.body_manage.data.entity.MealEntity
import com.app.body_manage.data.entity.MealFoodCrossRef
import com.app.body_manage.data.entity.MealFoodsEntity
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

    @Insert
    suspend fun saveFoods(foods: List<FoodEntity>): List<Long>

    @Insert
    suspend fun saveMealFoods(mealFood: MealFoodCrossRef)

    @Query(
        "SELECT * FROM foods " +
            "WHERE name LIKE '%' || :name || '%' OR " +
            "name_jp LIKE '%' || :name || '%' OR " +
            "name_kana LIKE '%' || :name || '%'"
    )
    suspend fun getFoods(name: String): List<FoodEntity>
}
