package com.app.body_manage.data.repository

import com.app.body_manage.data.dao.PhotoDao
import com.app.body_manage.data.entity.toModel
import com.app.body_manage.data.model.BodyMeasure
import com.app.body_manage.data.model.PhotoModel
import com.app.body_manage.data.model.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotoRepository(private val photoDao: PhotoDao) {
    suspend fun insert(photoModels: List<PhotoModel>): List<Long> {
        val entities = photoModels.map { it.toEntity() }
        return photoDao.insert(entities)
    }

    suspend fun selectPhotos(bodyMeasureId: BodyMeasure.Id): List<PhotoModel> {
        return photoDao.selectPhotos(bodyMeasureId.value).map { it.toModel() }
    }

    suspend fun selectPhoto(photoId: PhotoModel.Id): PhotoModel =
        withContext(Dispatchers.IO) {
            return@withContext photoDao.selectPhoto(photoId = photoId.id).toModel()
        }

    suspend fun deletePhotos(bodyMeasureId: BodyMeasure.Id) {
        return photoDao.deletePhotos(bodyMeasureId.value)
    }
}
