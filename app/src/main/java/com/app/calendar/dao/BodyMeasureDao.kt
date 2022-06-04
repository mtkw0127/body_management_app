package com.app.calendar.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.app.calendar.model.BodyMeasureEntity
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface BodyMeasureDao {
    @Query("SELECT * FROM bodyMeasures WHERE calendar_date = :calendarDate ORDER BY capture_time ASC")
    suspend fun getTrainingEntityListByDate(calendarDate: LocalDate): List<BodyMeasureEntity>

    @Query("SELECT * FROM bodyMeasures WHERE capture_time = :captureTime")
    suspend fun getTrainingEntityByLocalDateTime(captureTime: LocalDateTime): List<BodyMeasureEntity>

    @Insert
    suspend fun insert(bodyMeasureEntity: BodyMeasureEntity): Long

    @Update
    suspend fun update(bodyMeasureEntity: BodyMeasureEntity): Int
}