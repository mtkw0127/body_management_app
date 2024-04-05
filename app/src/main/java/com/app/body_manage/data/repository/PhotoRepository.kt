package com.app.body_manage.data.repository

import com.app.body_manage.data.dao.PhotoDao
import com.app.body_manage.data.entity.toModel
import com.app.body_manage.data.model.BodyMeasure
import com.app.body_manage.data.model.BodyPhoto
import com.app.body_manage.data.model.Photo
import com.app.body_manage.data.model.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotoRepository(
    private val photoDao: PhotoDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend fun insert(photoModels: List<BodyPhoto>): List<Long> {
        val entities = photoModels.map { it.toEntity() }
        return photoDao.insert(entities)
    }

    suspend fun selectPhotos(bodyMeasureId: BodyMeasure.Id): List<BodyPhoto> {
        return photoDao.selectPhotos(bodyMeasureId.value).map { it.toModel() }
    }

    suspend fun selectPhoto(photoId: Photo.Id): BodyPhoto =
        withContext(ioDispatcher) {
            return@withContext photoDao.selectPhoto(photoId = photoId.value).toModel()
        }

    suspend fun deletePhotos(bodyMeasureId: BodyMeasure.Id) {
        return photoDao.deletePhotos(bodyMeasureId.value)
    }
}
