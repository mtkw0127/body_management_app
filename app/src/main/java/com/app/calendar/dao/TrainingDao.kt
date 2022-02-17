package com.app.calendar.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.calendar.model.TrainingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {
    @Query("SELECT * FROM trainingEntity")
    fun getAll(): Flow<List<TrainingEntity>>

    @Insert
    suspend fun insert(trainingEntity: TrainingEntity)
}