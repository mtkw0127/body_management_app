package com.app.body_manage.data.entity

import androidx.core.net.toUri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.app.body_manage.data.model.BodyMeasureModel
import com.app.body_manage.data.model.PhotoModel
import java.io.Serializable

@Entity(
    tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = BodyMeasureEntity::class,
            parentColumns = arrayOf("ui"),
            childColumns = arrayOf("body_measure_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "body_measure_id", index = true) val bodyMeasureId: Int,
    @ColumnInfo(name = "photo_uri") val photoUri: String
) : Serializable

fun PhotoEntity.toModel() = PhotoModel(
    id = PhotoModel.Id(id),
    bodyMeasureId = BodyMeasureModel.Id(bodyMeasureId),
    uri = photoUri.toUri(),
)
