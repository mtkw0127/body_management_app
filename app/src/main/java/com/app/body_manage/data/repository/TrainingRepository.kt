package com.app.body_manage.data.repository

import com.app.body_manage.data.dao.TrainingDao
import com.app.body_manage.data.model.Training

class TrainingRepository(
    private val trainingDao: TrainingDao
) {
    @Suppress("all")
    suspend fun saveTraining(training: Training) {
        trainingDao.insert()
    }
}
