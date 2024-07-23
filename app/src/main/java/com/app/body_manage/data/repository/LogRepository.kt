package com.app.body_manage.data.repository

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class LogRepository {

    companion object {
        const val KEY_REVIEW_REQUEST = "review_request"
        const val KEY_MEASURE_ADD = "measure_add"
        const val KEY_MEASURE_EDIT = "measure_edit"
        const val KEY_OPEN_MEASURE_CAMERA = "open_measure_camera"
        const val KEY_SAVE_MEAL = "meal_add"
        const val KEY_OPEN_MEAL_CAMERA = "open_meal_camera"
        const val KEY_ADD_MEAL = "add_meal"
        const val KEY_SAVE_HISTORY = "save_history"
        const val KEY_OPEN_OBJECT_WEIGHT = "open_object_weight"
        const val KEY_OPEN_OBJECT_KCAL = "open_object_kcal"
        const val KEY_INITIAL_DIALOG = "open_initial_dialog"
        const val KEY_USER_SETTINGS = "set_user_settings"
    }

    fun sendLog(
        context: Context,
        key: String,
        bundle: Bundle = Bundle(),
    ) {
        val instance = FirebaseAnalytics.getInstance(context)
        instance.logEvent(key, bundle)
    }
}
