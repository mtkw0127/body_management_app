package com.app.calendar.repository

import androidx.annotation.WorkerThread
import com.app.calendar.dao.BodyMeasureDao
import com.app.calendar.model.BodyMeasureEntity
import java.time.LocalDate
import java.time.LocalDateTime

class BodyMeasureRepository(private val trainingDao: BodyMeasureDao) {

    @WorkerThread
    suspend fun insert(bodyMeasureEntity: BodyMeasureEntity): Long {
        return trainingDao.insert(bodyMeasureEntity)
    }

    suspend fun getEntityListByDate(date: LocalDate): List<BodyMeasureEntity> {
        return trainingDao.getTrainingEntityListByDate(date)
    }

    suspend fun getEntityByCaptureTime(localDateTime: LocalDateTime): List<BodyMeasureEntity> {
        return trainingDao.getTrainingEntityByLocalDateTime(localDateTime)
    }

    suspend fun update(bodyMeasureEntity: BodyMeasureEntity): Int {
        return trainingDao.update(bodyMeasureEntity)
    }
}