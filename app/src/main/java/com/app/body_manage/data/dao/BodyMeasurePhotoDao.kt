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

    @Query("SELECT bodyMeasures.calendar_date from photos INNER JOIN bodyMeasures ON bodyMeasures.ui = photos.body_measure_id WHERE bodyMeasures.calendar_date BETWEEN :from AND :to ORDER BY bodyMeasures.capture_date asc")
    suspend fun selectHavePhotoDateList(from: LocalDate, to: LocalDate): List<LocalDate>

    @Query("SELECT bodyMeasures.calendar_date as calendar_date, bodyMeasures.weight as weight, bodyMeasures.fat as fat, photos.photo_uri as photo_uri from photos INNER JOIN bodyMeasures ON bodyMeasures.ui = photos.body_measure_id WHERE photos.ui = :photoId")
    suspend fun selectBodyMeasureByPhotoId(photoId: Int): BodyMeasure?

    data class PhotoData(
        @ColumnInfo(name = "photo_id") val photoId: Int,
        @ColumnInfo(name = "photo_uri") val photoUri: String
    )

    data class BodyMeasure(
        @ColumnInfo(name = "calendar_date") val calendarDate: LocalDate,
        @ColumnInfo(name = "weight") val weight: Float,
        @ColumnInfo(name = "fat") val fat: Float,
        @ColumnInfo(name = "photo_uri") val photoUri: String
    )
}
