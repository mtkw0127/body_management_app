package com.app.body_manage.ui.trainingForm

import androidx.lifecycle.ViewModel
import com.app.body_manage.data.model.Training
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.data.repository.TrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime

open class TrainingFormBaseViewModel(
    val trainingRepository: TrainingRepository,
) : ViewModel() {

    protected val privateTraining: MutableStateFlow<Training> = MutableStateFlow(
        Training(
            id = Training.NEW_ID,
            date = LocalDate.now(),
            time = LocalDate.now().atTime(LocalTime.now()),
            startTime = LocalTime.now(),
            endTime = LocalTime.now(),
            menus = emptyList(),
            memo = "",
            createdAt = LocalDate.now(),
        )
    )
    val training: StateFlow<Training> = privateTraining

    fun addMenu(trainingMenu: TrainingMenu) {
        val trainingMenuWithDefaultSet = trainingMenu.copy(
            eventIndex = privateTraining.value.menus.lastIndex + 1L,
            // 有酸素運動の場合はセットという概念がない
            sets = if (trainingMenu.type == TrainingMenu.Type.Cardio) {
                listOf(
                    TrainingMenu.CardioSet(
                        id = TrainingMenu.TrainingInterface.ID_NEW,
                        distance = 0F,
                        minutes = 0,
                    )
                )
            } else {
                List(DEFAULT_SET_NUMBER) { setIndex ->
                    val castedSetIndex = setIndex.toLong()
                    when (trainingMenu.type) {
                        TrainingMenu.Type.MACHINE -> TrainingMenu.WeightSet(
                            id = TrainingMenu.TrainingInterface.ID_NEW,
                            setIndex = castedSetIndex,
                            number = DEFAULT_ACTUAL_NUMBER,
                            weight = 0 // TODO: 前回の重量を引き継ぐ
                        )

                        TrainingMenu.Type.FREE -> TrainingMenu.WeightSet(
                            id = TrainingMenu.TrainingInterface.ID_NEW,
                            setIndex = castedSetIndex,
                            number = DEFAULT_ACTUAL_NUMBER,
                            weight = 0
                        )

                        TrainingMenu.Type.OWN_WEIGHT -> TrainingMenu.OwnWeightSet(
                            id = TrainingMenu.TrainingInterface.ID_NEW,
                            setIndex = castedSetIndex,
                            number = DEFAULT_ACTUAL_NUMBER,
                        )

                        else -> throw IllegalArgumentException("Invalid type: ${trainingMenu.type}")
                    }
                }
            }
        )
        privateTraining.update {
            it.copy(menus = it.menus + trainingMenuWithDefaultSet)
        }
    }

    fun updateRep(menuIndex: Int, setIndex: Int, number: Long) {
        update(menuIndex, setIndex) { set ->
            when (set) {
                is TrainingMenu.WeightSet -> set.copy(number = number)
                is TrainingMenu.OwnWeightSet -> set.copy(number = number)
                else -> set
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

    fun updateStartTime(time: LocalTime) {
        privateTraining.update {
            it.copy(startTime = time)
        }
    }

    fun updateEndTime(time: LocalTime) {
        privateTraining.update {
            it.copy(endTime = time)
        }
    }

    fun updateCardioDistance(menuIndex: Int, distance: Float) {
        update(menuIndex, 0) { set ->
            when (set) {
                is TrainingMenu.CardioSet -> set.copy(distance = distance)
                else -> set
            }
        }
    }

    fun updateCardioMinutes(menuIndex: Int, minutes: Long) {
        update(menuIndex, 0) { set ->
            when (set) {
                is TrainingMenu.CardioSet -> set.copy(minutes = minutes)
                else -> set
            }
        }
    }

    private fun update(
        menuIndex: Int,
        setIndex: Int,
        update: (TrainingMenu.TrainingInterface) -> TrainingMenu.TrainingInterface
    ) {
        privateTraining.update { training: Training ->
            training.copy(
                menus = training.menus.mapIndexed { i, trainingMenu ->
                    if (i == menuIndex) {
                        trainingMenu.copy(
                            sets = trainingMenu.sets.mapIndexed { j, set ->
                                if (j == setIndex) {
                                    when (set) {
                                        is TrainingMenu.WeightSet -> update(set)
                                        is TrainingMenu.OwnWeightSet -> update(set)
                                        is TrainingMenu.CardioSet -> update(set)
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
        privateTraining.update { training ->
            training.copy(
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

    fun deleteMenu(menuIndex: Long) {
        privateTraining.update {
            it.copy(
                menus = it.menus.filter { menu ->
                    menu.eventIndex != menuIndex
                }.mapIndexed { index, menu ->
                    menu.copy(eventIndex = index.toLong())
                }
            )
        }
    }

    fun updateMemo(memo: String) {
        privateTraining.update {
            it.copy(memo = memo)
        }
    }

    companion object {
        // 初期のセット数
        private const val DEFAULT_SET_NUMBER = 5

        // 初期値となる実施回数
        private const val DEFAULT_ACTUAL_NUMBER = 10L
    }
}
