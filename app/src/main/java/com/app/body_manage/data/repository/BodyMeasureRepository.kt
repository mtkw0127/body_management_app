package com.app.body_manage.data.repository

import com.app.body_manage.data.dao.BodyMeasureDao
import com.app.body_manage.data.entity.BodyMeasureEntity
import com.app.body_manage.data.entity.toModel
import com.app.body_manage.data.model.BodyMeasure
import com.app.body_manage.data.model.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime

class BodyMeasureRepository(
    private val trainingDao: BodyMeasureDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend fun insert(model: BodyMeasure): BodyMeasure.Id {
        val id = trainingDao.insert(model.toEntity())
        return BodyMeasure.Id(id.toInt())
    }

    suspend fun getEntityListAll(): List<BodyMeasure> {
        return trainingDao.getTrainingEntityListAll().map { it.toModel() }
    }

    suspend fun getLast(): BodyMeasureEntity? {
        return trainingDao.getLast()
    }

    suspend fun getFirst(): BodyMeasureEntity? {
        return trainingDao.getFirst()
    }

    suspend fun getBetween(from: LocalDate, to: LocalDate): List<BodyMeasure> =
        withContext(ioDispatcher) {
            return@withContext trainingDao.getTrainingEntityListBetween(from, to)
                .map { it.toModel() }
        }

    suspend fun getBetweenGroupByDate(from: LocalDate, to: LocalDate): List<BodyMeasure> =
        withContext(ioDispatcher) {
            return@withContext trainingDao.getTrainingEntityListBetweenGroupByDate(from, to)
                .map { it.toModel() }
        }

    suspend fun updateTallByDate(tall: Float, calendarDate: LocalDate): Int =
        withContext(ioDispatcher) {
            return@withContext trainingDao.updateTallByDate(tall, calendarDate)
        }

    suspend fun getEntityListByDate(date: LocalDate): List<BodyMeasure> {
        return trainingDao.getTrainingEntityListByDate(date).map { it.toModel() }
    }

    suspend fun getEntityByCaptureTime(
        localDateTime: LocalDateTime
    ): List<BodyMeasureEntity> {
        return trainingDao.getTrainingEntityByLocalDateTime(localDateTime)
    }

    suspend fun deleteBodyMeasure(localDateTime: LocalDateTime): Int {
        return trainingDao.deleteBodyMeasure(localDateTime)
    }

    suspend fun update(model: BodyMeasure): Int {
        return trainingDao.update(model.toEntity())
    }

    suspend fun fetch(bodyMeasureId: BodyMeasure.Id): BodyMeasure {
        return trainingDao.fetch(bodyMeasureId.value).toModel()
    }
}
