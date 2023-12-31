package com.app.body_manage.ui.graph

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.repository.BodyMeasureRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate

sealed interface GraphState {
    data object Initial : GraphState
    data object NoData : GraphState
    data class HasData(
        val duration: Duration,
        val currentType: DataType,
        val timelineForWeight: List<Pair<LocalDate, Float>>,
        val timelineForFat: List<Pair<LocalDate, Float>>
    ) : GraphState
}

enum class DataType {
    WEIGHT, FAT,
}

enum class Duration(val value: Long) {
    ONE_MONTH(1),
    THREE_MONTH(3),
    HALF_YEAR(6),
    ONE_YEAR(12),
    ALL(-1)
}

data class GraphViewModelState(
    val duration: Duration = Duration.ONE_MONTH,
    val timelineWeight: List<Pair<LocalDate, Float>>? = null,
    val timelineFat: List<Pair<LocalDate, Float>>? = null,
    val dataType: DataType = DataType.WEIGHT
) {
    fun toUiState(): GraphState {
        if (timelineWeight == null || timelineFat == null) {
            return GraphState.Initial
        }
        if (timelineWeight.isEmpty() || timelineFat.isEmpty()) {
            return GraphState.NoData
        }
        return GraphState.HasData(
            duration,
            dataType,
            timelineWeight,
            timelineFat,
        )
    }
}

class GraphViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val viewModelState = MutableStateFlow(GraphViewModelState())

    val uiState = viewModelState.map {
        it.toUiState()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, GraphState.Initial)

    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (application as TrainingApplication).bodyMeasureRepository
    }

    fun loadBodyMeasure() {
        viewModelScope.launch {
            runCatching {
                bodyMeasureRepository.getLast()?.let { last ->
                    when (viewModelState.value.duration) {
                        Duration.ALL -> bodyMeasureRepository.getEntityListAll()
                        else -> {
                            bodyMeasureRepository.getBetweenGroupByDate(
                                from = last.calendarDate.minusMonths(viewModelState.value.duration.value),
                                to = last.calendarDate
                            )
                        }
                    }
                }
            }
                .onFailure { Timber.e(it) }
                .onSuccess { bodyMeasureList ->
                    if (bodyMeasureList != null) {
                        val timelineForWeight = bodyMeasureList
                            .map {
                                it.capturedLocalDateTime.toLocalDate() to it.weight
                            }.toList()

                        val timelineForFat = bodyMeasureList
                            .map {
                                it.capturedLocalDateTime.toLocalDate() to it.fat
                            }.toList()

                        viewModelState.update {
                            it.copy(
                                timelineWeight = timelineForWeight,
                                timelineFat = timelineForFat
                            )
                        }
                    } else {
                        viewModelState.update {
                            it.copy(
                                timelineWeight = emptyList(),
                                timelineFat = emptyList(),
                            )
                        }
                    }
                }
        }
    }

    fun setDataType(dataType: DataType) {
        viewModelState.update {
            it.copy(dataType = dataType)
        }
    }

    fun setDuration(duration: Duration) {
        if (duration == viewModelState.value.duration) return
        viewModelState.update {
            it.copy(duration = duration)
        }
        loadBodyMeasure()
    }
}
