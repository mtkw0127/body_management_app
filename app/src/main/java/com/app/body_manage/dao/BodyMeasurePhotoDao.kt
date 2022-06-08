package com.app.body_manage.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query
import com.app.body_manage.model.BodyMeasureEntity
import com.app.body_manage.model.PhotoEntity
import java.time.LocalDate

@Dao
interface BodyMeasurePhotoDao {
    @Query("SELECT * from photos INNER JOIN bodyMeasures ON bodyMeasures.ui = photos.body_measure_id ORDER BY bodyMeasures.capture_date asc")
    suspend fun selectPhotosByDate(): Map<BodyMeasureEntity, List<PhotoEntity>>

    data class BodyMeasurePhoto(
        @ColumnInfo(name = "bid") val bodyMeasureId: Int,
        @ColumnInfo(name = "pid") val photoId: Int,
        @ColumnInfo(name = "c_date") val calendarDate: LocalDate,
        @ColumnInfo(name = "photo_uri") val photoUri: String
    )
}