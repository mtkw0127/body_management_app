package com.app.body_manage.ui.top

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.entity.toModel
import com.app.body_manage.data.local.UserPreference
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.data.model.BodyMeasureModel
import com.app.body_manage.data.repository.BodyMeasureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TopViewModel(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val bodyMeasureRepository: BodyMeasureRepository,
) : ViewModel() {
    private val _userPreference: MutableStateFlow<UserPreference?> = MutableStateFlow(null)
    val userPreference = _userPreference.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _showUserPrefDialog = MutableStateFlow(false)
    val showUserPrefDialog: Flow<Boolean> = _showUserPrefDialog

    private val _lastMeasure: MutableStateFlow<BodyMeasureModel?> = MutableStateFlow(null)
    val lastMeasure = _lastMeasure.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun checkSetUpUserPref() {
        viewModelScope.launch {
            try {
                _userPreference.value = userPreferenceRepository.userPref.first()
                load()
            } catch (_: Throwable) {
                _showUserPrefDialog.value = true
            }
        }
    }

    fun load() {
        viewModelScope.launch {
            _userPreference.value = userPreferenceRepository.userPref.first()
            _lastMeasure.value = bodyMeasureRepository.getLast()?.toModel()
        }
    }

    fun setGoalWeight(goal: Float) {
        viewModelScope.launch {
            userPreferenceRepository.setGoatWeight(goal)
            load()
        }
    }
}
