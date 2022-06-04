package com.app.calendar.ui.measure.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.calendar.model.BodyMeasureEntity
import com.app.calendar.repository.BodyMeasureRepository
import java.time.LocalDate
import kotlinx.coroutines.launch

class MeasureListViewModel(
    private val localDate: LocalDate,
    private val bodyMeasureRepository: BodyMeasureRepository
) : ViewModel() {
    
    private val _measureList = MutableLiveData<List<BodyMeasureEntity>>(mutableListOf())
    val measureList: LiveData<List<BodyMeasureEntity>> = _measureList

    init {
        loadMeasureList()
    }

    fun reload() {
        _measureList.value = mutableListOf()
        loadMeasureList()
    }

    private fun loadMeasureList() {
        viewModelScope.launch {
            runCatching {
                bodyMeasureRepository.getEntityListByDate(localDate)
            }.onFailure { e ->
                e.printStackTrace()
            }.onSuccess {
                _measureList.value = it
            }
        }
    }
}