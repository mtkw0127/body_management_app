package com.app.body_manage.data.repository

import com.app.body_manage.data.dao.BodyMeasurePhotoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class BodyMeasurePhotoRepository(private val bpDao: BodyMeasurePhotoDao) {
    suspend fun selectPhotosByDate(): Map<String, List<BodyMeasurePhotoDao.PhotoData>> =
        withContext(Dispatchers.IO) {
            return@withContext bpDao.selectPhotosByDate()
        }

    suspend fun selectPhotosByDate(date: LocalDate): List<BodyMeasurePhotoDao.PhotoData> =
        withContext(Dispatchers.IO) {
            val photoListMap = bpDao.selectPhotosByDateOnlyDate(date)
            return@withContext if (photoListMap.isEmpty()) {
                listOf()
            } else {
                photoListMap.firstNotNullOf { it.value }
            }
        }

    suspend fun selectHavePhotoDateList(from: LocalDate, to: LocalDate): List<LocalDate> =
        withContext(Dispatchers.IO) {
            return@withContext bpDao.selectHavePhotoDateList(from, to)
        }

    suspend fun selectBodyMeasureByPhotoId(photoId: Int): BodyMeasurePhotoDao.BodyMeasure? =
        withContext(Dispatchers.IO) {
            return@withContext bpDao.selectBodyMeasureByPhotoId(photoId)
        }
}
