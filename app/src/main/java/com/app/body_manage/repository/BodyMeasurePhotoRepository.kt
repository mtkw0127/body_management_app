package com.app.body_manage.repository

import com.app.body_manage.dao.BodyMeasurePhotoDao
import com.app.body_manage.dao.BodyMeasurePhotoDao.BodyMeasurePhoto

class BodyMeasurePhotoRepository(private val bpDao: BodyMeasurePhotoDao) {
    suspend fun selectPhotosByDate(): List<BodyMeasurePhoto> {
        return bpDao.selectPhotosByDate()
    }
}