package com.app.body_manage.ui.measure.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.entity.BodyMeasureEntity
import com.app.body_manage.data.entity.BodyMeasureModel
import com.app.body_manage.data.entity.MealMeasureEntity
import com.app.body_manage.data.entity.toModel
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.ui.measure.list.MeasureListState.BodyMeasureListState
import com.app.body_manage.ui.measure.list.MeasureListState.MealMeasureListState
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalDateTime

sealed interface MeasureListState {
    val date: LocalDate
    val measureType: MeasureType

    data class BodyMeasureListState(
        val list: List<BodyMeasureModel>,
        override val measureType: MeasureType,
        override val date: LocalDate,
    ) : MeasureListState

    data class MealMeasureListState(
        val list: List<MealMeasureEntity>,
        override val measureType: MeasureType,
        override val date: LocalDate
    ) : MeasureListState
}

internal data class MeasureListViewModelState(
    val date: LocalDate,
    val measureType: MeasureType,
    val bodyMeasureList: List<BodyMeasureModel> = listOf(),
    val mealMeasureList: List<MealMeasureEntity> = listOf(),
) {
    fun toUiState(): MeasureListState {
        return when (measureType) {
            MeasureType.BODY -> {
                BodyMeasureListState(date = date, list = bodyMeasureList, measureType = measureType)
            }
            MeasureType.MEAL -> {
                MealMeasureListState(date = date, list = mealMeasureList, measureType = measureType)
            }
        }
    }
}

class MeasureListViewModel(
    private val localDate: LocalDate,
    private val mealType: MeasureType,
    private val bodyMeasureRepository: BodyMeasureRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        MeasureListViewModelState(
            date = localDate,
            measureType = mealType,
        )
    )
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun switchType(measureType: MeasureType) {
        viewModelState.update {
            it.copy(
                measureType = measureType
            )
        }
        reload()
    }

    fun reload() {
        when (viewModelState.value.measureType) {
            MeasureType.BODY -> {
                loadBodyMeasure()
            }
            MeasureType.MEAL -> {
                loadMealMeasureList()
            }
        }
    }

    private fun loadBodyMeasure() {
        val result = listOf(
            BodyMeasureEntity(
                ui = 1,
                capturedDate = LocalDate.now(),
                calendarDate = LocalDate.now(),
                capturedTime = LocalDateTime.now(),
                weight = 1.0F,
                fatRate = 1.0F,
                photoUri = "https:yahoo.co.jp"
            ).toModel()
        )
        viewModelState.update {
            it.copy(
                bodyMeasureList = result
            )
        }
//        viewModelScope.launch {
//            runCatching {
//                bodyMeasureRepository.getEntityListByDate(localDate)
//            }.onFailure { e ->
//                e.printStackTrace()
//            }.onSuccess { loadedResult ->
//                viewModelState.update {
//                    it.copy(
//                        date = localDate,
//                        mealType = mealType,
//                        bodyMeasureList = loadedResult,
//                        mealMeasureList = mutableListOf()
//                    )
//                }
//            }
//        }
    }

    private fun loadMealMeasureList() {
        val result = listOf(
            MealMeasureEntity(ui = 1),
            MealMeasureEntity(ui = 2),
            MealMeasureEntity(ui = 3)
        )
        viewModelState.update {
            it.copy(
                mealMeasureList = result
            )
        }
    }
}