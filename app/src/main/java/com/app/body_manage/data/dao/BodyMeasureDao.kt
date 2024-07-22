package com.app.body_manage.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.app.body_manage.data.entity.BodyMeasureEntity
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface BodyMeasureDao {
    @Query(
        "SELECT * " +
            "FROM bodyMeasures " +
            "WHERE calendar_date = :calendarDate " +
            "ORDER BY capture_time ASC"
    )
    suspend fun getTrainingEntityListByDate(
        calendarDate: LocalDate
    ): List<BodyMeasureEntity>

    @Query(
        "SELECT ui, " +
            "calendar_date, " +
            "capture_time, " +
            "AVG(weight) as weight, " +
            "AVG(fat) as fat," +
            "memo " +
            "FROM bodyMeasures " +
            "WHERE calendar_date " +
            "BETWEEN :from AND :to " +
            "GROUP BY bodyMeasures.calendar_date " +
            "ORDER BY capture_time ASC"
    )
    suspend fun getTrainingEntityListBetweenGroupByDate(
        from: LocalDate,
        to: LocalDate,
    ): List<BodyMeasureEntity>

    @Query(
        "SELECT * " +
            "FROM bodyMeasures " +
            "WHERE calendar_date " +
            "BETWEEN :from AND :to " +
            "ORDER BY capture_time ASC"
    )
    suspend fun getTrainingEntityListBetween(
        from: LocalDate,
        to: LocalDate,
    ): List<BodyMeasureEntity>

    @Query(
        "SELECT " +
            "ui, " +
            "calendar_date, " +
            "capture_time, " +
            "AVG(weight) as weight, " +
            "AVG(fat) as fat, " +
            "memo, " +
            "photo_uri, " +
            "tall " +
            "FROM bodyMeasures GROUP BY bodyMeasures.calendar_date"
    )
    suspend fun getTrainingEntityListAll(): List<BodyMeasureEntity>

    @Query("UPDATE bodyMeasures SET tall = :tall WHERE calendar_date = :calendarDate")
    suspend fun updateTallByDate(tall: Float, calendarDate: LocalDate): Int

    @Query("SELECT * FROM bodyMeasures WHERE capture_time = :captureTime")
    suspend fun getTrainingEntityByLocalDateTime(captureTime: LocalDateTime): List<BodyMeasureEntity>

    @Query("DELETE FROM bodyMeasures WHERE capture_time = :captureTime")
    suspend fun deleteBodyMeasure(captureTime: LocalDateTime): Int

    @Insert
    suspend fun insert(bodyMeasureEntity: BodyMeasureEntity): Long

    @Update
    suspend fun update(bodyMeasureEntity: BodyMeasureEntity): Int

    @Query("SELECT * FROM bodyMeasures WHERE ui = :bodyMeasureId")
    suspend fun fetch(bodyMeasureId: Int): BodyMeasureEntity

    @Query("SELECT * FROM bodyMeasures ORDER BY capture_time DESC")
    suspend fun getLast(): BodyMeasureEntity?

    @Query("SELECT * FROM bodyMeasures ORDER BY capture_time ASC")
    suspend fun getFirst(): BodyMeasureEntity?
}
