package com.app.body_manage.data.entity

import androidx.core.net.toUri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.app.body_manage.data.model.BodyMeasure
import com.app.body_manage.data.model.BodyPhoto
import com.app.body_manage.data.model.Photo
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

fun PhotoEntity.toModel() = BodyPhoto(
    id = Photo.Id(id),
    bodyMeasureId = BodyMeasure.Id(bodyMeasureId),
    uri = photoUri.toUri(),
)
