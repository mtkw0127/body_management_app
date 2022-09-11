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

sealed interface PhotoListState {
    data class NoPhoto(
        val message: String
    ) : PhotoListState

    data class HasPhoto(
        val photos: Map<String, List<BodyMeasurePhotoDao.PhotoData>>
    ) : PhotoListState
}

internal data class PhotoListViewModelState(
    val photos: Map<String, List<BodyMeasurePhotoDao.PhotoData>> = mutableMapOf()
) {
    fun toUiState(): PhotoListState =
        when {
            photos.isEmpty() -> {
                NoPhoto(message = "写真はまだ登録されていません。")
            }
            else -> {
                HasPhoto(photos = photos)
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

    /**
     * 写真が登録された日、一覧を取得する
     */
    fun loadPhotoRegisteredDates() {
        viewModelScope.launch {
            kotlin.runCatching { bmpRepository.selectPhotosByDate() }
                .onFailure { e -> e.printStackTrace() }
                .onSuccess {
                    if (it.isNotEmpty()) {
                        viewModelState.update { state ->
                            state.copy(photos = it)
                        }
                    }
                }
        }
    }
}