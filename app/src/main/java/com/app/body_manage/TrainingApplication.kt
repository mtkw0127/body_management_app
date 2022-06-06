package com.app.body_manage

import android.app.Application
import com.app.body_manage.database.AppDatabase
import com.app.body_manage.repository.BodyMeasureRepository
import com.app.body_manage.repository.PhotoRepository

class TrainingApplication : Application() {
    private val database by lazy { AppDatabase.createDatabase(this) }
    val bodyMeasureRepository by lazy { BodyMeasureRepository(database.bodyMeasureDao()) }
    val photoRepository by lazy { PhotoRepository(database.photoDao()) }
}