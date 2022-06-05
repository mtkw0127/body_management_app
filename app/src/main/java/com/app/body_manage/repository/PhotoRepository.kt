package com.app.body_manage.repository

import androidx.annotation.WorkerThread
import com.app.body_manage.dao.PhotoDao
import com.app.body_manage.model.PhotoEntity

class PhotoRepository(private val photoDao: PhotoDao) {

    @WorkerThread
    suspend fun insert(photoEntityList: List<PhotoEntity>): List<Long> {
        return photoDao.insert(photoEntityList)
    }

    suspend fun selectPhotos(bodyMeasureId: Int): List<PhotoEntity> {
        return photoDao.selectPhotos(bodyMeasureId)
    }

    suspend fun deletePhotos(bodyMeasureId: Int) {
        return photoDao.deletePhotos(bodyMeasureId)
    }
}