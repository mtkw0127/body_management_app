package com.app.body_manage.ui.compare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.repository.BodyMeasurePhotoRepository
import com.app.body_manage.data.repository.CompareHistoryRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

sealed interface CompareState {
    data class CompareItemsHasSet(
        val before: CompareItemStruct?,
        val after: CompareItemStruct?,
    ) : CompareState

    data class CompareItemsError(
        val error: Throwable
    ) : CompareState
}

data class CompareViewModelState(
    val before: CompareItemStruct? = null,
    val after: CompareItemStruct? = null,
    val error: Throwable? = null,
) {
    fun toUiSate(): CompareState {
        return if (error != null) {
            CompareState.CompareItemsError(error)
        } else {
            CompareState.CompareItemsHasSet(before, after)
        }
    }
}

data class CompareItemStruct(
    val date: LocalDate,
    val weight: Float,
    val fat: Float,
    val photoUri: String,
)

enum class CompareItemType {
    BEFORE, AFTER,
}

class CompareViewModel(
    private val bodyMeasurePhotoRepository: BodyMeasurePhotoRepository,
    private val compareHistoryRepository: CompareHistoryRepository,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(CompareViewModelState())
    val uiState = viewModelState.map {
        it.toUiSate()
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        CompareState.CompareItemsHasSet(null, null),
    )

    fun saveHistory() {

    }

    fun loadBodyMeasure(photoId: Int, compareItemType: CompareItemType) {
        viewModelScope.launch {
            runCatching { bodyMeasurePhotoRepository.selectBodyMeasureByPhotoId(photoId = photoId) }
                .onFailure { Timber.e(it) }
                .onSuccess { response ->
                    response?.let {
                        val compareItem = CompareItemStruct(
                            date = response.calendarDate,
                            weight = response.weight,
                            fat = response.fat,
                            photoUri = response.photoUri
                        )
                        viewModelState.update {
                            when (compareItemType) {
                                CompareItemType.BEFORE -> {
                                    it.copy(before = compareItem)
                                }
                                CompareItemType.AFTER -> {
                                    it.copy(after = compareItem)
                                }
                            }
                        }
                    }
                }
        }
    }
}