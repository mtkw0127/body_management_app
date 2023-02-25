package com.app.body_manage.ui.measure.form

import android.app.Application
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.entity.BodyMeasureEntity
import com.app.body_manage.data.entity.BodyMeasureModel
import com.app.body_manage.data.entity.PhotoEntity
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.data.model.PhotoModel
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.data.repository.PhotoRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime

class BodyMeasureEditFormViewModel(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val formType: BodyMeasureEditFormActivity.FormType,
) : ViewModel() {

    val deleteButtonVisibility: Boolean =
        formType == BodyMeasureEditFormActivity.FormType.EDIT

    enum class PhotoType {
        Saved, ADDED
    }

    private val _photoList = MutableLiveData<MutableList<PhotoModel>>(mutableListOf())
    val photoList: LiveData<MutableList<PhotoModel>> = _photoList

    fun addPhotos(photoList: List<PhotoModel>) {
        _photoList.value?.addAll(photoList)
        _photoList.value = _photoList.value
    }

    fun deletePhoto(position: Int) {
        _photoList.value?.removeAt(position)
        _photoList.value = _photoList.value
    }

    var application: Application? = null
    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (application as TrainingApplication).bodyMeasureRepository
    }
    private val photoRepository: PhotoRepository by lazy {
        (application as TrainingApplication).photoRepository
    }

    // 更新後の測定日時
    private var fetchUserPref = false
    val fetchedUserPref = MutableLiveData(false)
    lateinit var measureTime: LocalDateTime
    var measureWeight = 50F
    var measureFat = 20.0F
    var tall: Float = 160F

    // coroutineによるローディング取得
    lateinit var bodyMeasureEntity: BodyMeasureEntity
    var loadedBodyMeasure = MutableLiveData(false)
    private var loadingBodyMeasure = MutableLiveData(false)
    fun loadBodyMeasure() {
        // ロード中はロードしない
        if (loadingBodyMeasure.value == true) return
        loadingBodyMeasure.value = true
        // 対象の日付に紐づくデータが存在すれば取得する.
        viewModelScope.launch {
            runCatching { bodyMeasureRepository.getEntityByCaptureTime(measureTime) }
                .onFailure { e ->
                    Toast.makeText(
                        application,
                        "読み込みに失敗しました。",
                        Toast.LENGTH_SHORT
                    ).show()
                    Timber.e(e)
                }
                .onSuccess { res ->
                    val it = res[0]
                    bodyMeasureEntity = it
                    measureTime = it.capturedTime
                    measureWeight = it.weight
                    measureFat = it.fatRate
                    loadedBodyMeasure.value = true
                }.also {
                    // ロード中終了
                    loadingBodyMeasure.value = false
                }
        }
    }

    var deleting = false
    val deleted = MutableLiveData(false)
    fun deleteBodyMeasure() {
        if (deleting) return
        deleting = true
        viewModelScope.launch {
            runCatching { bodyMeasureRepository.deleteBodyMeasure(measureTime) }
                .onFailure {
                    Timber.e(it)
                }
                .onSuccess {
                    deleted.value = true
                }
        }
    }

    private val loadingPhoto = MutableLiveData(false)
    fun loadPhotos() {
        if (loadingPhoto.value == true || deleting) return
        loadingPhoto.value = true
        viewModelScope.launch {
            runCatching { photoRepository.selectPhotos(bodyMeasureId = bodyMeasureEntity.ui) }
                .onFailure { e ->
                    Timber.e(e)
                    Toast.makeText(
                        application,
                        "写真の読み込みに失敗しました",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .onSuccess { photos ->
                    if (photos.isNotEmpty()) {
                        _photoList.value =
                            photos.map {
                                PhotoModel(
                                    id = PhotoModel.Id(it.ui),
                                    uri = it.photoUri.toUri(),
                                    photoType = PhotoType.Saved,
                                )
                            }.toList().toMutableList()
                    }
                }
                .also {
                    loadingPhoto.value = false
                }
        }
    }

    fun fetchTallAndUserPref(captureDate: LocalDate) {
        viewModelScope.launch {
            runCatching {
                bodyMeasureRepository.getTallByDate(captureDate)
            }.onFailure { e ->
                Timber.e(e)
            }.onSuccess {
                fetchUserPreference()
                if (it != null) {
                    tall = it
                }
            }
        }
    }

    private fun fetchUserPreference() {
        if (fetchUserPref) return
        fetchUserPref = true
        viewModelScope.launch {
            try {
                userPreferenceRepository.userPref.collect {
                    it.tall?.let { t ->
                        tall = t
                    }
                    // 新規追加の場合のみデフォルトの体重・体脂肪率を設定する
                    if (formType == BodyMeasureEditFormActivity.FormType.ADD) {
                        it.weight?.let { w ->
                            measureWeight = w
                        }
                        it.fat?.let { f ->
                            measureFat = f
                        }
                    }
                    fetchedUserPref.value = true
                }
            } catch (e: Throwable) {
                Timber.e(e)
            } finally {
                fetchUserPref = false
            }
        }
    }

    /**
     * 写真を登録
     */
    fun addPhoto(saveModel: BodyMeasureEntity) {
        viewModelScope.launch {
            runCatching {
                // 最新の写真をサムネイルに設定
                if (_photoList.value?.isNotEmpty() == true) {
                    saveModel.photoUri = checkNotNull(_photoList.value).last().uri.toString()
                }
                val id = bodyMeasureRepository.insert(saveModel)
                if (_photoList.value?.isNotEmpty() == true) {
                    photoRepository.insert(
                        createPhotoModels(id)
                    )
                }
            }.onFailure {
                Timber.e(it)
            }.onSuccess {
                println("")
            }
        }
    }

    /**
     * 写真を更新
     */
    fun editPhoto(saveModel: BodyMeasureEntity) {
        viewModelScope.launch {
            saveModel.ui = bodyMeasureEntity.ui
            // 紐づく写真を全件->再登録して更新
            runCatching {
                photoRepository.deletePhotos(bodyMeasureEntity.ui)
                if (_photoList.value?.isNotEmpty() == true) {
                    photoRepository.insert(
                        createPhotoModels(BodyMeasureModel.Id(bodyMeasureEntity.ui.toLong()))
                    )
                    // 最新の写真をサムネイルに設定
                    saveModel.photoUri = checkNotNull(_photoList.value).last().uri.toString()
                }
                bodyMeasureRepository.update(saveModel)
            }.onFailure { Timber.e(it) }
        }
    }

    /**
     * 写真のモデル生成
     */
    private fun createPhotoModels(bodyMeasureId: BodyMeasureModel.Id): List<PhotoEntity> {
        val photoList = checkNotNull(_photoList.value)
        return photoList.map {
            PhotoEntity(
                ui = 0,
                bodyMeasureId = bodyMeasureId.ui.toInt(),
                photoUri = it.uri.toString()
            )
        }
    }

    fun updateWeightAndFat() {
        viewModelScope.launch {
            runCatching {
                runBlocking {
                    userPreferenceRepository.putWeight(measureWeight)
                    userPreferenceRepository.putFat(measureFat)
                }
            }.onFailure {
                Timber.e(it)
            }.onSuccess { }
        }
    }
}