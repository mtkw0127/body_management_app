package com.app.body_manage.ui.photoList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.dao.BodyMeasurePhotoDao
import com.app.body_manage.data.repository.BodyMeasurePhotoRepository
import com.app.body_manage.ui.photoList.PhotoListState.HasPhoto
import com.app.body_manage.ui.photoList.PhotoListState.NoPhoto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

enum class SortType {
    WEIGHT, DATE
}

sealed interface PhotoListState {
    val type: SortType

    data class NoPhoto(
        val message: String,
        override val type: SortType
    ) : PhotoListState

    data class HasPhoto(
        val photos: Map<String, List<BodyMeasurePhotoDao.PhotoData>>,
        override val type: SortType
    ) : PhotoListState
}

internal data class PhotoListViewModelState(
    val photos: Map<String, List<BodyMeasurePhotoDao.PhotoData>> = mutableMapOf(),
    val sortType: SortType = SortType.DATE
) {
    fun toUiState(): PhotoListState =
        when {
            photos.isEmpty() -> {
                NoPhoto(message = "写真はまだ登録されていません。", sortType)
            }

            else -> {
                HasPhoto(photos = photos, sortType)
            }
        }
}

class PhotoListViewModel(application: Application) : AndroidViewModel(application) {

    private val bmpRepository: BodyMeasurePhotoRepository by lazy {
        (application as TrainingApplication).bodyMeasurePhotoRepository
    }

    private val viewModelState = MutableStateFlow(PhotoListViewModelState())
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun load() {
        when (viewModelState.value.sortType) {
            SortType.DATE -> {
                loadPhotoRegisteredDates()
            }

            SortType.WEIGHT -> {
                loadPhotoRegisteredWeight()
            }
        }
    }

    /**
     * 写真が登録された日、一覧を取得する
     */
    private fun loadPhotoRegisteredDates() {
        viewModelScope.launch {
            kotlin.runCatching { bmpRepository.selectPhotosByDate() }
                .onFailure { Timber.e(it) }
                .onSuccess {
                    if (it.isNotEmpty()) {
                        viewModelState.update { state ->
                            state.copy(photos = it)
                        }
                    }
                }
        }
    }

    private fun loadPhotoRegisteredWeight() {
        viewModelScope.launch {
            kotlin.runCatching { bmpRepository.selectPhotosByWeight() }
                .onFailure { Timber.e(it) }
                .onSuccess {
                    if (it.isNotEmpty()) {
                        viewModelState.update { state ->
                            state.copy(photos = it)
                        }
                    }
                }
        }
    }

    fun changeType(sortType: SortType) {
        viewModelState.update {
            it.copy(sortType = sortType)
        }
        load()
    }
}
