package com.app.body_manage.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.MapInfo
import androidx.room.Query
import java.time.LocalDate

@Dao
interface BodyMeasurePhotoDao {
    @MapInfo(keyColumn = "calendar_date", valueColumn = "photo_uri")
    @Query("SELECT bodyMeasures.calendar_date as calendar_date, photos.photo_uri as photo_uri from photos INNER JOIN bodyMeasures ON bodyMeasures.ui = photos.body_measure_id ORDER BY bodyMeasures.capture_date asc")
    suspend fun selectPhotosByDate(): Map<String, List<String>>

    data class BodyMeasurePhoto(
        @ColumnInfo(name = "bid") val bodyMeasureId: Int,
        @ColumnInfo(name = "pid") val photoId: Int,
        @ColumnInfo(name = "c_date") val calendarDate: LocalDate,
        @ColumnInfo(name = "photo_uri") val photoUri: String
    )
}