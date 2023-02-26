package com.app.body_manage.ui.choosePhoto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.dao.BodyMeasurePhotoDao
import com.app.body_manage.data.model.PhotoModel
import com.app.body_manage.data.repository.BodyMeasurePhotoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.YearMonth

sealed interface SelectPhotoState {
    val date: LocalDate

    data class SelectedPhoto(
        val currentMonth: YearMonth,
        val currentMonthRegisteredDateList: List<LocalDate>,
        val photoId: PhotoModel.Id?,
        val photoList: List<BodyMeasurePhotoDao.PhotoData>,
        override val date: LocalDate,
    ) : SelectPhotoState

    data class Error(
        val error: Throwable,
        override val date: LocalDate,
    ) : SelectPhotoState
}

data class SelectPhotoViewModelState(
    val currentMonth: YearMonth,
    val selectedDate: LocalDate,
    val photoId: PhotoModel.Id? = null,
    val currentMonthRegisteredDateList: List<LocalDate> = listOf(),
    val photoList: List<BodyMeasurePhotoDao.PhotoData> = listOf(),
    val error: Throwable? = null
) {
    fun toUiState(): SelectPhotoState {
        return if (error != null) {
            SelectPhotoState.Error(
                error = error,
                date = selectedDate
            )
        } else {
            SelectPhotoState.SelectedPhoto(
                currentMonth = currentMonth,
                currentMonthRegisteredDateList = currentMonthRegisteredDateList,
                date = selectedDate,
                photoList = photoList,
                photoId = photoId,
            )
        }
    }
}

class ChoosePhotoViewModel(
    private val bodyMeasurePhotoRepository: BodyMeasurePhotoRepository
) : ViewModel() {
    private val viewModelState = MutableStateFlow(
        SelectPhotoViewModelState(
            currentMonth = YearMonth.now(),
            selectedDate = LocalDate.now(),
        )
    )

    val uiState = viewModelState
        .map {
            it.toUiState()
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun setLocalDate(date: LocalDate) {
        viewModelState.update {
            it.copy(
                selectedDate = date
            )
        }
        loadPhoto()
    }

    private fun loadPhoto() {
        viewModelScope.launch {
            runCatching { bodyMeasurePhotoRepository.selectPhotosByDate(viewModelState.value.selectedDate) }
                .onFailure { Timber.e(it) }
                .onSuccess { dbResponse ->
                    viewModelState.update {
                        it.copy(photoList = dbResponse)
                    }
                }
        }
    }

    fun updateCurrentMonth(currentMonth: YearMonth) {
        viewModelState.update {
            it.copy(currentMonth = currentMonth)
        }
        loadCurrentMonthHavePhotosDateList()
    }

    fun loadCurrentMonthHavePhotosDateList() {
        viewModelScope.launch {
            with(viewModelState.value.currentMonth) {
                val nextMonth = this.plusMonths(1)
                val nextMonthFirstDay = LocalDate.of(nextMonth.year, nextMonth.month, 1)
                val from = LocalDate.of(year, month, 1)
                val to = nextMonthFirstDay.minusDays(1)
                runCatching {
                    bodyMeasurePhotoRepository.selectHavePhotoDateList(from, to)
                }.onFailure {
                    Timber.e(it)
                }.onSuccess { list ->
                    viewModelState.update {
                        it.copy(
                            currentMonthRegisteredDateList = list,
                            selectedDate = if (list.isNotEmpty()) { // 当該月の最初の日付を選択
                                list.first()
                            } else {
                                viewModelState.value.selectedDate
                            }
                        )
                    }
                    // 選択した日の写真を読み込み
                    loadPhoto()
                }
            }
        }
    }
}