package com.app.body_manage.ui.measure.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.entity.BodyMeasureEntity
import com.app.body_manage.data.entity.MealMeasureEntity
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.ui.measure.list.MeasureListState.BodyMeasureListState
import com.app.body_manage.ui.measure.list.MeasureListState.MealMeasureListState
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface MeasureListState {
    val tabName: String
    val date: LocalDate

    data class BodyMeasureListState(
        val list: List<BodyMeasureEntity>,
        override val tabName: String = "体型",
        override val date: LocalDate
    ) : MeasureListState

    data class MealMeasureListState(
        val list: List<MealMeasureEntity>,
        override val tabName: String = "食事",
        override val date: LocalDate
    ) : MeasureListState
}

internal data class MeasureListViewModelState(
    val date: LocalDate,
    val pagerState: MeasureListState,
    val bodyMeasureList: List<BodyMeasureEntity> = mutableListOf<BodyMeasureEntity>().toList(),
    val mealMeasureList: List<MealMeasureEntity> = listOf(),
) {
    fun toUiState(): MeasureListState {
        return when (pagerState) {
            is BodyMeasureListState -> {
                BodyMeasureListState(date = date, list = bodyMeasureList)
            }
            is MealMeasureListState -> {
                MealMeasureListState(date = date, list = mealMeasureList)
            }
        }
    }
}

class MeasureListViewModel(
    private val localDate: LocalDate,
    private val pagerState: MeasureListState,
    private val bodyMeasureRepository: BodyMeasureRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        MeasureListViewModelState(
            date = localDate,
            pagerState = pagerState
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
        when (uiState.value) {
            is BodyMeasureListState -> {
                loadBodyMeasure()
            }
            is MealMeasureListState -> {
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
                        pagerState = pagerState,
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
                pagerState = pagerState,
                bodyMeasureList = mutableListOf(),
                mealMeasureList = result
            )
        }
    }
}