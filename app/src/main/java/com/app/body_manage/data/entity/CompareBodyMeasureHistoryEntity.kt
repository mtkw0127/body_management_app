package com.app.body_manage.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "compareBodyMeasureHistory",
)
data class CompareBodyMeasureHistoryEntity(
    @PrimaryKey(autoGenerate = true) var ui: Int,
    @ColumnInfo(name = "beforeBodyMeasureId") val beforeBodyMeasureId: Int,
    @ColumnInfo(name = "afterBodyMeasureId") val afterBodyMeasureId: Int,
) : Serializable