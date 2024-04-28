package com.app.body_manage.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.body_manage.data.model.TrainingMenu
import java.io.Serializable

// ある一回のトレーニングの１セットを表すEntity
@Entity(tableName = "training_sets")
data class TrainingSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "set_index") val setIndex: Long, // 何セット目かを表す
    @ColumnInfo(name = "rep") val rep: Long, // レップ数
    @ColumnInfo(name = "weight") val weight: Long, // 重量 自重は基本0、加重で将来拡張用
) : Serializable

fun TrainingSetEntity.toModel(type: TrainingMenu.Type): TrainingMenu.Set {
    return when (type) {
        TrainingMenu.Type.MACHINE, TrainingMenu.Type.FREE -> TrainingMenu.WeightSet(
            id = TrainingMenu.Set.Id(this.id),
            setIndex = this.setIndex,
            number = this.rep,
            weight = this.weight,
        )

        TrainingMenu.Type.OWN_WEIGHT -> TrainingMenu.OwnWeightSet(
            id = TrainingMenu.Set.Id(this.id),
            setIndex = this.setIndex,
            number = this.rep,
        )
    }
}
