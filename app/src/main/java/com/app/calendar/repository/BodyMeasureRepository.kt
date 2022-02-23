package com.app.calendar.repository

import androidx.annotation.WorkerThread
import com.app.calendar.dao.BodyMeasureDao
import com.app.calendar.model.BodyMeasureEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

class BodyMeasureRepository(private val trainingDao: BodyMeasureDao) {

    fun getAll(): Flow<List<BodyMeasureEntity>> = trainingDao.getAll()

    @WorkerThread
    suspend fun insert(bodyMeasureEntity: BodyMeasureEntity) {
        trainingDao.insert(bodyMeasureEntity)
    }

    fun getEntityListByDate(date: LocalDate): Flow<List<BodyMeasureEntity>> {
        return trainingDao.getTrainingEntityListByDate(date)
    }

    fun getEntityByCaptureTime(localDateTime: LocalDateTime): Flow<BodyMeasureEntity> {
        return trainingDao.getTrainingEntityByLocalDateTime(localDateTime)
    }

    suspend fun update(bodyMeasureEntity: BodyMeasureEntity): Int {
        return trainingDao.update(bodyMeasureEntity)
    }
}