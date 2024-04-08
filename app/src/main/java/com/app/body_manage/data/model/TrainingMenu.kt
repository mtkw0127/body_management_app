package com.app.body_manage.data.model

import androidx.annotation.StringRes
import com.app.body_manage.R
import com.app.body_manage.data.entity.TrainingMenuEntity
import java.io.Serializable

data class TrainingMenu(
    val id: Id,
    val name: String,
    val part: Part,
    val memo: String,
    val sets: List<Set>, // MEMO：最新のセット数。履歴管理する。
    val type: Type,
) : Serializable {
    data class Id(val value: Long) : Serializable

    sealed interface Set {
        val index: Int
        val number: Int
    }

    // MEMO: 例えば60kgを10回上げるような感じ
    // マシン・フリーウェイト用のSet
    data class WeightSet(
        override val index: Int,
        override val number: Int, // 実際の回数
        val weight: Int, // 実際の重量
    ) : Set, Serializable

    // 自重用のSet
    data class OwnWeightSet(
        override val index: Int,
        override val number: Int, // 実際の回数
    ) : Set, Serializable

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

fun createSampleTrainingMenu(): TrainingMenu {
    return TrainingMenu(
        id = TrainingMenu.Id(0),
        name = "ダンベルベンチプレス",
        part = TrainingMenu.Part.ARM,
        memo = "メモメモ".repeat(4),
        sets = List(5) { index ->
            TrainingMenu.WeightSet(
                index = index + 1,
                number = 10,
                weight = 55,
            )
        },
        type = TrainingMenu.Type.MACHINE,
    )
}

fun createSampleOwnWeightTrainingMenu(): TrainingMenu {
    return TrainingMenu(
        id = TrainingMenu.Id(0),
        name = "腕立て伏せ",
        part = TrainingMenu.Part.ARM,
        memo = "メモメモ".repeat(4),
        sets = List(5) { index ->
            TrainingMenu.OwnWeightSet(
                index = index + 1,
                number = 10 + index,
            )
        },
        type = TrainingMenu.Type.OWN_WEIGHT,
    )
}
