package com.app.body_manage.data.repository

import com.app.body_manage.data.dao.ComparePhotoHistoryDao
import com.app.body_manage.data.entity.ComparePhotoHistoryEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CompareHistoryRepository(
    private val bpDao: ComparePhotoHistoryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend fun saveHistory(compareHistoryEntity: ComparePhotoHistoryEntity) =
        withContext(ioDispatcher) {
            return@withContext bpDao.saveHistory(compareHistoryEntity)
        }

    suspend fun selectAll() =
        withContext(ioDispatcher) {
            return@withContext bpDao.selectAll()
        }

    suspend fun delete(compareHistoryId: Int) =
        withContext(ioDispatcher) {
            return@withContext bpDao.delete(compareHistoryId)
        }
}
