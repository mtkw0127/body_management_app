package com.app.body_manage.data.repository

import com.app.body_manage.data.dao.BodyMeasureDao
import com.app.body_manage.data.entity.BodyMeasureEntity
import com.app.body_manage.data.entity.BodyMeasureModel
import com.app.body_manage.data.entity.toModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime

class BodyMeasureRepository(private val trainingDao: BodyMeasureDao) {

    suspend fun insert(bodyMeasureEntity: BodyMeasureEntity): BodyMeasureModel.Id {
        return BodyMeasureModel.Id(trainingDao.insert(bodyMeasureEntity))
    }

    suspend fun getEntityListAll(): List<BodyMeasureModel> {
        return trainingDao.getTrainingEntityListAll().map { it.toModel() }
    }

    suspend fun getLast(): BodyMeasureEntity? {
        return trainingDao.getLast()
    }

    suspend fun getBetween(from: LocalDate, to: LocalDate): List<BodyMeasureModel> =
        withContext(Dispatchers.IO) {
            return@withContext trainingDao.getTrainingEntityListBetween(from, to)
                .map { it.toModel() }
        }

    suspend fun getBetweenGroupByDate(from: LocalDate, to: LocalDate): List<BodyMeasureModel> =
        withContext(Dispatchers.IO) {
            return@withContext trainingDao.getTrainingEntityListBetweenGroupByDate(from, to)
                .map { it.toModel() }
        }

    suspend fun updateTallByDate(tall: Float, calendarDate: LocalDate): Int =
        withContext(Dispatchers.IO) {
            return@withContext trainingDao.updateTallByDate(tall, calendarDate)
        }

    suspend fun getTallByDate(calendarDate: LocalDate): Float? =
        withContext(Dispatchers.IO) {
            return@withContext trainingDao.getTrainingEntityListByDate(calendarDate)
                .firstOrNull()?.tall
        }

    suspend fun getEntityListByDate(date: LocalDate): List<BodyMeasureModel> {
        return trainingDao.getTrainingEntityListByDate(date).map { it.toModel() }
    }

    suspend fun getEntityByCaptureTime(localDateTime: LocalDateTime): List<BodyMeasureEntity> {
        return trainingDao.getTrainingEntityByLocalDateTime(localDateTime)
    }

    suspend fun deleteBodyMeasure(localDateTime: LocalDateTime): Int {
        return trainingDao.deleteBodyMeasure(localDateTime)
    }

    suspend fun update(bodyMeasureEntity: BodyMeasureEntity): Int {
        return trainingDao.update(bodyMeasureEntity)
    }

    suspend fun fetch(bodyMeasureId: BodyMeasureModel.Id): BodyMeasureModel {
        return trainingDao.fetch(bodyMeasureId.ui.toInt()).toModel()
    }
}
