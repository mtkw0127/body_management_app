package com.app.body_manage.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "photos",
    foreignKeys = [ForeignKey(
        entity = BodyMeasureEntity::class,
        parentColumns = arrayOf("ui"),
        childColumns = arrayOf("body_measure_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
class PhotoEntity(
    @PrimaryKey(autoGenerate = true) var ui: Int,
    @ColumnInfo(name = "body_measure_id", index = true) val bodyMeasureId: Int,
    @ColumnInfo(name = "photo_uri") val photoUri: String
) : Serializable