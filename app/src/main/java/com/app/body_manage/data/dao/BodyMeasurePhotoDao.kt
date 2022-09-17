package com.app.body_manage.data.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.MapInfo
import androidx.room.Query
import java.time.LocalDate

@Dao
interface BodyMeasurePhotoDao {
    @MapInfo(keyColumn = "calendar_date")
    @Query("SELECT bodyMeasures.calendar_date as calendar_date, photos.ui as photo_id,  photos.photo_uri as photo_uri from photos INNER JOIN bodyMeasures ON bodyMeasures.ui = photos.body_measure_id ORDER BY bodyMeasures.capture_date asc")
    suspend fun selectPhotosByDate(): Map<String, List<PhotoData>>

    @MapInfo(keyColumn = "calendar_date")
    @Query("SELECT bodyMeasures.calendar_date as calendar_date, photos.ui as photo_id,  photos.photo_uri as photo_uri from photos INNER JOIN bodyMeasures ON bodyMeasures.ui = photos.body_measure_id WHERE bodyMeasures.calendar_date = :date ORDER BY bodyMeasures.capture_date asc")
    suspend fun selectPhotosByDateOnlyDate(date: LocalDate): Map<String, List<PhotoData>>

    data class PhotoData(
        @ColumnInfo(name = "photo_id") val photoId: Int,
        @ColumnInfo(name = "photo_uri") val photoUri: String
    )
}