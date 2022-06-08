package com.app.body_manage.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query
import java.time.LocalDate

@Dao
interface BodyMeasurePhotoDao {
    @Query("SELECT bodyMeasures.ui as bid, bodyMeasures.calendar_date as c_date, photos.ui as pid, photos.photo_uri as photo_uri from photos INNER JOIN bodyMeasures ON bodyMeasures.ui = photos.body_measure_id GROUP BY bodyMeasures.capture_date ORDER BY bodyMeasures.capture_date asc")
    suspend fun selectPhotosByDate(): List<BodyMeasurePhoto>

    data class BodyMeasurePhoto(
        @ColumnInfo(name = "bid") val bodyMeasureId: Int,
        @ColumnInfo(name = "pid") val photoId: Int,
        @ColumnInfo(name = "c_date") val calendarDate: LocalDate,
        @ColumnInfo(name = "photo_uri") val photoUri: String
    )
}