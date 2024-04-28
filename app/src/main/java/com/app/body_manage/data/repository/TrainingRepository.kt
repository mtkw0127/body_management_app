package com.app.body_manage.data.repository

import androidx.room.Transaction
import com.app.body_manage.data.dao.TrainingDao
import com.app.body_manage.data.entity.TrainingTrainingMenuSetEntity
import com.app.body_manage.data.entity.toModel
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
    @Transaction
    suspend fun saveTraining(
        training: Training,
    ) {
        // その日のトレーニングを作る
        val trainingId = trainingDao.insertTraining(training.toEntity())

        training.menus.forEach { trainingMenu ->
            trainingMenu.sets.forEach { trainingSet ->
                // そのトレーニングの１セットを登録する
                val trainingSetId =
                    trainingDao.insertTrainingSet(trainingSet.toEntity(trainingMenu.eventIndex))

                // 中間テーブルを登録する
                val middleTableEntity = TrainingTrainingMenuSetEntity(
                    id = 0,
                    trainingId = trainingId,
                    eventIndex = trainingMenu.eventIndex,
                    trainingMenuId = trainingMenu.id.value,
                    trainingSetId = trainingSetId,
                )

                trainingDao.insertTrainingTrainingMenuSet(middleTableEntity)
            }
        }
    }

    // その日のトレーニングを取得する
    suspend fun getTrainingsByDate(date: LocalDate): List<Training> {
        // その日に行ったトレーニングを全て取得する
        val trainings = trainingDao.getTrainingsByDate(date)
        return trainings.map { training ->
            val trainingId = training.id
            // そのトレーニングに紐づくトレーニングセットを取得する（ベンチプレスの1セット目、2セット目、etc...）
            val middleTable = trainingDao.getTrainingTrainingMenuSetById(trainingId)

            // 何種目やったのかを取得する
            val trainingMenus = middleTable
                .sortedBy { it.eventIndex }
                .groupBy { it.eventIndex }
                .map { middleTableList ->
                    val trainingMenuSetEntities =
                        trainingDao.getTrainingSetByIds(middleTableList.value.map { it.trainingSetId })

                    val trainingMenuEntity =
                        trainingDao.getTrainingMenu(middleTableList.value.first().trainingMenuId)

                    val type =
                        checkNotNull(TrainingMenu.Type.entries.find { type -> type.index == trainingMenuEntity.type })

                    trainingMenuEntity.toModel(
                        sets = trainingMenuSetEntities.map { it.toModel(type) }, // セット数
                        eventIndex = middleTableList.key, // キーが何種目かを表すため
                    )
                }

            training.toModel(trainingMenus)
        }
    }

    suspend fun getJustTrainingMenuList(): List<TrainingMenu> = withContext(ioDispatcher) {
        return@withContext trainingDao.getTrainingMenuList().map {
            it.toModel(emptyList(), 0)
        }
    }

    suspend fun deleteTraining(training: Training) = withContext(ioDispatcher) {
        // 古い記録を削除する
        val setIds = training.menus.flatMap { it.sets }.map { set -> set.id.value }
        // トレーニングセットレコード削除
        trainingDao.deleteTrainingSet(setIds)
        // 中間テーブル削除
        trainingDao.deleteTrainingTrainingSet(training.id.value)
        // トレーニングを削除
        trainingDao.deleteTraining(training.id.value)
    }

    suspend fun updateTraining(training: Training) = withContext(ioDispatcher) {
        // 古いデータを削除
        deleteTraining(training)

        // 新しい記録を登録する
        if (training.menus.isNotEmpty()) {
            saveTraining(training.copy(id = Training.NEW_ID))
        }
    }
}
