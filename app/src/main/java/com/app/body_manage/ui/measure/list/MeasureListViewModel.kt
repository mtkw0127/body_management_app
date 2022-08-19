package com.app.body_manage.ui.measure.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.entity.BodyMeasureEntity
import com.app.body_manage.data.entity.MealMeasureEntity
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.ui.measure.list.MeasureListState.BodyMeasureListState
import com.app.body_manage.ui.measure.list.MeasureListState.MealMeasureListState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface MeasureListState {
    val date: LocalDate
    val measureType: MeasureType

    data class BodyMeasureListState(
        val list: List<BodyMeasureEntity>,
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
    val mealType: MeasureType,
    val bodyMeasureList: List<BodyMeasureEntity> = mutableListOf<BodyMeasureEntity>().toList(),
    val mealMeasureList: List<MealMeasureEntity> = listOf(),
) {
    fun toUiState(): MeasureListState {
        return when (mealType) {
            MeasureType.BODY -> {
                BodyMeasureListState(date = date, list = bodyMeasureList, measureType = mealType)
            }
            MeasureType.MEAL -> {
                MealMeasureListState(date = date, list = mealMeasureList, measureType = mealType)
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
            mealType = mealType,
        )
    )
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun reload() {
        when (viewModelState.value.mealType) {
            MeasureType.BODY -> {
                loadBodyMeasure()
            }
            MeasureType.MEAL -> {
                loadMealMeasureList()
            }
        }
    }

    private fun loadBodyMeasure() {
        viewModelScope.launch {
            runCatching {
                bodyMeasureRepository.getEntityListByDate(localDate)
            }.onFailure { e ->
                e.printStackTrace()
            }.onSuccess { loadedResult ->
                viewModelState.update {
                    it.copy(
                        date = localDate,
                        mealType = mealType,
                        bodyMeasureList = loadedResult,
                        mealMeasureList = mutableListOf()
                    )
                }
            }
        }
    }

    private fun loadMealMeasureList() {
        val result = listOf(
            MealMeasureEntity(ui = 1),
            MealMeasureEntity(ui = 2),
            MealMeasureEntity(ui = 3)
        )
        viewModelState.update {
            it.copy(
                date = localDate,
                mealType = mealType,
                bodyMeasureList = mutableListOf(),
                mealMeasureList = result
            )
        }
    }
}