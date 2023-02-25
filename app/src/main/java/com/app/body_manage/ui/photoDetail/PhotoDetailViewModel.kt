package com.app.body_manage.ui.photoDetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.entity.BodyMeasureModel
import com.app.body_manage.data.model.PhotoModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

sealed interface PhotoDetailState {
    data class ShowPhotoDetail(
        val photoModel: PhotoModel,
        val bodyMeasureModel: BodyMeasureModel?
    ) : PhotoDetailState

    data class LoadingPhotoDetail(
        val loading: Boolean
    ) : PhotoDetailState

    data class NotFoundPhoto(val err: Throwable) : PhotoDetailState
}

data class PhotoDetailViewModelState(
    val photoModel: PhotoModel? = null,
    val bodyMeasureModel: BodyMeasureModel? = null,
    val loading: Boolean = true,
    val err: Throwable? = null,
) {
    fun toUiState(): PhotoDetailState {
        return if (photoModel != null) {
            PhotoDetailState.ShowPhotoDetail(
                photoModel,
                bodyMeasureModel,
            )
        } else if (err != null) {
            PhotoDetailState.NotFoundPhoto(err)
        } else if (loading) {
            PhotoDetailState.LoadingPhotoDetail(loading)
        } else {
            val err = IllegalArgumentException("写真詳細で写真を表示できません。")
            Timber.e(err)
            PhotoDetailState.NotFoundPhoto(err)
        }
    }
}

class PhotoDetailViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val photoDetailRepository by lazy {
        (application as TrainingApplication).photoRepository
    }

    private val bodyMeasureRepository by lazy {
        (application as TrainingApplication).bodyMeasureRepository
    }

    private val viewModelState = MutableStateFlow(
        PhotoDetailViewModelState(
            loading = true,
        )
    )

    val uiState = viewModelState.map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun loadPhoto(photoId: PhotoModel.Id) {
        viewModelState.update {
            it.copy(loading = true)
        }
        viewModelScope.launch {
            runCatching { photoDetailRepository.selectPhoto(photoId) }
                .onFailure {
                    Timber.e(it)
                    viewModelState.update { viewModelState ->
                        viewModelState.copy(loading = false)
                    }
                }
                .onSuccess { photoModel ->
                    viewModelState.update {
                        it.copy(photoModel = photoModel)
                    }
                    loadBodyMeasure()
                }
        }
    }

    /** 写真に紐づく計測情報を取得*/
    private fun loadBodyMeasure() {
        viewModelScope.launch {
            runCatching {
                bodyMeasureRepository.fetch(
                    viewModelState.value.photoModel?.bodyMeasureId ?: return@launch
                )
            }.onFailure {
                Timber.e(it)
            }.onSuccess { fetchedResult ->
                viewModelState.update {
                    it.copy(bodyMeasureModel = fetchedResult)
                }
            }.also {
                viewModelState.update {
                    it.copy(loading = false)
                }
            }
        }
    }
}