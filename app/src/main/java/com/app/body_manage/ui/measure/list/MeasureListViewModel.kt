package com.app.body_manage.ui.measure.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.dao.BodyMeasurePhotoDao
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.data.model.BodyMeasure
import com.app.body_manage.data.model.Meal
import com.app.body_manage.data.model.Measure
import com.app.body_manage.data.model.Training
import com.app.body_manage.data.repository.BodyMeasurePhotoRepository
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.data.repository.MealRepository
import com.app.body_manage.data.repository.TrainingRepository
import com.app.body_manage.ui.measure.list.MeasureListState.BodyMeasureListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.YearMonth

sealed interface MeasureListState {
    val date: LocalDate
    val currentMonth: YearMonth
    val currentMonthRegisteredDayList: List<LocalDate>

    data class BodyMeasureListState(
        val list: List<Measure>,
        val tall: String,
        val loading: Boolean,
        val message: String,
        override val date: LocalDate,
        val photoList: List<BodyMeasurePhotoDao.PhotoData>,
        override val currentMonth: YearMonth,
        override val currentMonthRegisteredDayList: List<LocalDate>,
    ) : MeasureListState
}

data class MeasureListViewModelState(
    val date: LocalDate,
    val currentMonth: YearMonth,
    val currentMonthRegisteredDayList: List<LocalDate> = emptyList(),
    val bodies: List<BodyMeasure> = emptyList(),
    val meals: List<Meal> = emptyList(),
    val trainings: List<Training> = emptyList(),
    val photoList: List<BodyMeasurePhotoDao.PhotoData> = emptyList(),
    val tall: String = 150.0F.toString(),
    val updateTall: Boolean = false,
    val loadingTall: Boolean = false,
    val message: String = ""
) {
    private val someLoading = updateTall || loadingTall

    fun toUiState(): BodyMeasureListState {
        return BodyMeasureListState(
            date = date,
            list = (bodies + meals + trainings).sortedBy { it.time }, // 時刻が早い順に並べる
            photoList = photoList,
            tall = tall,
            currentMonth = currentMonth,
            currentMonthRegisteredDayList = currentMonthRegisteredDayList,
            loading = someLoading,
            message = message,
        )
    }
}

class MeasureListViewModel(
    localDate: LocalDate,
    private val bodyMeasureRepository: BodyMeasureRepository,
    private val bodyMeasurePhotoRepository: BodyMeasurePhotoRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val mealRepository: MealRepository,
    private val trainingRepository: TrainingRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        MeasureListViewModelState(
            date = localDate,
            currentMonth = YearMonth.now(),
        )
    )
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun setDate(localDate: LocalDate) {
        viewModelState.update {
            it.copy(
                date = localDate
            )
        }
        reload()
    }

    private fun viewModelStateLoadingUpdate(updateTall: Boolean? = null, loading: Boolean? = null) {
        updateTall?.let { it1 ->
            viewModelState.update {
                it.copy(updateTall = it1)
            }
        }
        loading?.let { it2 ->
            viewModelState.update {
                it.copy(loadingTall = it2)
            }
        }
    }

    fun reload() {
        loadTall()
        loadBodyMeasure()
        loadPhoto()
        loadRegisteredDayList()
        loadMeals()
        loadTraining()
    }

    private fun loadTraining() {
        viewModelScope.launch {
            val trainingList = trainingRepository.getTrainingsByDate(viewModelState.value.date)
            viewModelState.update {
                it.copy(trainings = trainingList)
            }
        }
    }

    private fun loadMeals() {
        viewModelScope.launch {
            val meals = mealRepository.getMealsByDate(viewModelState.value.date)
            viewModelState.update {
                it.copy(meals = meals)
            }
        }
    }

    private fun updateMessage(message: String) {
        viewModelState.update {
            it.copy(message = message)
        }
    }

    fun resetMessage() {
        viewModelState.update {
            it.copy(message = "")
        }
    }

    fun updateTall() {
        if (viewModelState.value.updateTall) return
        viewModelStateLoadingUpdate(updateTall = true)
        val tall = viewModelState.value.tall.toFloat()
        viewModelScope.launch {
            runCatching {
                bodyMeasureRepository.updateTallByDate(
                    tall = tall,
                    calendarDate = viewModelState.value.date,
                )
            }.onFailure {
                Timber.e(it)
            }.onSuccess {
                updateUserPrefTall(tall)
                reload()
                updateMessage("身長を更新し、BMIを再計算しました")
            }.also {
                viewModelStateLoadingUpdate(updateTall = false)
            }
        }
    }

    fun updateDate(diff: Int) {
        viewModelState.update {
            it.copy(date = viewModelState.value.date.plusDays(diff.toLong()))
        }
        reload()
    }

    private fun loadPhoto() {
        viewModelScope.launch {
            runCatching { bodyMeasurePhotoRepository.selectPhotosByDate(viewModelState.value.date) }
                .onFailure { Timber.e(it) }
                .onSuccess { dbResponse ->
                    viewModelState.update {
                        it.copy(photoList = dbResponse)
                    }
                }
        }
    }

    private fun updateUserPrefTall(tall: Float) {
        viewModelScope.launch {
            runCatching { userPreferenceRepository.putTall(tall) }
                .onFailure {
                    Timber.e(it)
                }
                .onSuccess {}
        }
    }

    fun setTall(tall: String) {
        runCatching {
            tall.toFloat()
        }.onSuccess {
            viewModelState.update { viewModelState ->
                viewModelState.copy(tall = tall)
            }
        }
    }

    fun setCurrentYearMonth(yearMonth: YearMonth) {
        viewModelState.update {
            it.copy(currentMonth = yearMonth)
        }
        loadRegisteredDayList()
    }

    private fun loadTall() {
        viewModelScope.launch {
            userPreferenceRepository.tall.collect { tall ->
                if (tall != null) {
                    setTall(tall.toString())
                }
            }
        }
    }

    private fun loadBodyMeasure() {
        if (viewModelState.value.loadingTall) return
        viewModelStateLoadingUpdate(loading = true)
        viewModelScope.launch {
            runCatching {
                bodyMeasureRepository.getEntityListByDate(viewModelState.value.date)
            }.onFailure { e ->
                Timber.e(e)
            }.onSuccess { loadedResult ->
                // 当日の記録の身長を優先して利用する、未設定の場合は前回保存の身長を利用する。
                var tall = loadedResult.firstOrNull()?.tall
                if (tall == null) tall = viewModelState.value.tall.toFloat()
                viewModelState.update {
                    it.copy(
                        date = viewModelState.value.date,
                        bodies = loadedResult,
                        tall = tall.toString()
                    )
                }
            }.also {
                viewModelStateLoadingUpdate(loading = false)
            }
        }
    }

    private fun loadRegisteredDayList() {
        viewModelScope.launch {
            runCatching {
                with(viewModelState.value.currentMonth) {
                    val from = LocalDate.of(year, month, 1)
                    val to = atEndOfMonth()
                    bodyMeasureRepository.getBetween(from, to)
                }
            }
                .onFailure { Timber.e(it) }
                .onSuccess {
                    val dayList =
                        it.map { bodyMeasure -> bodyMeasure.time.toLocalDate() }
                            .distinct()
                    viewModelState.update { vmState ->
                        vmState.copy(currentMonthRegisteredDayList = dayList)
                    }
                }
        }
    }
}
