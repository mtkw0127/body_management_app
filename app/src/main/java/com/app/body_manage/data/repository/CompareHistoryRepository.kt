package com.app.body_manage.data.repository

import com.app.body_manage.data.dao.CompareBodyMeasureHistoryDao
import com.app.body_manage.data.entity.CompareBodyMeasureHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CompareHistoryRepository(private val bpDao: CompareBodyMeasureHistoryDao) {
    suspend fun saveHistory(compareHistoryEntity: CompareBodyMeasureHistoryEntity) =
        withContext(Dispatchers.IO) {
            return@withContext bpDao.saveHistory(compareHistoryEntity)
        }

    suspend fun selectAll() =
        withContext(Dispatchers.IO) {
            return@withContext bpDao.selectAll()
        }

    suspend fun delete(entity: CompareBodyMeasureHistoryEntity) =
        withContext(Dispatchers.IO) {
            return@withContext bpDao.delete(entity)
        }
}