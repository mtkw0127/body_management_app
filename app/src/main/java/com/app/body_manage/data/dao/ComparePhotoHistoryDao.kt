package com.app.body_manage.data.dao

import androidx.room.*
import com.app.body_manage.data.entity.ComparePhotoHistoryEntity
import java.time.LocalDate

@Dao
interface ComparePhotoHistoryDao {
    @Insert
    suspend fun saveHistory(compareBodyMeasureHistory: ComparePhotoHistoryEntity)

    @Query(
        "SELECT photos1.photo_uri as beforePhotoUri, "
                + "photos2.photo_uri as afterPhotoUri, "
                + "photos1.ui as beforePhotoId, "
                + "photos2.ui as afterPhotoId, "
                + "beforeBodyMeasure.weight as beforeWeight, "
                + "afterBodyMeasure.weight as afterWeight, "
                + "beforeBodyMeasure.calendar_date as beforeCalendarDate, "
                + "afterBodyMeasure.calendar_date as afterCalendarDate "
                + "FROM comparePhotoHistory "
                + "INNER JOIN photos as photos1 ON photos1.ui = comparePhotoHistory.beforePhotoId "
                + "INNER JOIN photos as photos2 ON photos2.ui = comparePhotoHistory.afterPhotoId "
                + "INNER JOIN bodyMeasures as beforeBodyMeasure ON photos1.body_measure_id = beforeBodyMeasure.ui "
                + "INNER JOIN bodyMeasures as afterBodyMeasure ON photos2.body_measure_id = afterBodyMeasure.ui "
    )
    suspend fun selectAll(): List<PhotoAndBodyMeasure>

    @Delete
    suspend fun delete(compareBodyMeasureHistoryEntity: ComparePhotoHistoryEntity)

    data class PhotoAndBodyMeasure(
        @ColumnInfo(name = "beforePhotoId") val beforePhotoId: Int,
        @ColumnInfo(name = "beforePhotoUri") val beforePhotoUri: String,
        @ColumnInfo(name = "beforeWeight") val beforeWeight: Float,
        @ColumnInfo(name = "beforeCalendarDate") val beforeCalendarDate: LocalDate,
        @ColumnInfo(name = "afterPhotoUri") val afterPhotoUri: String,
        @ColumnInfo(name = "afterPhotoId") val afterPhotoId: Int,
        @ColumnInfo(name = "afterWeight") val afterWeight: Float,
        @ColumnInfo(name = "afterCalendarDate") val afterCalendarDate: LocalDate,
    )
}
