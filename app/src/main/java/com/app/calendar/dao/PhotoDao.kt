package com.app.calendar.dao

import androidx.room.Dao
import androidx.room.Insert
import com.app.calendar.model.PhotoEntity

@Dao
interface PhotoDao {
    @Insert
    suspend fun insert(photos: List<PhotoEntity>): List<Long>

}