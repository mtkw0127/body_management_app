package com.app.body_manage.data.model

import com.app.body_manage.data.entity.TrainingEntity
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class Training(
    val id: Id,
    val date: LocalDate,
    override val time: LocalDateTime,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val menus: List<TrainingMenu>,
    val memo: String,
    val createdAt: LocalDate,
) : Measure, Serializable {
    data class Id(val value: Long) : Serializable

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
        createdAt = this.createdAt,
    )
}

fun createSampleTraining(): Training = Training(
    id = Training.NEW_ID,
    date = LocalDate.now(),
    createdAt = LocalDate.now(),
    startTime = LocalTime.now(),
    endTime = LocalTime.now(),
    memo = "",
    menus = emptyList(),
    time = LocalDateTime.now(),
)
