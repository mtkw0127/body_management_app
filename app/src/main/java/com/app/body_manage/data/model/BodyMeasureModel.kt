package com.app.body_manage.data.model

import android.net.Uri
import com.app.body_manage.data.entity.BodyMeasureEntity
import java.time.LocalDateTime

data class BodyMeasureModel(
    val id: Id,
    val capturedLocalDateTime: LocalDateTime,
    val weight: Float,
    val fat: Float,
    val photoUri: Uri?,
    val tall: Float?,
) {
    @JvmInline
    value class Id(val value: Int)
}

fun BodyMeasureModel.toEntity() = BodyMeasureEntity(
    ui = id.value,
    calendarDate = capturedLocalDateTime.toLocalDate(),
    capturedTime = capturedLocalDateTime,
    weight = weight,
    fat = fat,
    photoUri = photoUri.toString(),
    tall = tall,
)
