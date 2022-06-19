package com.app.body_manage.data.repository

import com.app.body_manage.data.dao.BodyMeasurePhotoDao

class BodyMeasurePhotoRepository(private val bpDao: BodyMeasurePhotoDao) {
    suspend fun selectPhotosByDate(): Map<String, List<String>> {
        return bpDao.selectPhotosByDate()
    }
}