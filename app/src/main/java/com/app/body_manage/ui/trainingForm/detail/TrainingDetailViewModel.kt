package com.app.body_manage.ui.trainingForm.detail

import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.model.Training
import com.app.body_manage.data.repository.TrainingRepository
import com.app.body_manage.ui.trainingForm.TrainingFormBaseViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class TrainingDetailViewModel(
    trainingRepository: TrainingRepository
) : TrainingFormBaseViewModel(trainingRepository) {
    fun init(training: Training) {
        privateTraining.value = training
    }

    fun updateTraining() {
        viewModelScope.launch {
            try {
                trainingRepository.updateTraining(privateTraining.value)
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }
    }

    fun deleteTraining() {
        viewModelScope.launch {
            try {
                trainingRepository.deleteTraining(privateTraining.value)
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }
    }
}
