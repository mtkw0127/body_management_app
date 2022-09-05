package com.app.body_manage.data.repository

import androidx.annotation.WorkerThread
import com.app.body_manage.data.dao.PhotoDao
import com.app.body_manage.data.entity.PhotoEntity
import com.app.body_manage.data.entity.toModel
import com.app.body_manage.ui.measure.form.BodyMeasureEditFormViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotoRepository(private val photoDao: PhotoDao) {

    @WorkerThread
    suspend fun insert(photoEntityList: List<PhotoEntity>): List<Long> {
        return photoDao.insert(photoEntityList)
    }

    suspend fun selectPhotos(bodyMeasureId: Int): List<PhotoEntity> {
        return photoDao.selectPhotos(bodyMeasureId)
    }

    suspend fun selectPhoto(photoId: BodyMeasureEditFormViewModel.PhotoModel.Id): BodyMeasureEditFormViewModel.PhotoModel =
        withContext(Dispatchers.IO) {
            return@withContext photoDao.selectPhoto(photoId = photoId.id).toModel()
        }

    suspend fun deletePhotos(bodyMeasureId: Int) {
        return photoDao.deletePhotos(bodyMeasureId)
    }
}