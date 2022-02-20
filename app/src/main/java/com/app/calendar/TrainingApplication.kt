package com.app.calendar

import android.app.Application
import com.app.calendar.database.AppDatabase
import com.app.calendar.repository.BodyMeasureRepository

class TrainingApplication: Application() {
    private val database by lazy {AppDatabase.createDatabase(this)}
    val repository by lazy {BodyMeasureRepository(database.bodyMeasureDao())}
}