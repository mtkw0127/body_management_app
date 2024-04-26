package com.app.body_manage.ui.trainingMenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.data.repository.TrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class TrainingMenuListViewModel(
    private val trainingRepository: TrainingRepository,
) : ViewModel() {

    private val _trainingMenuList: MutableStateFlow<List<TrainingMenu>> =
        MutableStateFlow(emptyList())
    val trainingMenuList: StateFlow<List<TrainingMenu>> = _trainingMenuList

    fun loadMenu() {
        viewModelScope.launch {
            runCatching {
                trainingRepository.getJustTrainingMenuList()
            }.onSuccess {
                _trainingMenuList.value = it
            }.onFailure { error ->
                Timber.e(error)
            }
        }
    }
}
