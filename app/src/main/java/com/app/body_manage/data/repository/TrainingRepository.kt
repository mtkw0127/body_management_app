package com.app.body_manage.data.repository

import com.app.body_manage.data.dao.TrainingDao
import com.app.body_manage.data.model.Training
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.data.model.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class TrainingRepository(
    private val trainingDao: TrainingDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    // その日のトレーニングを保存する
    suspend fun saveTraining(
        training: Training,
    ) {
//        // その日のトレーニングを作る
//        val trainingId = trainingDao.insertTraining(training.toEntity())
//
//        // そのトレーニングに紐づくメニューに紐づくトレーニング実績を登録する
//        training.menus.forEach { trainingMenu ->
//            for (set in trainingMenu.sets) {
//                val entity =
//                    set.toEntity(
//                        trainingId = trainingId,
//                        trainingMenuId = trainingMenu.id.value,
//                        eventIndex = trainingMenu.eventIndex,
//                    )
//                trainingDao.insertTrainingSet(entity)
//            }
//        }
    }

    // その日のトレーニングを取得する
    suspend fun getTrainingsByDate(date: LocalDate): List<Training> {
//        // その日に行ったトレーニングを全て取得する
//        val trainings = trainingDao.getTrainingsByDate(date)
//        return trainings.map { training ->
//            val trainingId = training.id
//            // そのトレーニングに紐づくトレーニングセットを取得する（ベンチプレスの1セット目、2セット目、etc...）
//            val trainingSets = trainingDao.getTrainingSetsByTrainingId(trainingId)
//
//            // そのトレーニングセットをグルーピングする
//            trainingSets.groupBy { it.trainingMenuId }
//
//            val trainingMenus = trainingSets.map { trainingSet ->
//                // トレーニングメニューを取得する（ベンチプレス、スクワット、etc...）
//                val trainingMenu = trainingDao.getTrainingMenu(trainingSet.trainingMenuId)
//                // トレーニングメニューのタイプをEntityからModelに変換する
//                val type = TrainingMenu.Type.entries.find { it.index == trainingMenu.type }!!
//                trainingMenu.toModel(trainingSets.map { it.toModel(type) })
//            }
//            training.toModel(trainingMenus)
//            val menus = trainingDao.getTrainingMenuList().map { it.toModel() }
//            training.toModel(menus)
//        }
        return emptyList()
    }

    @Suppress("all")
    suspend fun createTrainingMenu(
        trainingMenu: TrainingMenu,
    ) {
        trainingDao.insertTrainingMenu(trainingMenu.toEntity())
    }

    suspend fun getTrainingMenuList(): List<TrainingMenu> = withContext(ioDispatcher) {
//        return@withContext trainingDao.getTrainingMenuList().map { it.toModel() }
        return@withContext emptyList()
    }
}
