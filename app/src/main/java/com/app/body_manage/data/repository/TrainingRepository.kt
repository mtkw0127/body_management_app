package com.app.body_manage.data.repository

import androidx.room.Transaction
import com.app.body_manage.data.dao.TrainingDao
import com.app.body_manage.data.entity.TrainingMenuEntity
import com.app.body_manage.data.entity.TrainingSetEntity
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
                val trainingSetId = when (trainingSet) {
                    is TrainingMenu.Set -> {
                        trainingDao.insertTrainingSet(trainingSet.toEntity(trainingMenu.eventIndex))
                    }

                    is TrainingMenu.CardioSet -> {
                        trainingDao.insertTrainingCardioSet(trainingSet.toEntity())
                    }
                }
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
                    val trainingMenuEntity =
                        trainingDao.getTrainingMenu(middleTableList.value.first().trainingMenuId)

                    val type =
                        checkNotNull(TrainingMenu.Type.entries.find { type -> type.index == trainingMenuEntity.type })

                    // 有酸素と筋トレのセットは別テーブルで管理しているため
                    when (type) {
                        TrainingMenu.Type.Cardio -> {
                            val trainingCardioSetEntities =
                                trainingDao.getTrainingCardioSetByIds(middleTableList.value.map { it.trainingSetId })

                            trainingMenuEntity.toModel(
                                sets = trainingCardioSetEntities.map { it.toModel() },
                                eventIndex = middleTableList.key, // キーが何種目かを表すため
                            )
                        }

                        else -> {
                            val trainingMenuSetEntities: List<TrainingSetEntity> =
                                trainingDao.getTrainingSetByIds(middleTableList.value.map { it.trainingSetId })

                            trainingMenuEntity.toModel(
                                sets = trainingMenuSetEntities.map { it.toModel(type) },
                                eventIndex = middleTableList.key, // キーが何種目かを表すため
                            )
                        }
                    }
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
        val cardioSetIds =
            training.menus.filter { it.type == TrainingMenu.Type.Cardio }.flatMap { it.sets }
                .map { set -> set.id.value }
        val muscleSetIds =
            training.menus.filter { it.type != TrainingMenu.Type.Cardio }.flatMap { it.sets }
                .map { set -> set.id.value }

        // トレーニングセットレコード削除
        trainingDao.deleteTrainingCardioSet(cardioSetIds)
        trainingDao.deleteTrainingSet(muscleSetIds)
        // 中間テーブル削除
        trainingDao.deleteTrainingTrainingSet(training.id.value)
        // トレーニングを削除
        trainingDao.deleteTraining(training.id.value)
    }

    suspend fun updateTraining(training: Training) = withContext(ioDispatcher) {
        // 古いデータを削除
        deleteTraining(training)

        // 新しい記録を登録する
        saveTraining(training.copy(id = Training.NEW_ID))
    }

    suspend fun saveMenu(menu: TrainingMenuEntity) {
        trainingDao.insertTrainingMenu(menu)
    }

    suspend fun updateMenu(menu: TrainingMenuEntity) {
        trainingDao.updateTrainingMenu(menu)
    }

    suspend fun getTrainingCount(): Int {
        return trainingDao.getCount()
    }
}
