package com.app.body_manage.ui.top

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.local.Gender
import com.app.body_manage.data.local.UserPreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale

sealed interface UiState {
    val name: String?
    val gender: Gender
    val birth: String?
    val tall: Float?
    val weight: Float?

    data class NotYet(
        override val name: String?,
        override val gender: Gender,
        override val birth: String?,
        override val tall: Float?,
        override val weight: Float?,
    ) : UiState

    data class Done(
        override val name: String,
        override val gender: Gender,
        override val birth: String,
        override val tall: Float,
        override val weight: Float,
    ) : UiState
}

data class UserPreferenceSettingViewModelState(
    val name: String? = null,
    val gender: Gender = Gender.MALE,
    val birth: String? = null,
    val tall: Float? = null,
    val weight: Float? = null,
) {
    fun toUiState(): UiState {
        return if (name == null ||
            birth == null ||
            tall == null ||
            weight == null
        ) {
            UiState.NotYet(
                name = name,
                gender = gender,
                birth = birth,
                tall = tall,
                weight = weight
            )
        } else {
            UiState.Done(
                name = name,
                gender = gender,
                birth = birth,
                tall = tall,
                weight = weight,
            )
        }
    }
}

class UserPreferenceSettingViewModel(
    private val userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {
    private val viewModelState = MutableStateFlow(UserPreferenceSettingViewModelState())
    val uiState = viewModelState.map { it.toUiState() }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        UserPreferenceSettingViewModelState().toUiState()
    )

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    fun setGender(gender: Gender) {
        viewModelState.update { it.copy(gender = gender) }
    }

    fun setName(name: String) {
        viewModelState.update { it.copy(name = name.ifBlank { null }) }
    }

    fun setTall(tall: String) {
        viewModelState.update {
            it.copy(
                tall = try {
                    if (tall.isEmpty()) {
                        null
                    } else {
                        tall.toFloat()
                    }
                } catch (_: Throwable) {
                    return
                }
            )
        }
    }

    fun setWeight(weight: String) {
        viewModelState.update {
            it.copy(
                weight = try {
                    if (weight.isEmpty()) {
                        null
                    } else {
                        weight.toFloat()
                    }
                } catch (_: Throwable) {
                    return
                }
            )
        }
    }

    fun setBirth(birth: String) {
        // 空文字はクリアしてリターン
        if (birth.isBlank()) {
            viewModelState.update {
                it.copy(birth = birth)
            }
            return
        }
        try {
            birth.substring(0, birth.lastIndex + 1).toInt()
        } catch (e: NumberFormatException) {
            // 数値以外はセットしない
            return
        }
        var tempBirth = birth
        val isAdd = (birth.length - (viewModelState.value.birth?.length ?: 0)) > 0
        if (isAdd) {
            // 月を取得する
            if (birth.length == 5) {
                // 3-9で始まる場合は03-09として処理する
                val month = birth.substring(4, 5).toInt()
                if (month in 2..9) {
                    tempBirth = "0$month"
                }
            }
            if (birth.length == 6) {
                // 13月以降は無効とする
                val month = birth.substring(4, 6).toInt()
                if (12 < month) {
                    return
                }
            }
        }
        if (8 < birth.length) {
            return
        }
        // 日付に変換できるかチェック
        if (birth.length in 7..8) {
            try {
                SimpleDateFormat("yyyyMMdd", Locale.getDefault()).apply {
                    isLenient = false
                }.parse(tempBirth)
            } catch (_: Throwable) {
                return
            }
        }
        if (birth.startsWith("0").not()) {
            viewModelState.update { it.copy(birth = tempBirth) }
        }
    }

    fun save() {
        with(viewModelState.value) {
            val birth = birth ?: return
            val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                .parse(birth)
                ?.toInstant()
                ?.atZone(ZoneId.systemDefault())
                ?.toLocalDate() ?: return@with

            viewModelScope.launch {
                userPreferenceRepository.setBirth(date)
                userPreferenceRepository.setGender(gender)
                userPreferenceRepository.setName(name.orEmpty())
                userPreferenceRepository.putTall(checkNotNull(tall))
                userPreferenceRepository.putWeight(checkNotNull(weight))
                _saved.value = true
            }
        }
    }
}
