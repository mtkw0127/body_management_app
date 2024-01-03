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

class TopViewModel(
    private val userPreferenceRepository: UserPreferenceRepository,
) : ViewModel(
) {
    private val _userPreference: MutableStateFlow<UserPreference?> = MutableStateFlow(null)
    val userPreference = _userPreference.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _showUserPrefDialog = MutableStateFlow(false)
    val showUserPrefDialog: Flow<Boolean> = _showUserPrefDialog

    fun checkSetUpUserPref() {
        viewModelScope.launch {
            try {
                _userPreference.value = userPreferenceRepository.userPref.first()
            } catch (_: Throwable) {
                _showUserPrefDialog.value = true
            }
        }
    }
}