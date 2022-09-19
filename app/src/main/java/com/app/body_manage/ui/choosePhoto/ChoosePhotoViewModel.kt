package com.app.body_manage.ui.choosePhoto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.dao.BodyMeasurePhotoDao
import com.app.body_manage.data.model.PhotoModel
import com.app.body_manage.data.repository.BodyMeasurePhotoRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

sealed interface SelectPhotoState {
    val date: LocalDate

    data class SelectedPhoto(
        val currentMonth: LocalDate,
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
    val currentMonth: LocalDate,
    val selectedDate: LocalDate,
    val photoId: PhotoModel.Id? = null,
    val currentMonthRegisteredDateList: List<LocalDate> = listOf(),
    val photoList: List<BodyMeasurePhotoDao.PhotoData> = listOf(),
    val error: Throwable? = null
) {
    fun toUiState(): SelectPhotoState {
        return if (error != null) {
            SelectPhotoState.Error(error = error, date = selectedDate)
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
            currentMonth = LocalDate.now(),
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

    fun loadCurrentMonthHavePhotosDateList() {
        viewModelScope.launch {
            with(viewModelState.value.currentMonth) {
                val from = LocalDate.of(year, month, 1)
                val to = this.plusMonths(1).withDayOfMonth(1).minusDays(1)
                runCatching {
                    bodyMeasurePhotoRepository.selectHavePhotoDateList(from, to)
                }.onFailure {
                    Timber.e(it)
                }.onSuccess { list ->
                    viewModelState.update {
                        it.copy(currentMonthRegisteredDateList = list)
                    }
                }
            }
        }
    }
}