package com.app.body_manage.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_training_menu_sets")
data class TrainingTrainingMenuSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "training_id") val trainingId: Long, // 何月何日のトレーニングかを表す
    @ColumnInfo(name = "event_index") val eventIndex: Long, // 何種目かを表す
    @ColumnInfo(name = "training_menu_id") val trainingMenuId: Long, // その種目で行ったトレーニングメニューのID
    @ColumnInfo(name = "training_set_id") val trainingSetId: Long, // その種目で行ったトレーニングセットのID
)