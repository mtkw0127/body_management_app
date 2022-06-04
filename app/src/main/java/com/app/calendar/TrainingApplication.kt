package com.app.calendar

import android.app.Application
import com.app.calendar.database.AppDatabase
import com.app.calendar.repository.BodyMeasureRepository
import com.app.calendar.repository.PhotoRepository

class TrainingApplication : Application() {
    private val database by lazy { AppDatabase.createDatabase(this) }
    val bodyMeasureRepository by lazy { BodyMeasureRepository(database.bodyMeasureDao()) }
    val photoRepository by lazy { PhotoRepository(database.photoDao()) }
}