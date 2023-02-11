package com.app.body_manage.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDateTime

@Entity(
    tableName = "comparePhotoHistory",
)
data class ComparePhotoHistoryEntity(
    @PrimaryKey(autoGenerate = true) var ui: Int,
    @ColumnInfo(name = "beforePhotoId") val beforePhotoId: Int,
    @ColumnInfo(name = "afterPhotoId") val afterPhotoId: Int,
    @ColumnInfo(name = "createdAt") val createdAt: LocalDateTime,
) : Serializable