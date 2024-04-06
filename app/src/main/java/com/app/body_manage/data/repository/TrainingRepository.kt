package com.app.body_manage.data.repository

import com.app.body_manage.data.dao.TrainingDao
import com.app.body_manage.data.entity.TrainingSetEntity
import com.app.body_manage.data.model.Training
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.data.model.toEntity

class TrainingRepository(
    private val trainingDao: TrainingDao
) {
    @Suppress("all")
    suspend fun saveTraining(
        training: Training,
        trainingSetEntityList: List<TrainingSetEntity>,
    ) {
        // その日のトレーニングを作る
        trainingDao.insertTraining(training.toEntity())

        // トレーニングを登録する
        trainingSetEntityList.forEach { trainingSetEntity ->
            trainingDao.insertTraingSet(trainingSetEntity)
        }
    }

    @Suppress("all")
    suspend fun createTrainingMenu(
        trainingMenu: TrainingMenu,
    ) {
        trainingDao.insertTrainingMenu(trainingMenu.toEntity())
    }
}
