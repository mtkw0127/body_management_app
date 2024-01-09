package com.app.body_manage.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val mealId: Int,
    @ColumnInfo(name = "timing") val timing: String, // 朝昼夕間
    @ColumnInfo(name = "dateTime") val dateTime: LocalDateTime, // 食事をした時間
)
