package com.app.calendar.model

import android.net.Uri
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

data class TrainingModel(
    val capturedDate: LocalDate,
    val capturedTime: LocalDateTime,
    val weight:Float,
    val fatRate:Float,
    val photoUris:String?
    ): Serializable