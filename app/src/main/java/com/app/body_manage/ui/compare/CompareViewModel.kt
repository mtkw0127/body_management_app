package com.app.body_manage.ui.compare

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.dao.ComparePhotoHistoryDao
import com.app.body_manage.data.entity.ComparePhotoHistoryEntity
import com.app.body_manage.data.repository.BodyMeasurePhotoRepository
import com.app.body_manage.data.repository.CompareHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime

sealed interface CompareState {
    data object NoPhotos : CompareState

    data class CompareItemsHasSet(
        val before: CompareItemStruct?,
        val after: CompareItemStruct?,
        val saveButtonVisibility: Boolean,
        val saveSuccess: Boolean,
        val saveFail: Boolean,
        val compareHistory: List<ComparePhotoHistoryDao.PhotoAndBodyMeasure>
    ) : CompareState

    data object CompareItemsError : CompareState
}

data class CompareViewModelState(
    val hasMeasureWithPhotos: Boolean = false,
    val before: CompareItemStruct? = null,
    val after: CompareItemStruct? = null,
    val histories: List<ComparePhotoHistoryDao.PhotoAndBodyMeasure> = listOf(),
    val error: Throwable? = null,
    val saveSuccess: Boolean = false,
    val saveFail: Boolean = false
) {
    fun toUiSate(): CompareState {
        return if (hasMeasureWithPhotos.not()) {
            CompareState.NoPhotos
        } else if (error != null) {
            CompareState.CompareItemsError
        } else {
            CompareState.CompareItemsHasSet(
                before = before,
                after = after,
                saveButtonVisibility = before != null && after != null,
                saveFail = saveFail,
                saveSuccess = saveSuccess,
                compareHistory = histories,
            )
        }
    }
}

data class CompareItemStruct(
    val photoId: Int,
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
        null,
    )

    private val isLoadingHistory = MutableLiveData(false)

    fun saveHistory() {
        if (viewModelState.value.before == null && viewModelState.value.after == null) {
            return
        }
        viewModelScope.launch {
            runCatching {
                compareHistoryRepository.saveHistory(
                    compareHistoryEntity = ComparePhotoHistoryEntity(
                        ui = 0,
                        beforePhotoId = requireNotNull(viewModelState.value.before?.photoId),
                        afterPhotoId = requireNotNull(viewModelState.value.after?.photoId),
                        createdAt = LocalDateTime.now(),
                    )
                )
            }.onFailure {
                Timber.e(it)
                viewModelState.update { viewModelState ->
                    viewModelState.copy(saveFail = true)
                }
            }.onSuccess {
                viewModelState.update { viewModelState ->
                    viewModelState.copy(saveSuccess = true)
                }
            }
        }
    }

    fun loadHistory() {
        if (isLoadingHistory.value == true) return
        isLoadingHistory.value = true
        viewModelScope.launch {
            runCatching {
                runCatching {
                    compareHistoryRepository.selectAll()
                }.onFailure {
                    Timber.e(it)
                }.onSuccess { response ->
                    viewModelState.update {
                        it.copy(
                            histories = response
                        )
                    }
                }.also {
                    isLoadingHistory.value = false
                }
            }
        }
    }

    fun loadBodyMeasure(photoId: Int, compareItemType: CompareItemType) {
        viewModelScope.launch {
            runCatching { bodyMeasurePhotoRepository.selectBodyMeasureByPhotoId(photoId = photoId) }
                .onFailure { Timber.e(it) }
                .onSuccess { response ->
                    response?.let {
                        val compareItem = CompareItemStruct(
                            photoId = photoId,
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

    fun deleteHistory(target: ComparePhotoHistoryDao.PhotoAndBodyMeasure) {
        viewModelScope.launch {
            runCatching {
                compareHistoryRepository.delete(target.compareHistoryId)
            }.onFailure {
                Timber.e(it)
            }.onSuccess {
                loadHistory()
            }
        }
    }

    fun loadPhotoMeasure() {
        viewModelScope.launch {
            runCatching {
                bodyMeasurePhotoRepository.selectPhotosByDate()
            }.onSuccess { response ->
                viewModelState.update {
                    it.copy(hasMeasureWithPhotos = response.isNotEmpty())
                }
            }
        }
    }
}
