package com.app.body_manage.data.dao

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface TrainingDao {
    @Insert
    suspend fun insert()
}
