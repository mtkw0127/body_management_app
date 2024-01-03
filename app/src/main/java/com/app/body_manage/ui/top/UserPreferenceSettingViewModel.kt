package com.app.body_manage.ui.top

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.local.Gender
import com.app.body_manage.data.local.UserPreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Locale

sealed interface UiState {
    val name: String?
    val gender: Gender
    val birth: String?
    val tall: Float?
    val fat: Float?
    val weight: Float?

    data class NotYet(
        override val name: String?,
        override val gender: Gender,
        override val birth: String?,
        override val tall: Float?,
        override val fat: Float?,
        override val weight: Float?,
    ) : UiState

    data class Done(
        override val name: String,
        override val gender: Gender,
        override val birth: String,
        override val tall: Float,
        override val fat: Float,
        override val weight: Float,
    ) : UiState
}

data class UserPreferenceSettingViewModelState(
    val name: String? = null,
    val gender: Gender = Gender.MALE,
    val birth: String? = null,
    val tall: Float? = null,
    val fat: Float? = null,
    val weight: Float? = null,
) {
    fun toUiState(): UiState {
        return if (name == null ||
            birth == null ||
            tall == null ||
            fat == null ||
            weight == null
        ) {
            UiState.NotYet(
                name = name,
                gender = gender,
                birth = birth,
                tall = tall,
                fat = fat,
                weight = weight
            )
        } else {
            UiState.Done(
                name = name,
                gender = gender,
                birth = birth,
                tall = tall,
                fat = fat,
                weight = weight
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

    fun setGender(gender: Gender) {
        viewModelState.update { it.copy(gender = gender) }
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
            } catch (e: Throwable) {
                return
            }
        }
        if (birth.startsWith("0").not()) {
            viewModelState.update { it.copy(birth = tempBirth) }
        }
    }
}