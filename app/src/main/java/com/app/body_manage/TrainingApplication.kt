package com.app.body_manage

import android.app.Application
import com.app.body_manage.data.database.AppDatabase
import com.app.body_manage.data.repository.BodyMeasurePhotoRepository
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.data.repository.PhotoRepository

class TrainingApplication : Application() {
    private val database by lazy { AppDatabase.createDatabase(this) }
    val bodyMeasureRepository by lazy { BodyMeasureRepository(database.bodyMeasureDao()) }
    val photoRepository by lazy { PhotoRepository(database.photoDao()) }
    val bodyMeasurePhotoRepository by lazy { BodyMeasurePhotoRepository(database.bodyMeasurePhotoDao()) }

}