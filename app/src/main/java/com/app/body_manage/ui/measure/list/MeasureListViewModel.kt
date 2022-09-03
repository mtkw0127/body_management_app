package com.app.body_manage.ui.measure.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.entity.BodyMeasureModel
import com.app.body_manage.data.entity.MealMeasureEntity
import com.app.body_manage.data.local.UserPreferenceRepository
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
import kotlinx.coroutines.runBlocking
import timber.log.Timber

sealed interface MeasureListState {
    val date: LocalDate
    val measureType: MeasureType

    data class BodyMeasureListState(
        val list: List<BodyMeasureModel>,
        val tall: String,
        override val measureType: MeasureType,
        override val date: LocalDate,
    ) : MeasureListState

    data class MealMeasureListState(
        val list: List<MealMeasureEntity>,
        override val measureType: MeasureType,
        override val date: LocalDate
    ) : MeasureListState

    data class CommonMeasureListState(
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
    val tall: String = 150.0F.toString()
) {
    fun toUiState(): MeasureListState {
        return when (measureType) {
            MeasureType.BODY -> {
                BodyMeasureListState(
                    date = date,
                    list = bodyMeasureList,
                    tall = tall,
                    measureType = measureType,
                )
            }
            MeasureType.MEAL -> {
                MealMeasureListState(
                    date = date,
                    list = mealMeasureList,
                    measureType = measureType,
                )
            }
            else -> {
                MeasureListState.CommonMeasureListState(
                    date = date,
                    list = mealMeasureList,
                    measureType = measureType,
                )
            }
        }
    }
}

class MeasureListViewModel(
    private val localDate: LocalDate,
    private val mealType: MeasureType,
    private val bodyMeasureRepository: BodyMeasureRepository,
    private val userPreferenceRepository: UserPreferenceRepository
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
                runBlocking {
                    loadTall()
                    loadBodyMeasure()
                }
            }
            MeasureType.MEAL -> {
                loadMealMeasureList()
            }
            else -> {}
        }
    }

    fun updateTall() {
        val tall = viewModelState.value.tall.toFloat()
        viewModelScope.launch {
            runCatching {
                bodyMeasureRepository.updateTallByDate(
                    tall = tall,
                    calendarDate = localDate,
                )
            }.onFailure {
                Timber.e(it)
            }.onSuccess {
                reload()
                updateUserPrefTall(tall)
            }
        }
    }

    private fun updateUserPrefTall(tall: Float) {
        viewModelScope.launch {
            runCatching { userPreferenceRepository.putTall(tall) }
                .onFailure {
                    Timber.e(it)
                }
                .onSuccess {
                    println("hoge")
                }
        }
    }

    fun setTall(tall: String) {
        viewModelState.update {
            it.copy(tall = tall)
        }
    }

    private fun loadTall() {
        viewModelScope.launch {
            userPreferenceRepository.tall.collect {
                if (it.tall != null) {
                    setTall(it.tall.toString())
                }
            }
        }
    }

    private fun loadBodyMeasure() {
        viewModelScope.launch {
            runCatching {
                bodyMeasureRepository.getEntityListByDate(localDate)
            }.onFailure { e ->
                Timber.e(e)
            }.onSuccess { loadedResult ->
                // 当日の記録の身長を優先して利用する、未設定の場合は前回保存の身長を利用する。
                var tall = loadedResult.firstOrNull()?.tall
                if (tall == null) tall = viewModelState.value.tall.toFloat()
                viewModelState.update {
                    it.copy(
                        date = localDate,
                        measureType = mealType,
                        bodyMeasureList = loadedResult,
                        mealMeasureList = mutableListOf(),
                        tall = tall.toString()
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
                mealMeasureList = result
            )
        }
    }

}