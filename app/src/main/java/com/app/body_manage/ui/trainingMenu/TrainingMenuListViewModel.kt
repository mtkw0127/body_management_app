package com.app.body_manage.ui.trainingMenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.entity.TrainingMenuEntity
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.data.repository.TrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class TrainingMenuListViewModel(
    private val trainingRepository: TrainingRepository,
) : ViewModel() {

    private val _selectedPart: MutableStateFlow<TrainingMenu.Part?> = MutableStateFlow(null)
    val selectedPart: StateFlow<TrainingMenu.Part?> = _selectedPart

    private val _selectedType: MutableStateFlow<TrainingMenu.Type?> = MutableStateFlow(null)
    val selectedType: StateFlow<TrainingMenu.Type?> = _selectedType

    private val _trainingMenuList: MutableStateFlow<List<TrainingMenu>> =
        MutableStateFlow(emptyList())
    val trainingMenuList: StateFlow<List<TrainingMenu>> = _trainingMenuList

    fun loadMenu() {
        viewModelScope.launch {
            runCatching {
                trainingRepository.getJustTrainingMenuList()
            }.onSuccess { menus ->
                _trainingMenuList.value = menus

                if (_selectedPart.value != null) {
                    _trainingMenuList.value =
                        _trainingMenuList.value.filter { it.part == _selectedPart.value }
                }

                if (_selectedType.value != null) {
                    _trainingMenuList.value =
                        _trainingMenuList.value.filter { it.type == _selectedType.value }
                }
            }.onFailure { error ->
                Timber.e(error)
            }
        }
    }

    fun saveTrainingMenu(menu: TrainingMenuEntity) {
        viewModelScope.launch {
            trainingRepository.saveMenu(menu)
            loadMenu()
        }
    }

    fun updateTrainingMenu(menu: TrainingMenuEntity) {
        viewModelScope.launch {
            trainingRepository.updateMenu(menu)
            loadMenu()
        }
    }

    fun updatePartFilter(it: TrainingMenu.Part?) {
        _selectedPart.value = it
        loadMenu()
    }

    fun updateTypeFilter(it: TrainingMenu.Type?) {
        _selectedType.value = it
        loadMenu()
    }
}
