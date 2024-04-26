package com.app.body_manage.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.body_manage.data.model.Training
import com.app.body_manage.data.model.TrainingMenu
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime

// ある一回のトレーニングを保存するEntity
@Entity(tableName = "trainings")
data class TrainingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "start_time") val startTime: LocalTime,
    @ColumnInfo(name = "end_time") val endTime: LocalTime,
    @ColumnInfo(name = "memo") val memo: String,
    @ColumnInfo(name = "created_at") val createdAt: LocalDate,
) : Serializable

fun TrainingEntity.toModel(menus: List<TrainingMenu>): Training {
    return Training(
        id = Training.Id(this.id),
        date = this.date,
        startTime = this.startTime,
        endTime = this.endTime,
        menus = menus,
        memo = this.memo,
        createdAt = this.createdAt,
    )
}
