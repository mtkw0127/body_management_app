package com.app.calendar.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.calendar.model.BodyMeasureEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface BodyMeasureDao {
    @Query("SELECT * FROM bodyMeasureEntity")
    fun getAll(): Flow<List<BodyMeasureEntity>>

    @Query("SELECT * FROM bodyMeasureEntity WHERE calendar_date = :calendarDate ORDER BY capture_time ASC")
    fun getTrainingEntityListByDate(calendarDate: LocalDate): Flow<List<BodyMeasureEntity>>

    @Insert
    suspend fun insert(bodyMeasureEntity: BodyMeasureEntity)
}