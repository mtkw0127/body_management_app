package com.app.body_manage.ui.trainingForm.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.model.Training
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.data.repository.TrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalTime

class TrainingDetailViewModel(
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    private val _training: MutableStateFlow<Training?> = MutableStateFlow(null)
    val training: StateFlow<Training?> = _training

    fun addMenu(trainingMenu: TrainingMenu) {
        val currentTraining = _training.value ?: return
        val trainingMenuWithDefaultSet = trainingMenu.copy(
            eventIndex = currentTraining.menus.lastIndex + 1L,
            sets = List(DEFAULT_SET_NUMBER) { setIndex ->
                val castedSetIndex = setIndex.toLong()
                when (trainingMenu.type) {
                    TrainingMenu.Type.MACHINE -> TrainingMenu.WeightSet(
                        id = TrainingMenu.Set.ID_NEW,
                        setIndex = castedSetIndex,
                        number = DEFAULT_ACTUAL_NUMBER,
                        weight = 0 // TODO: 前回の重量を引き継ぐ
                    )

                    TrainingMenu.Type.FREE -> TrainingMenu.WeightSet(
                        id = TrainingMenu.Set.ID_NEW,
                        setIndex = castedSetIndex,
                        number = DEFAULT_ACTUAL_NUMBER,
                        weight = 0
                    )

                    TrainingMenu.Type.OWN_WEIGHT -> TrainingMenu.OwnWeightSet(
                        id = TrainingMenu.Set.ID_NEW,
                        setIndex = castedSetIndex,
                        number = DEFAULT_ACTUAL_NUMBER,
                    )
                }
            }
        )
        _training.update { training ->
            training?.copy(menus = currentTraining.menus + trainingMenuWithDefaultSet)
        }
    }

    fun deleteSet(
        menuIndex: Int,
        setIndex: Int
    ) {
        _training.update { training ->
            training?.copy(
                menus = training.menus.mapIndexed { i, trainingMenu ->
                    if (i == menuIndex) {
                        trainingMenu.copy(
                            sets = trainingMenu.sets.filterIndexed { j, _ -> j != setIndex }
                        )
                    } else {
                        trainingMenu
                    }
                }.filter {
                    it.sets.isNotEmpty()
                }
            )
        }
    }

    fun updateStartTime(time: LocalTime) {
        _training.update {
            it?.copy(startTime = time)
        }
    }

    fun updateEndTime(time: LocalTime) {
        _training.update {
            it?.copy(endTime = time)
        }
    }

    fun updateRep(menuIndex: Int, setIndex: Int, number: Long) {
        update(menuIndex, setIndex) { set ->
            when (set) {
                is TrainingMenu.WeightSet -> set.copy(number = number)
                is TrainingMenu.OwnWeightSet -> set.copy(number = number)
            }
        }
    }

    fun updateWeight(menuIndex: Int, setIndex: Int, weight: Long) {
        update(menuIndex, setIndex) { set ->
            when (set) {
                is TrainingMenu.WeightSet -> set.copy(weight = weight)
                else -> set
            }
        }
    }

    private fun update(
        menuIndex: Int,
        setIndex: Int,
        update: (TrainingMenu.Set) -> TrainingMenu.Set
    ) {
        _training.update { training ->
            training?.copy(
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


    fun init(training: Training) {
        _training.value = training
    }

    fun updateTraining() {
        viewModelScope.launch {
            try {
                trainingRepository.updateTraining(_training.value ?: return@launch)
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }
    }

    fun deleteMenu(menuIndex: Long) {
        _training.update {
            it?.copy(
                menus = it.menus.filter { menu ->
                    menu.eventIndex != menuIndex
                }.mapIndexed { index, menu ->
                    menu.copy(eventIndex = index.toLong())
                }
            )
        }
    }

    fun deleteTraining() {
        viewModelScope.launch {
            try {
                trainingRepository.deleteTraining(_training.value ?: return@launch)
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }
    }

    companion object {
        // 初期のセット数
        private const val DEFAULT_SET_NUMBER = 5

        // 初期値となる実施回数
        private const val DEFAULT_ACTUAL_NUMBER = 10L
    }
}