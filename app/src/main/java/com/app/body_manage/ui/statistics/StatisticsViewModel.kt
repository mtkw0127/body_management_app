package com.app.body_manage.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.entity.toModel
import com.app.body_manage.data.local.UserPreference
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.data.model.BodyMeasure
import com.app.body_manage.data.repository.BodyMeasureRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StatisticsViewModel(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val measureRepository: BodyMeasureRepository,
) : ViewModel() {

    private val _userPreference: MutableStateFlow<UserPreference?> = MutableStateFlow(null)
    val userPreference: StateFlow<UserPreference?> =
        _userPreference.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _latestMeasure: MutableStateFlow<BodyMeasure?> = MutableStateFlow(null)
    val latestMeasure: StateFlow<BodyMeasure?> =
        _latestMeasure.stateIn(viewModelScope, SharingStarted.Eagerly, null)


    fun init() {
        viewModelScope.launch {
            val latestMeasure = measureRepository.getLast()
            _latestMeasure.update { latestMeasure?.toModel() }
            val userPreference = userPreferenceRepository.userPref.firstOrNull()
            _userPreference.update { userPreference }
        }
    }
}