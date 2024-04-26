package com.app.body_manage.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.body_manage.data.model.TrainingMenu
import java.io.Serializable

// トレーニングの名前や種類を表すエンティティ
@Entity(tableName = "training_menus")
data class TrainingMenuEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "part") val part: Int, // 10: 胸, 20: 肩, 30: 背中, 40: 腕, 50: 腹筋, 60: 尻, 70: 脚, 999: その他
    @ColumnInfo(name = "memo") val memo: String,
    @ColumnInfo(name = "type") val type: Int, // 1: マシン, 2: フリーウェイト, 3: 自重
) : Serializable

fun TrainingMenuEntity.toModel(
    sets: List<TrainingMenu.Set>,
    eventIndex: Long,
): TrainingMenu {
    return TrainingMenu(
        id = TrainingMenu.Id(id),
        name = name,
        part = checkNotNull(TrainingMenu.Part.entries.find { it.index == part }),
        memo = memo,
        sets = sets,
        type = checkNotNull(TrainingMenu.Type.entries.first { it.index == type }),
        eventIndex = eventIndex,
    )
}
