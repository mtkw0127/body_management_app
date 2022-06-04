package com.app.calendar.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.calendar.model.PhotoEntity

@Dao
interface PhotoDao {
    @Insert
    suspend fun insert(photos: List<PhotoEntity>): List<Long>

    @Query("SELECT * FROM photos WHERE body_measure_id = :bodyMeasureId")
    suspend fun selectPhotos(bodyMeasureId: Int): List<PhotoEntity>

    @Query("DELETE FROM photos WHERE body_measure_id = :bodyMeasureId")
    suspend fun deletePhotos(bodyMeasureId: Int)

}