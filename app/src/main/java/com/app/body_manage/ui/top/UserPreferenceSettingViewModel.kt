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
import kotlinx.coroutines.flow.firstOrNull
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
    val hasMealFeature: Boolean
    val hasTrainingFeature: Boolean

    data class NotYet(
        override val name: String?,
        override val gender: Gender,
        override val birth: TextFieldValue,
        override val hasMealFeature: Boolean,
        override val hasTrainingFeature: Boolean,
    ) : UiState

    data class Done(
        override val name: String,
        override val gender: Gender,
        override val birth: TextFieldValue,
        override val hasMealFeature: Boolean,
        override val hasTrainingFeature: Boolean,
    ) : UiState
}

data class UserPreferenceSettingViewModelState(
    val name: String? = null,
    val gender: Gender = Gender.MALE,
    val birth: TextFieldValue = TextFieldValue(),
    val hasMealFeature: Boolean = false,
    val hasTrainingFeature: Boolean = false,
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

        val isNotYet = name.isNullOrBlank() ||
            setBirth.not() ||
            birth.text.isEmpty()

        return if (isNotYet) {
            UiState.NotYet(
                name = name,
                gender = gender,
                birth = birth,
                hasMealFeature = hasMealFeature,
                hasTrainingFeature = hasTrainingFeature,
            )
        } else {
            UiState.Done(
                name = name.orEmpty(),
                gender = gender,
                birth = birth,
                hasMealFeature = hasMealFeature,
                hasTrainingFeature = hasTrainingFeature,
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

    // 体重未入力の状態で起動できてしまう問題があったため修正
    fun load() {
        viewModelScope.launch {
            try {
                userPreferenceRepository.userPref.firstOrNull()?.let { pref ->
                    viewModelState.update {
                        val birth = if (pref.birth.monthValue < 10) {
                            "${pref.birth.year}0${pref.birth.monthValue}${pref.birth.dayOfMonth}"
                        } else {
                            "${pref.birth.year}${pref.birth.monthValue}${pref.birth.dayOfMonth}"
                        }
                        it.copy(
                            name = pref.name,
                            gender = pref.gender,
                            birth = TextFieldValue(
                                text = birth,
                                selection = TextRange(birth.length, birth.length),
                            ),
                            hasMealFeature = pref.optionFeature.meal,
                            hasTrainingFeature = pref.optionFeature.training,
                        )
                    }
                }
            } catch (_: Throwable) {
            }
        }
    }

    fun setGender(gender: Gender) {
        viewModelState.update { it.copy(gender = gender) }
    }

    fun setName(name: String) {
        viewModelState.update { it.copy(name = name) }
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
        var tempBirth = TextFieldValue(
            text = trimBirth,
            selection = TextRange(
                start = trimBirth.length,
                end = trimBirth.length
            )
        )
        val isAdd = (trimBirth.length - viewModelState.value.birth.text.length) > 0
        if (isAdd) {
            // 月を取得する
            if (trimBirth.length == 5) {
                // 3-9で始まる場合は03-09として処理する
                val month = trimBirth.substring(4, 5).toInt()
                val year = trimBirth.substring(0, 4)
                if (month in 2..9) {
                    tempBirth = TextFieldValue(
                        text = "${year}0$month",
                        selection = TextRange(
                            start = "${year}0$month".length,
                            end = "${year}0$month".length,
                        )
                    )
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
                userPreferenceRepository.setOptionMeal(hasMealFeature)
                userPreferenceRepository.setOptionTraining(hasTrainingFeature)
                _saved.value = true
            }
        }
    }

    fun updateMealFeature(hasMealFeature: Boolean) {
        viewModelState.update { it.copy(hasMealFeature = hasMealFeature) }
    }

    fun updateTrainingFeature(hasTrainingFeature: Boolean) {
        viewModelState.update { it.copy(hasTrainingFeature = hasTrainingFeature) }
    }
}
