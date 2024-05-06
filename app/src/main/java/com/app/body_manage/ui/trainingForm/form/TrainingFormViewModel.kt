package com.app.body_manage.ui.trainingForm.form

import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.repository.TrainingRepository
import com.app.body_manage.ui.trainingForm.TrainingFormBaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime

class TrainingFormViewModel(
    trainingRepository: TrainingRepository,
) : TrainingFormBaseViewModel(trainingRepository = trainingRepository) {

    private val _isSuccessForSavingTraining: MutableSharedFlow<Unit> = MutableSharedFlow()
    val isSuccessForSavingTraining: SharedFlow<Unit> = _isSuccessForSavingTraining

    fun init(date: LocalDate) {
        privateTraining.update {
            it.copy(
                date = date,
                time = date.atTime(LocalTime.now()),
                createdAt = date,
            )
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
}
