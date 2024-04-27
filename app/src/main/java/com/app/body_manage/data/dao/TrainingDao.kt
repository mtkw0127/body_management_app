package com.app.body_manage.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.body_manage.data.entity.TrainingEntity
import com.app.body_manage.data.entity.TrainingMenuEntity
import com.app.body_manage.data.entity.TrainingSetEntity
import com.app.body_manage.data.entity.TrainingTrainingMenuSetEntity
import java.time.LocalDate

@Dao
interface TrainingDao {
    // ある１日のトレーニングを作る
    @Insert
    suspend fun insertTraining(training: TrainingEntity): Long

    @Insert
    suspend fun insertTrainingTrainingMenuSet(trainingTrainingMenuSetEntity: TrainingTrainingMenuSetEntity)

    @Insert
    suspend fun insertTrainingSet(trainingSet: TrainingSetEntity): Long

    @Query("SELECT * FROM trainings WHERE date = :date")
    suspend fun getTrainingsByDate(date: LocalDate): List<TrainingEntity>

    @Query("SELECT * FROM training_training_menu_sets WHERE training_id = :id ")
    suspend fun getTrainingTrainingMenuSetById(id: Long): List<TrainingTrainingMenuSetEntity>

    @Query("SELECT * FROM training_sets WHERE id IN (:ids)")
    suspend fun getTrainingSetByIds(ids: List<Long>): List<TrainingSetEntity>


    @Query("SELECT * FROM training_menus WHERE id = :trainingMenuId")
    suspend fun getTrainingMenu(trainingMenuId: Long): TrainingMenuEntity

    @Query("SELECT * FROM training_menus")
    suspend fun getTrainingMenuList(): List<TrainingMenuEntity>
}
