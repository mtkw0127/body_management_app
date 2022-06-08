package com.app.body_manage.repository

import com.app.body_manage.dao.BodyMeasurePhotoDao
import com.app.body_manage.model.BodyMeasureEntity
import com.app.body_manage.model.PhotoEntity

class BodyMeasurePhotoRepository(private val bpDao: BodyMeasurePhotoDao) {
    suspend fun selectPhotosByDate(): Map<BodyMeasureEntity, List<PhotoEntity>> {
        return bpDao.selectPhotosByDate()
    }
}