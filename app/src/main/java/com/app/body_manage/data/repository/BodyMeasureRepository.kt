package com.app.body_manage.data.repository

import androidx.annotation.WorkerThread
import com.app.body_manage.data.dao.BodyMeasureDao
import com.app.body_manage.data.entity.BodyMeasureEntity
import com.app.body_manage.data.entity.BodyMeasureModel
import com.app.body_manage.data.entity.toModel
import java.time.LocalDate
import java.time.LocalDateTime

class BodyMeasureRepository(private val trainingDao: BodyMeasureDao) {

    @WorkerThread
    suspend fun insert(bodyMeasureEntity: BodyMeasureEntity): Long {
        return trainingDao.insert(bodyMeasureEntity)
    }

    suspend fun getEntityListBetween(): List<BodyMeasureEntity> {
        return trainingDao.getTrainingEntityListBetween()
    }

    suspend fun getEntityListByDate(date: LocalDate): List<BodyMeasureModel> {
        return trainingDao.getTrainingEntityListByDate(date).map { it.toModel() }
    }

    suspend fun getEntityByCaptureTime(localDateTime: LocalDateTime): List<BodyMeasureEntity> {
        return trainingDao.getTrainingEntityByLocalDateTime(localDateTime)
    }

    suspend fun update(bodyMeasureEntity: BodyMeasureEntity): Int {
        return trainingDao.update(bodyMeasureEntity)
    }
}