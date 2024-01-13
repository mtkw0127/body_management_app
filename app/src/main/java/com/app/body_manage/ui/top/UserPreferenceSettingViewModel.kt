package com.app.body_manage.ui.top

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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
    val birth: TextFieldValue
    val tall: TextFieldValue
    val weight: TextFieldValue

    data class NotYet(
        override val name: String?,
        override val gender: Gender,
        override val birth: TextFieldValue,
        override val tall: TextFieldValue,
        override val weight: TextFieldValue,
    ) : UiState

    data class Done(
        override val name: String,
        override val gender: Gender,
        override val birth: TextFieldValue,
        override val tall: TextFieldValue,
        override val weight: TextFieldValue,
    ) : UiState
}

data class UserPreferenceSettingViewModelState(
    val name: String? = null,
    val gender: Gender = Gender.MALE,
    val birth: TextFieldValue = TextFieldValue(),
    val tall: TextFieldValue = TextFieldValue(),
    val weight: TextFieldValue = TextFieldValue(),
) {
    fun toUiState(): UiState {
        val setBirth = try {
            run {
                SimpleDateFormat("yyyyMMdd", Locale.getDefault()).apply {
                    isLenient = false
                }.parse(birth.text)
                true
            }
        } catch (_: Throwable) {
            false
        }

        return if (name == null ||
            setBirth.not() ||
            birth.text.isEmpty() ||
            tall == null ||
            weight.text.isEmpty()
        ) {
            UiState.NotYet(
                name = name,
                gender = gender,
                birth = TextFieldValue(
                    birth.text,
                    TextRange(birth.text.length, birth.text.length)
                ),
                tall = tall,
                weight = TextFieldValue(
                    weight.text,
                    TextRange(weight.text.length, weight.text.length)
                ),
            )
        } else {
            UiState.Done(
                name = name,
                gender = gender,
                birth = birth,
                tall = tall,
                weight = TextFieldValue(
                    weight.text,
                    TextRange(weight.text.length, birth.text.length)
                ),
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
        viewModelState.update { it.copy(name = name) }
    }

    fun setTall(tall: TextFieldValue) {
        val trimTall = tall.text.trim()
        if (trimTall.startsWith("0")) {
            return
        }
        viewModelState.update {
            it.copy(
                tall = try {
                    if (trimTall.isEmpty()) {
                        TextFieldValue()
                    } else {
                        trimTall.toFloat()
                        tall
                    }
                } catch (_: Throwable) {
                    return
                }
            )
        }
    }

    fun setWeight(weight: TextFieldValue) {
        val trimWeight = weight.text.trim()
        if (trimWeight.startsWith("0")) {
            return
        }
        viewModelState.update {
            it.copy(
                weight = try {
                    if (trimWeight.isEmpty()) {
                        TextFieldValue()
                    } else {
                        trimWeight.toFloat()
                        weight
                    }
                } catch (_: Throwable) {
                    return
                }
            )
        }
    }

    fun setBirth(birth: TextFieldValue) {
        val trimBirth = birth.text.trim()
        // 空文字はクリアしてリターン
        if (trimBirth.isBlank()) {
            viewModelState.update {
                it.copy(birth = TextFieldValue(""))
            }
            return
        }
        try {
            trimBirth.substring(0, trimBirth.lastIndex + 1).toInt()
        } catch (e: NumberFormatException) {
            // 数値以外はセットしない
            return
        }
        var tempBirth = TextFieldValue(trimBirth)
        val isAdd = (trimBirth.length - (viewModelState.value.birth?.text?.length ?: 0)) > 0
        if (isAdd) {
            // 月を取得する
            if (trimBirth.length == 5) {
                // 3-9で始まる場合は03-09として処理する
                val month = trimBirth.substring(4, 5).toInt()
                val year = trimBirth.substring(0, 4)
                if (month in 2..9) {
                    tempBirth = TextFieldValue("${year}0$month")
                }
            }
            if (trimBirth.length == 6) {
                // 13月以降は無効とする
                val month = trimBirth.substring(4, 6).toInt()
                if (12 < month) {
                    return
                }
            }
        }
        if (8 < trimBirth.length) {
            return
        }
        // 日付に変換できるかチェック
        if (trimBirth.length in 7..8) {
            try {
                SimpleDateFormat("yyyyMMdd", Locale.getDefault()).apply {
                    isLenient = false
                }.parse(tempBirth.text)
            } catch (_: Throwable) {
                return
            }
        }
        if (trimBirth.startsWith("0").not()) {
            viewModelState.update { it.copy(birth = tempBirth) }
        }
    }

    fun save() {
        with(viewModelState.value) {
            val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                .parse(birth.text)
                ?.toInstant()
                ?.atZone(ZoneId.systemDefault())
                ?.toLocalDate() ?: return@with

            viewModelScope.launch {
                userPreferenceRepository.setBirth(date)
                userPreferenceRepository.setGender(gender)
                userPreferenceRepository.setName(name.orEmpty())
                userPreferenceRepository.putTall(tall.text.toFloat())
                userPreferenceRepository.putWeight(weight.text.toFloat())
                _saved.value = true
            }
        }
    }
}
