package com.app.body_manage.data.repository

import com.app.body_manage.data.dao.BodyMeasurePhotoDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class BodyMeasurePhotoRepository(
    private val bpDao: BodyMeasurePhotoDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend fun selectPhotosByDate(): Map<String, List<BodyMeasurePhotoDao.PhotoData>> =
        withContext(ioDispatcher) {
            return@withContext bpDao.selectPhotosByDate()
        }

    suspend fun selectPhotosByWeight(): Map<String, List<BodyMeasurePhotoDao.PhotoData>> =
        withContext(ioDispatcher) {
            val map = mutableMapOf<String, List<BodyMeasurePhotoDao.PhotoData>>()
            val data = bpDao.selectPhotosByWeight()
            data.forEach { (t, u) ->
                run {
                    map[t.toString()] = u
                }
            }
            return@withContext map.toMap()
        }

    suspend fun selectPhotosByDate(date: LocalDate): List<BodyMeasurePhotoDao.PhotoData> =
        withContext(ioDispatcher) {
            val photoListMap = bpDao.selectPhotosByDateOnlyDate(date)
            return@withContext if (photoListMap.isEmpty()) {
                listOf()
            } else {
                photoListMap.firstNotNullOf { it.value }
            }
        }

    suspend fun selectHavePhotoDateList(from: LocalDate, to: LocalDate): List<LocalDate> =
        withContext(ioDispatcher) {
            return@withContext bpDao.selectHavePhotoDateList(from, to)
        }

    suspend fun selectBodyMeasureByPhotoId(photoId: Int): BodyMeasurePhotoDao.BodyMeasure? =
        withContext(ioDispatcher) {
            return@withContext bpDao.selectBodyMeasureByPhotoId(photoId)
        }
}
