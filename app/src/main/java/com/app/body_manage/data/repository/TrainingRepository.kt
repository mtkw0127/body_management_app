package com.app.body_manage.data.repository

import com.app.body_manage.data.dao.TrainingDao
import com.app.body_manage.data.entity.toModel
import com.app.body_manage.data.model.Training
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.data.model.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrainingRepository(
    private val trainingDao: TrainingDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    // その日のトレーニングを保存する
    suspend fun saveTraining(
        training: Training,
    ) {
        // その日のトレーニングを作る
        val trainingId = trainingDao.insertTraining(training.toEntity())

        // そのトレーニングに紐づくメニューに紐づくトレーニング実績を登録する
        training.menus.forEach { trainingMenu ->
            for (set in trainingMenu.sets) {
                val entity = set.toEntity(trainingId, trainingMenu.id.value)
                trainingDao.insertTrainingSet(entity)
            }
        }
    }

    @Suppress("all")
    suspend fun createTrainingMenu(
        trainingMenu: TrainingMenu,
    ) {
        trainingDao.insertTrainingMenu(trainingMenu.toEntity())
    }

    suspend fun getTrainingMenuList(): List<TrainingMenu> = withContext(ioDispatcher) {
        return@withContext trainingDao.getTrainingMenuList().map { it.toModel() }
    }
}
