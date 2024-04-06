package com.app.body_manage.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.app.body_manage.data.entity.TrainingEntity
import com.app.body_manage.data.entity.TrainingMenuEntity
import com.app.body_manage.data.entity.TrainingSetEntity

@Dao
interface TrainingDao {
    // ある１日のトレーニングを作る
    @Insert
    suspend fun insertTraining(training: TrainingEntity)

    // トレーニングメニューを作る
    @Insert
    suspend fun insertTrainingMenu(trainingMenu: TrainingMenuEntity)

    // トレーニングした実績を登録する
    @Insert
    suspend fun insertTraingSet(trainingSet: TrainingSetEntity)
}
