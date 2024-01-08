package com.app.body_manage.ui.photoDetail

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.model.BodyMeasure
import com.app.body_manage.data.model.PhotoModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

sealed interface PhotoDetailState {
    data class ShowPhotoDetailWithDetail(
        val photoModel: PhotoModel,
        val bodyMeasureModel: BodyMeasure?
    ) : PhotoDetailState

    data class ShowPhotoDetailFromUri(
        val uri: Uri
    ) : PhotoDetailState

    data class NotFoundPhoto(val err: Throwable) : PhotoDetailState
}

data class PhotoDetailViewModelState(
    val photoModel: PhotoModel? = null,
    val bodyMeasureModel: BodyMeasure? = null,
    val err: Throwable? = null,
    val uri: Uri? = null,
) {
    fun toUiState(): PhotoDetailState {
        return if (photoModel != null) {
            PhotoDetailState.ShowPhotoDetailWithDetail(
                photoModel,
                bodyMeasureModel,
            )
        } else if (uri != null) {
            PhotoDetailState.ShowPhotoDetailFromUri(
                uri = uri
            )
        } else if (err != null) {
            PhotoDetailState.NotFoundPhoto(err)
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

    private val viewModelState = MutableStateFlow(PhotoDetailViewModelState())

    val uiState = viewModelState.map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun loadPhoto(photoId: PhotoModel.Id) {
        viewModelScope.launch {
            runCatching { photoDetailRepository.selectPhoto(photoId) }
                .onFailure {
                    Timber.e(it)
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
            }
        }
    }

    fun setUri(uri: Uri) {
        viewModelState.update {
            it.copy(uri = uri)
        }
    }
}
