package com.app.body_manage.data.model

import com.app.body_manage.data.entity.TrainingEntity
import java.time.LocalDate
import java.time.LocalTime

data class Training(
    val id: Id,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val menus: List<TrainingMenu>,
    val memo: String,
) {
    data class Id(val value: Long)

    companion object {
        val NEW_ID = Id(0)
    }
}

fun Training.toEntity(): TrainingEntity {
    return TrainingEntity(
        id = this.id.value,
        date = this.date,
        startTime = this.startTime,
        endTime = this.endTime,
        memo = this.memo,
    )
}
