package com.app.body_manage.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.body_manage.data.model.Food

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true) val foodId: Int,
    @ColumnInfo(name = "name") val name: String, // 食べ物の名前
    @ColumnInfo(name = "name_jp") val nameJp: String, // 食べ物の名前（ひらがな）
    @ColumnInfo(name = "name_kana") val nameKana: String, // 食べ物の名前（かな）
    @ColumnInfo(name = "kcal") val kcal: Int, // カロリー
)

fun FoodEntity.toModel(foodNumber: Long) = Food(
    id = Food.Id(this.foodId),
    name = this.name,
    nameJp = this.nameJp,
    nameKana = this.nameKana,
    kcal = this.kcal,
    number = foodNumber,
)
