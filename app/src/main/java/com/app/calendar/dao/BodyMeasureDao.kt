package com.app.calendar.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.app.calendar.model.BodyMeasureEntity
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMeasureDao {
    @Query("SELECT * FROM bodyMeasureEntity")
    fun getAll(): Flow<List<BodyMeasureEntity>>

    @Query("SELECT * FROM bodyMeasureEntity WHERE calendar_date = :calendarDate ORDER BY capture_time ASC")
    fun getTrainingEntityListByDate(calendarDate: LocalDate): Flow<List<BodyMeasureEntity>>

    @Query("SELECT * FROM BodyMeasureEntity WHERE capture_time = :captureTime")
    fun getTrainingEntityByLocalDateTime(captureTime: LocalDateTime): Flow<BodyMeasureEntity>

    @Insert
    suspend fun insert(bodyMeasureEntity: BodyMeasureEntity)

    @Update
    suspend fun update(bodyMeasureEntity: BodyMeasureEntity): Int
}