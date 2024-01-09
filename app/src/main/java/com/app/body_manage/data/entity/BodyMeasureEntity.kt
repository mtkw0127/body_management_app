package com.app.body_manage.data.entity

import androidx.core.net.toUri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.body_manage.data.model.BodyMeasure
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "bodyMeasures")
data class BodyMeasureEntity(
    @PrimaryKey(autoGenerate = true) var ui: Int,
    @ColumnInfo(name = "calendar_date") val calendarDate: LocalDate, // 登録日
    @ColumnInfo(name = "capture_time") val capturedTime: LocalDateTime, // 検索用
    @ColumnInfo(name = "weight") val weight: Float,
    @ColumnInfo(name = "fat") val fat: Float,
    @ColumnInfo(name = "memo") val memo: String,
    @ColumnInfo(name = "photo_uri") var photoUri: String?,
    @ColumnInfo(name = "tall") val tall: Float?,
) : Serializable

fun BodyMeasureEntity.toModel(): BodyMeasure =
    BodyMeasure(
        id = BodyMeasure.Id(this.ui),
        time = this.capturedTime,
        weight = this.weight,
        fat = this.fat,
        memo = this.memo,
        photoUri = this.photoUri?.toUri(),
        tall = this.tall,
    )
