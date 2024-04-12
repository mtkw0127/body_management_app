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

    companion object {
        // 初期のセット数
        private const val DEFAULT_SET_NUMBER = 6

        // 初期値となる実施回数
        private const val DEFAULT_ACTUAL_NUMBER = 10
    }
}
