package com.app.body_manage.data.model

import android.net.Uri
import com.app.body_manage.data.entity.PhotoEntity

data class BodyPhoto(
    override val id: Photo.Id = Photo.Id(0),
    val bodyMeasureId: BodyMeasure.Id? = null,
    override val uri: Uri,
) : Photo

fun BodyPhoto.toEntity(bodyMeasureId: BodyMeasure.Id? = null) = PhotoEntity(
    id = this@toEntity.id.value,
    bodyMeasureId = checkNotNull(this@toEntity.bodyMeasureId?.value ?: bodyMeasureId?.value),
    photoUri = this@toEntity.uri.toString(),
)
