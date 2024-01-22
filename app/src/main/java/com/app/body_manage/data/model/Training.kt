package com.app.body_manage.data.model

import java.time.LocalDateTime

data class Training(
    val id: Id,
    val dateTime: LocalDateTime,
    val menus: List<TrainingMenu>,
    val memo: String,
) {
    data class Id(val value: Long)

    companion object {
        val NEW_ID = Id(-1)
    }
}
