package com.app.body_manage.data.model

import androidx.annotation.StringRes
import com.app.body_manage.R
import com.app.body_manage.data.entity.TrainingMenuEntity
import com.app.body_manage.data.entity.TrainingSetEntity
import java.io.Serializable

data class TrainingMenu(
    val id: Id,
    val eventIndex: Long, // 何種目目かを表す
    val name: String,
    val part: Part,
    val memo: String,
    val sets: List<Set>, // MEMO：最新のセット数。履歴管理する。
    val type: Type,
) : Serializable {
    data class Id(val value: Long) : Serializable

    companion object {
        val ID_NEW = Id(0)
    }

    sealed interface Set {
        val id: Id
        val setIndex: Long
        val number: Long

        data class Id(val value: Long) : Serializable

        companion object {
            val ID_NEW = Id(0)
        }

        fun toEntity(setIndex: Long): TrainingSetEntity
    }

    // MEMO: 例えば60kgを10回上げるような感じ
    // マシン・フリーウェイト用のSet
    data class WeightSet(
        override val id: Set.Id,
        override val setIndex: Long,
        override val number: Long, // 実際の回数
        val weight: Long, // 実際の重量
    ) : Set, Serializable {
        override fun toEntity(
            setIndex: Long,
        ): TrainingSetEntity {
            return TrainingSetEntity(
                id = ID_NEW.value,
                setIndex = setIndex,
                rep = number,
                weight = weight,
            )
        }
    }

    // 自重用のSet
    data class OwnWeightSet(
        override val id: Set.Id,
        override val setIndex: Long,
        override val number: Long, // 実際の回数
    ) : Set, Serializable {
        override fun toEntity(
            setIndex: Long,
        ): TrainingSetEntity {
            return TrainingSetEntity(
                id = ID_NEW.value,
                setIndex = setIndex,// 何セット目かを表す
                rep = number,
                weight = 0,
            )
        }
    }

    enum class Type(val index: Int, @StringRes val nameStringRes: Int) {
        MACHINE(
            1,
            R.string.label_machine
        ),
        FREE(
            2,
            R.string.label_free
        ),
        OWN_WEIGHT(
            3,
            R.string.label_own_weight
        )
    }

    enum class Part(
        val index: Int,
        @StringRes val nameStringResourceId: Int,
    ) {
        // 胸
        CHEST(10, R.string.label_type_chest),

        // 肩
        SHOULDER(20, R.string.label_type_shoulder),

        // 背中
        BACK(30, R.string.label_type_back),

        // 腕
        ARM(40, R.string.label_type_arm),

        // 腹部
        ABDOMINAL(50, R.string.label_type_abdominal),

        // 尻
        HIP(60, R.string.label_type_hip),

        // 脚
        LEG(70, R.string.label_type_leg),

        // その他
        ELSE(999, R.string.label_type_else),
    }
}

fun TrainingMenu.toEntity(): TrainingMenuEntity {
    return TrainingMenuEntity(
        id = this.id.value,
        name = this.name,
        part = this.part.index,
        memo = this.memo,
        type = this.type.index,
    )
}

fun createSampleTrainingMenu(eventIndex: Long): TrainingMenu {
    return TrainingMenu(
        id = TrainingMenu.Id(0),
        name = "ダンベルベンチプレス",
        part = TrainingMenu.Part.ARM,
        memo = "メモメモ".repeat(4),
        sets = List(5) { index ->
            TrainingMenu.WeightSet(
                id = TrainingMenu.Set.Id(0),
                setIndex = index.toLong() + 1,
                number = 10,
                weight = 55,
            )
        },
        eventIndex = eventIndex,
        type = TrainingMenu.Type.MACHINE,
    )
}

fun createSampleOwnWeightTrainingMenu(eventIndex: Long): TrainingMenu {
    return TrainingMenu(
        id = TrainingMenu.Id(0),
        name = "腕立て伏せ",
        part = TrainingMenu.Part.ARM,
        memo = "メモメモ".repeat(4),
        sets = List(5) { index ->
            TrainingMenu.OwnWeightSet(
                id = TrainingMenu.Set.Id(0),
                setIndex = index.toLong() + 1,
                number = index.toLong() + 10,
            )
        },
        eventIndex = eventIndex,
        type = TrainingMenu.Type.OWN_WEIGHT,
    )
}
