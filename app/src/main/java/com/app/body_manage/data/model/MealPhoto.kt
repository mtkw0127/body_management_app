package com.app.body_manage.data.model

import android.net.Uri

data class MealPhoto(
    override val id: Photo.Id = Photo.Id(0),
    val mealId: Meal.Id? = null,
    override val uri: Uri,
) : Photo
