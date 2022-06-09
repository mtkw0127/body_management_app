package com.app.body_manage.repository

import com.app.body_manage.dao.BodyMeasurePhotoDao

class BodyMeasurePhotoRepository(private val bpDao: BodyMeasurePhotoDao) {
    suspend fun selectPhotosByDate(): Map<String, List<String>> {
        return bpDao.selectPhotosByDate()
    }
}