package com.app.body_manage.data.model

import com.app.body_manage.data.entity.FoodEntity
import com.app.body_manage.extension.toJp
import com.app.body_manage.extension.toKana

data class Food(
    val id: Id,
    val name: String,
    val nameJp: String,
    val nameKana: String,
    val kcal: Int,
) {
    data class Id(val value: Int)

    val nameWithKcal: String
        get() {
            return "$name($kcal kcal)"
        }

    companion object {
        val NEW_ID: Id = Id(0)

        fun createNewFood(name: String): Food {
            return Food(
                id = NEW_ID,
                name = name,
                nameJp = name.toJp(),
                nameKana = name.toKana(),
                kcal = 0,
            )
        }
    }
}

fun Food.toEntity() = FoodEntity(
    foodId = this.id.value,
    name = this.name,
    kcal = this.kcal,
    nameJp = this.nameJp,
    nameKana = this.nameKana,
)
