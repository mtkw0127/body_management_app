package com.app.body_manage.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

// ある一回のトレーニングの１セットを表すEntity
@Entity(
    tableName = "training_sets",
)
data class TrainingSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "training_id") val trainingId: Long,
    @ColumnInfo(name = "training_menu_id") val trainingMenuId: Long,
    @ColumnInfo(name = "rep") val rep: Long, // レップ数
    @ColumnInfo(name = "weight") val weight: Long, // 重量 自重は基本0、加重で将来拡張用
) : Serializable
