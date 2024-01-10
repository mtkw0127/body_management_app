package com.app.body_manage.data.model

import android.net.Uri
import java.io.Serializable

data class MealPhoto(
    val id: Id = Id(0),
    val mealId: Meal.Id? = null,
    val uri: Uri,
) {
    @JvmInline
    value class Id(val value: Int) : Serializable
}
