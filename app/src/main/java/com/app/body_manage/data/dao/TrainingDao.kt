package com.app.body_manage.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
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
    suspend fun insertTrainingSet(trainingSet: TrainingSetEntity)

    @Query("SELECT * FROM training_menus")
    suspend fun getTrainingMenuList(): List<TrainingMenuEntity>
}
