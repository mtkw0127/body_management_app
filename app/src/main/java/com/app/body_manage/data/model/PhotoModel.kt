package com.app.body_manage.data.model

import android.net.Uri
import com.app.body_manage.data.entity.PhotoEntity
import java.io.Serializable

data class PhotoModel(
    val id: Id = Id(0),
    val bodyMeasureId: BodyMeasureModel.Id? = null,
    val uri: Uri,
) {
    @JvmInline
    value class Id(val id: Int) : Serializable
}

fun PhotoModel.toEntity(bodyMeasureId: BodyMeasureModel.Id? = null) = PhotoEntity(
    id = this@toEntity.id.id,
    bodyMeasureId = checkNotNull(this@toEntity.bodyMeasureId?.value ?: bodyMeasureId?.value),
    photoUri = this@toEntity.uri.toString(),
)
