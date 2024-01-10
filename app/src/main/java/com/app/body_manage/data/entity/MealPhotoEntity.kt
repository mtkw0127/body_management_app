package com.app.body_manage.data.entity

import androidx.core.net.toUri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.app.body_manage.data.model.Meal
import com.app.body_manage.data.model.MealPhoto

@Entity(
    tableName = "meal_photos",
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = arrayOf("mealId"),
            childColumns = arrayOf("meal_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class MealPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "meal_id") val mealId: Int,
    @ColumnInfo(name = "photo_uri") val photoUri: String
)

fun MealPhotoEntity.toModel() = MealPhoto(
    id = MealPhoto.Id(id),
    mealId = Meal.Id(mealId),
    uri = photoUri.toUri(),
)
