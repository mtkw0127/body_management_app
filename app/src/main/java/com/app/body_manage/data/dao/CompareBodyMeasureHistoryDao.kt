package com.app.body_manage.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.app.body_manage.data.entity.CompareBodyMeasureHistoryEntity

@Dao
interface CompareBodyMeasureHistoryDao {
    @Insert
    suspend fun saveHistory(compareBodyMeasureHistory: CompareBodyMeasureHistoryEntity)

    @Query("SELECT * FROM compareBodyMeasureHistory")
    suspend fun selectAll(): List<CompareBodyMeasureHistoryEntity>

    @Delete
    suspend fun delete(compareBodyMeasureHistoryEntity: CompareBodyMeasureHistoryEntity)
}
