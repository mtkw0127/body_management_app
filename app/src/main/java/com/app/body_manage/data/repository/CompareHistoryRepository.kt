package com.app.body_manage.data.repository

import com.app.body_manage.data.dao.ComparePhotoHistoryDao
import com.app.body_manage.data.entity.ComparePhotoHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CompareHistoryRepository(private val bpDao: ComparePhotoHistoryDao) {
    suspend fun saveHistory(compareHistoryEntity: ComparePhotoHistoryEntity) =
        withContext(Dispatchers.IO) {
            return@withContext bpDao.saveHistory(compareHistoryEntity)
        }

    suspend fun selectAll() =
        withContext(Dispatchers.IO) {
            return@withContext bpDao.selectAll()
        }

    suspend fun delete(compareHistoryId: Int) =
        withContext(Dispatchers.IO) {
            return@withContext bpDao.delete(compareHistoryId)
        }
}