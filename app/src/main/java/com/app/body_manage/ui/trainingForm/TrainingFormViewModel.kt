package com.app.body_manage.ui.trainingForm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.model.Training
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.data.repository.TrainingRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime

class TrainingFormViewModel(
    private val trainingRepository: TrainingRepository,
) : ViewModel() {

    private val _isSuccessForSavingTraining: MutableSharedFlow<Unit> = MutableSharedFlow()
    val isSuccessForSavingTraining: SharedFlow<Unit> = _isSuccessForSavingTraining

    private val _training: MutableStateFlow<Training> = MutableStateFlow(
        Training(
            id = Training.NEW_ID,
            date = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now(),
            menus = listOf(),
            memo = "",
        )
    )
    val training: StateFlow<Training> = _training

    fun addMenu(trainingMenu: TrainingMenu) {
        val trainingMenuWithDefaultSet = trainingMenu.copy(
            sets = List(DEFAULT_SET_NUMBER) {
                when (trainingMenu.type) {
                    TrainingMenu.Type.MACHINE -> TrainingMenu.WeightSet(
                        index = it,
                        number = DEFAULT_ACTUAL_NUMBER,
                        weight = 0 // TODO: 前回の重量を引き継ぐ
                    )

                    TrainingMenu.Type.FREE -> TrainingMenu.WeightSet(
                        index = it,
                        number = DEFAULT_ACTUAL_NUMBER,
                        weight = 0
                    )

                    TrainingMenu.Type.OWN_WEIGHT -> TrainingMenu.OwnWeightSet(
                        index = it,
                        number = DEFAULT_ACTUAL_NUMBER,
                    )
                }
            }
        )
        _training.update {
            it.copy(menus = it.menus + trainingMenuWithDefaultSet)
        }
    }

    fun registerTraining() {
        viewModelScope.launch {
            try {
                trainingRepository.saveTraining(training.value)
                _isSuccessForSavingTraining.emit(Unit)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun updateRep(menuIndex: Int, setIndex: Int, number: Int) {
        update(menuIndex, setIndex) { set ->
            when (set) {
                is TrainingMenu.WeightSet -> set.copy(number = number)
                is TrainingMenu.OwnWeightSet -> set.copy(number = number)
            }
        }
    }

    fun updateWeight(menuIndex: Int, setIndex: Int, weight: Int) {
        update(menuIndex, setIndex) { set ->
            when (set) {
                is TrainingMenu.WeightSet -> set.copy(weight = weight)
                else -> set
            }
        }
    }

    fun updateStartTime(time: LocalTime) {
        _training.update {
            it.copy(startTime = time)
        }
    }

    fun updateEndTime(time: LocalTime) {
        _training.update {
            it.copy(endTime = time)
        }
    }

    private fun update(
        menuIndex: Int,
        setIndex: Int,
        update: (TrainingMenu.Set) -> TrainingMenu.Set
    ) {
        _training.update { training: Training ->
            training.copy(
                menus = training.menus.mapIndexed { i, trainingMenu ->
                    if (i == menuIndex) {
                        trainingMenu.copy(
                            sets = trainingMenu.sets.mapIndexed { j, set ->
                                if (j == setIndex) {
                                    when (set) {
                                        is TrainingMenu.WeightSet -> update(set)
                                        is TrainingMenu.OwnWeightSet -> update(set)
                                    }
                                } else {
                                    set
                                }
                            }
                        )
                    } else {
                        trainingMenu
                    }
                }
            )
        }
    }

    fun deleteSet(
        menuIndex: Int,
        setIndex: Int
    ) {
        _training.update {
            it.copy(
                menus = it.menus.mapIndexed { i, trainingMenu ->
                    if (i == menuIndex) {
                        trainingMenu.copy(
                            sets = trainingMenu.sets.filterIndexed { j, _ -> j != setIndex }
                        )
                    } else {
                        trainingMenu
                    }
                }
            )
        }
    }

    companion object {
        // 初期のセット数
        private const val DEFAULT_SET_NUMBER = 5

        // 初期値となる実施回数
        private const val DEFAULT_ACTUAL_NUMBER = 10
    }
}
