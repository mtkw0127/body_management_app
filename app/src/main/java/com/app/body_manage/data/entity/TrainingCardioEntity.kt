package com.app.body_manage.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.body_manage.data.model.TrainingMenu
import java.io.Serializable

// ある一回のトレーニング（筋トレ系）の１セットを表すEntity
@Entity(tableName = "training_cardio_sets")
data class TrainingCardioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "distance") val distance: Float, // 距離
    @ColumnInfo(name = "minutes") val minutes: Long, // 時間
) : Serializable

fun TrainingCardioEntity.toModel(): TrainingMenu.CardioSet {
    return TrainingMenu.CardioSet(
        id = TrainingMenu.TrainingInterface.Id(this.id),
        distance = this.distance,
        minutes = this.minutes,
    )
}
