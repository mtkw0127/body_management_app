package com.app.body_manage.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

// トレーニングの名前や種類を表すエンティティ
@Entity(
    tableName = "training_menus",
)
data class TrainingMenuEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "part") val part: Int,
    @ColumnInfo(name = "memo") val memo: String,
    @ColumnInfo(name = "type") val type: Int,
) : Serializable

