package com.app.body_manage.ui.measure.form

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.entity.toModel
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.data.local.toBodyMeasureForAdd
import com.app.body_manage.data.model.BodyMeasureModel
import com.app.body_manage.data.model.PhotoModel
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.data.repository.PhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime

sealed interface FormState {
    data object Init : FormState
    sealed interface HasData : FormState {
        val model: BodyMeasureModel
        val photos: List<PhotoModel>
        val measureDate: LocalDate // 登録する日付アプリバー用

        data class Add(
            override val model: BodyMeasureModel,
            override val photos: List<PhotoModel>,
            override val measureDate: LocalDate,
        ) : HasData

        data class Edit(
            override val model: BodyMeasureModel,
            override val photos: List<PhotoModel>,
            override val measureDate: LocalDate,
        ) : HasData
    }
}

data class FormViewModelState(
    val model: BodyMeasureModel? = null,
    val photos: List<PhotoModel> = emptyList(),
    val type: Type? = null,
    val measureDate: LocalDate? = null,
) {
    enum class Type {
        Add, Edit
    }

    fun toState(): FormState {
        if (type == null || model == null || measureDate == null) return FormState.Init
        return when (type) {
            Type.Add -> {
                FormState.HasData.Add(
                    model = model,
                    photos = photos,
                    measureDate = measureDate,
                )
            }

            Type.Edit -> {
                FormState.HasData.Edit(
                    model = model,
                    photos = photos,
                    measureDate = measureDate,
                )
            }
        }
    }
}

class BodyMeasureEditFormViewModel(
    private val userPreferenceRepository: UserPreferenceRepository,
    application: Application,
) : AndroidViewModel(application) {

    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (application as TrainingApplication).bodyMeasureRepository
    }
    private val photoRepository: PhotoRepository by lazy {
        (application as TrainingApplication).photoRepository
    }

    private val viewModelState = MutableStateFlow(FormViewModelState())
    val uiState = viewModelState.map { it.toState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, FormState.Init)

    fun setType(type: FormViewModelState.Type) {
        viewModelState.update { it.copy(type = type) }
    }

    fun setMeasureDate(date: LocalDate) {
        viewModelState.update { it.copy(measureDate = date) }
    }

    fun loadFromUserPref() {
        assert(viewModelState.value.type == FormViewModelState.Type.Add)
        val measureDate = checkNotNull(viewModelState.value.measureDate)
        viewModelScope.launch {
            val bodyMeasureModel = userPreferenceRepository
                .userPref
                .first()
                .toBodyMeasureForAdd(measureDate)
            viewModelState.update {
                it.copy(model = bodyMeasureModel)
            }
        }
    }

    fun addPhotos(photoList: List<PhotoModel>) {
        viewModelState.update {
            it.copy(photos = it.photos + photoList)
        }
    }

    fun deletePhoto(photoModel: PhotoModel) {
        viewModelState.update {
            val photos = it.photos.filterNot { it.id == photoModel.id }
            it.copy(photos = photos)
        }
    }

    fun setFat(fat: Float) {
        viewModelState.update {
            val model = checkNotNull(it.model).copy(fat = fat)
            it.copy(model = model)
        }
    }

    fun setTime(time: LocalDateTime) {
        viewModelState.update {
            val model = checkNotNull(it.model).copy(capturedLocalDateTime = time)
            it.copy(model = model)
        }
    }

    fun setPreviousDay() {
        viewModelState.update {
            val model = checkNotNull(it.model)
            val updatedModel =
                model.copy(capturedLocalDateTime = model.capturedLocalDateTime.minusDays(1))
            val updatedMeasureDate = checkNotNull(it.measureDate).minusDays(1)

            it.copy(model = updatedModel, measureDate = updatedMeasureDate)
        }
    }

    fun setNextDay() {
        viewModelState.update {
            val model = checkNotNull(it.model)
            val updatedModel =
                model.copy(capturedLocalDateTime = model.capturedLocalDateTime.plusDays(1))
            val updatedMeasureDate = checkNotNull(it.measureDate).plusDays(1)

            it.copy(model = updatedModel, measureDate = updatedMeasureDate)
        }
    }

    fun setWeight(weight: Float) {
        viewModelState.update {
            val model = checkNotNull(it.model).copy(weight = weight)
            it.copy(model = model)
        }
    }

    fun loadBodyMeasure(measureTime: LocalDateTime) {
        viewModelScope.launch {
            runCatching {
                bodyMeasureRepository.getEntityByCaptureTime(measureTime).first().toModel()
            }.onFailure { e ->
                Timber.e(e)
            }.onSuccess { model ->
                viewModelState.update {
                    it.copy(model = model)
                }
                loadPhotos()
            }
        }
    }

    private fun loadPhotos() {
        viewModelScope.launch {
            runCatching {
                photoRepository.selectPhotos(
                    bodyMeasureId = checkNotNull(viewModelState.value.model?.id)
                )
            }.onFailure { e ->
                Timber.e(e)
            }.onSuccess { photos ->
                viewModelState.update {
                    it.copy(photos = photos)
                }
            }
        }
    }

    fun save() {
        val model = checkNotNull(viewModelState.value.model)
        when (checkNotNull(viewModelState.value.type)) {
            FormViewModelState.Type.Edit -> {
                viewModelScope.launch {
                    // MEMO: 計測モデルの更新をしその後に紐づく写真を削除し新規登録する
                    bodyMeasureRepository.update(model)
                    photoRepository.deletePhotos(model.id)
                    val photoModels = viewModelState.value
                        .photos.map { it.copy(bodyMeasureId = model.id) }
                    photoRepository.insert(photoModels)
                }
            }

            FormViewModelState.Type.Add -> {
                viewModelScope.launch {
                    // MEMO: 計測モデルの登録しその後に紐づく写真を登録
                    val modelId = bodyMeasureRepository.insert(model)
                    val photoModels = viewModelState.value
                        .photos.map { it.copy(bodyMeasureId = modelId) }
                    photoRepository.insert(photoModels)
                }
            }
        }
    }

    fun deleteBodyMeasure() {
        assert(viewModelState.value.type == FormViewModelState.Type.Edit)
        val target = viewModelState.value.model?.capturedLocalDateTime ?: return
        viewModelScope.launch {
            runCatching { bodyMeasureRepository.deleteBodyMeasure(target) }
                .onFailure {
                    Timber.e(it)
                }.onSuccess {}
        }
    }
}
