package com.app.body_manage.ui.top

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.entity.toModel
import com.app.body_manage.data.local.UserPreference
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.data.model.BodyMeasure
import com.app.body_manage.data.model.Meal
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.data.repository.MealRepository
import com.app.body_manage.data.repository.TrainingRepository
import com.app.body_manage.extension.toWeight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TodayMeasure(
    val bodyMeasures: List<BodyMeasure>,
    val meals: List<Meal>,
    val didTraining: Boolean,
) {
    val minWeight: String
        get() = bodyMeasures.minOf { it.weight }.toWeight()
}

class TopViewModel(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val bodyMeasureRepository: BodyMeasureRepository,
    private val trainingRepository: TrainingRepository,
    private val mealRepository: MealRepository,
) : ViewModel() {
    private val _userPreference: MutableStateFlow<UserPreference?> = MutableStateFlow(null)
    val userPreference = _userPreference.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _showUserPrefDialog = MutableStateFlow(false)
    val showUserPrefDialog: Flow<Boolean> = _showUserPrefDialog

    private val _lastMeasure: MutableStateFlow<BodyMeasure?> = MutableStateFlow(null)
    val lastMeasure = _lastMeasure.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _initialMeasure: MutableStateFlow<BodyMeasure?> = MutableStateFlow(null)
    val initialMeasure = _initialMeasure.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // 当日の測定
    private val _todayMeasure: MutableStateFlow<TodayMeasure> = MutableStateFlow(
        TodayMeasure(
            emptyList(),
            emptyList(),
            false,
        )
    )
    val todayMeasure = _todayMeasure.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        TodayMeasure(
            emptyList(),
            emptyList(),
            false
        )
    )

    private val _openMeasureForm = MutableSharedFlow<Unit>()
    val openMeasureForm = _openMeasureForm.asSharedFlow()

    fun checkSetUpUserPref() {
        viewModelScope.launch {
            try {
                _userPreference.value = userPreferenceRepository.userPref.first()
                // 入力なしに登録したユーザのための処理
                checkNotNull(_userPreference.value?.birth)
                load()
            } catch (_: Throwable) {
                _showUserPrefDialog.value = true
            }
        }
    }

    fun load() {
        viewModelScope.launch {
            runCatching {
                _userPreference.value = userPreferenceRepository.userPref.firstOrNull()
                _lastMeasure.value = bodyMeasureRepository.getLast()?.toModel()
                _initialMeasure.value = bodyMeasureRepository.getFirst()?.toModel()

                // 今日の記録を取得
                val now = LocalDate.now()
                val meals = mealRepository.getMealsByDate(now)
                val bodyMeasures = bodyMeasureRepository.getEntityListByDate(now)
                val training = trainingRepository.getTrainingsByDate(now)
                _todayMeasure.value = TodayMeasure(
                    meals = meals,
                    bodyMeasures = bodyMeasures,
                    didTraining = training.isNotEmpty()
                )
                if (_lastMeasure.value == null) {
                    _openMeasureForm.emit(Unit)
                }
            }.onFailure {
                // データがない可能性があるため再設定
                checkSetUpUserPref()
            }
        }
    }

    fun setGoalWeight(goal: Float) {
        viewModelScope.launch {
            userPreferenceRepository.setGoatWeight(goal)
            load()
        }
    }

    fun setGoalKcal(goal: Long) {
        viewModelScope.launch {
            userPreferenceRepository.setGoatKcal(goal)
            load()
        }
    }
}
