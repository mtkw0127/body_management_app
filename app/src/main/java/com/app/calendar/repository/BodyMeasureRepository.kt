package com.app.calendar.repository

import androidx.annotation.WorkerThread
import com.app.calendar.dao.BodyMeasureDao
import com.app.calendar.model.BodyMeasureEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class BodyMeasureRepository(private val trainingDao: BodyMeasureDao) {

    fun getAll(): Flow<List<BodyMeasureEntity>> = trainingDao.getAll()

    @WorkerThread
    suspend fun insert(bodyMeasureEntity: BodyMeasureEntity) {
        trainingDao.insert(bodyMeasureEntity)
    }

    fun getEntityListByDate(date: LocalDate): Flow<List<BodyMeasureEntity>> {
        return trainingDao.getTrainingEntityListByDate(date)
    }
}