package com.app.body_manage.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.local.UserPreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class SettingUiState {
    data class Settings(
        val alarm: Boolean
    ) : SettingUiState()

    data class ErrorSettings(
        val exception: Throwable,
    ) : SettingUiState()
}

class SettingViewModel(
    private val userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<SettingUiState>(SettingUiState.Settings(false))
    val uiState: StateFlow<SettingUiState> = _uiState

    init {
        viewModelScope.launch {
            userPreferenceRepository.userPref.catch { e ->
                _uiState.value = SettingUiState.ErrorSettings(e)
            }.collect {
                _uiState.value = SettingUiState.Settings(alarm = it.alarm ?: false)
            }
        }
    }

    fun updateAlarm(on: Boolean) {
        viewModelScope.launch {
            kotlin.runCatching { userPreferenceRepository.putAlarm(on) }
                .onFailure { Timber.e(it) }
                .onSuccess {
                    _uiState.value = SettingUiState.Settings(alarm = on)
                }
        }
    }
}