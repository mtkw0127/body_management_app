package com.app.body_manage.ui.top

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.local.UserPreference
import com.app.body_manage.data.local.UserPreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.pow

class TopViewModel(
    private val userPreferenceRepository: UserPreferenceRepository,
) : ViewModel(
) {
    private val _userPreference: MutableStateFlow<UserPreference?> = MutableStateFlow(null)
    val userPreference = _userPreference.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _showUserPrefDialog = MutableStateFlow(false)
    val showUserPrefDialog: Flow<Boolean> = _showUserPrefDialog

    private val _healthyDuration: MutableStateFlow<String> = MutableStateFlow("-")
    val healthyDuration = _healthyDuration.stateIn(viewModelScope, SharingStarted.Eagerly, "-")

    fun checkSetUpUserPref() {
        viewModelScope.launch {
            try {
                _userPreference.value = userPreferenceRepository.userPref.first()
            } catch (_: Throwable) {
                _showUserPrefDialog.value = true
            }
        }
    }

    fun load() {
        viewModelScope.launch {
            _userPreference.value = userPreferenceRepository.userPref.first()
            _healthyDuration.value = if (_userPreference.value?.tall == null) {
                "-"
            } else {
                val tallCalc =
                    (checkNotNull(_userPreference.value?.tall) / 100F).toDouble().pow(2.0)
                val min = (18.5F * tallCalc * 100).toInt() / 100F
                val max = (24.9F * tallCalc * 100).toInt() / 100F
                "${min}kg - ${max}kg"
            }
        }
    }
}