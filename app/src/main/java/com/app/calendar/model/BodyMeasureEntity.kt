package com.app.calendar.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
data class BodyMeasureEntity(
    @PrimaryKey(autoGenerate = true) val ui: Int,
    @ColumnInfo(name = "calendar_date") val calendarDate: LocalDate,
    @ColumnInfo(name = "capture_date") val capturedDate: LocalDate,
    @ColumnInfo(name = "capture_time") val capturedTime: LocalDateTime,
    @ColumnInfo(name = "weight") val weight:Float,
    @ColumnInfo(name = "fat_fate") val fatRate:Float,
    @ColumnInfo(name = "photo_uri") val photoUri:String?
    ): Serializable