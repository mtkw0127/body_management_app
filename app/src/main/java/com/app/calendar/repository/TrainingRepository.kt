package com.app.calendar.repository

import androidx.annotation.WorkerThread
import com.app.calendar.dao.TrainingDao
import com.app.calendar.model.TrainingEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class TrainingRepository(private val trainingDao: TrainingDao) {

    fun getAll(): Flow<List<TrainingEntity>> = trainingDao.getAll()

    @WorkerThread
    suspend fun insert(trainingEntity: TrainingEntity) {
        trainingDao.insert(trainingEntity)
    }

    fun getEntityListByDate(date: LocalDate): Flow<List<TrainingEntity>> {
        return trainingDao.getTrainingEntityListByDate(date)
    }
}