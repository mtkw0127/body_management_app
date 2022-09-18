package com.app.body_manage.ui.measure.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.dao.BodyMeasurePhotoDao
import com.app.body_manage.data.entity.BodyMeasureModel
import com.app.body_manage.data.entity.MealMeasureEntity
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.data.repository.BodyMeasurePhotoRepository
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.ui.measure.list.MeasureListState.BodyMeasureListState
import com.app.body_manage.ui.measure.list.MeasureListState.MealMeasureListState
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

sealed interface MeasureListState {
    val date: LocalDate
    val measureType: MeasureType

    data class BodyMeasureListState(
        val list: List<BodyMeasureModel>,
        val tall: String,
        val loading: Boolean,
        val message: String,
        override val measureType: MeasureType,
        override val date: LocalDate,
        val photoList: List<BodyMeasurePhotoDao.PhotoData>,
    ) : MeasureListState

    data class MealMeasureListState(
        val list: List<MealMeasureEntity>,
        override val measureType: MeasureType,
        override val date: LocalDate
    ) : MeasureListState

    data class CommonMeasureListState(
        val list: List<MealMeasureEntity>,
        override val measureType: MeasureType,
        override val date: LocalDate
    ) : MeasureListState
}

internal data class MeasureListViewModelState(
    val date: LocalDate,
    val measureType: MeasureType,
    val bodyMeasureList: List<BodyMeasureModel> = listOf(),
    val mealMeasureList: List<MealMeasureEntity> = listOf(),
    val photoList: List<BodyMeasurePhotoDao.PhotoData> = listOf(),
    val tall: String = 150.0F.toString(),
    val updateTall: Boolean = false,
    val loadingTall: Boolean = false,
    val message: String = ""
) {
    private val someLoading = updateTall || loadingTall

    fun toUiState(): MeasureListState {
        return when (measureType) {
            MeasureType.BODY -> {
                BodyMeasureListState(
                    date = date,
                    list = bodyMeasureList,
                    photoList = photoList,
                    tall = tall,
                    measureType = measureType,
                    loading = someLoading,
                    message = message,
                )
            }
            MeasureType.MEAL -> {
                MealMeasureListState(
                    date = date,
                    list = mealMeasureList,
                    measureType = measureType,
                )
            }
            else -> {
                MeasureListState.CommonMeasureListState(
                    date = date,
                    list = mealMeasureList,
                    measureType = measureType,
                )
            }
        }
    }
}

class MeasureListViewModel(
    private val localDate: LocalDate,
    private val mealType: MeasureType,
    private val bodyMeasureRepository: BodyMeasureRepository,
    private val bodyMeasurePhotoRepository: BodyMeasurePhotoRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        MeasureListViewModelState(
            date = localDate,
            measureType = mealType,
        )
    )
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun setDate(localDate: LocalDate) {
        viewModelState.update {
            it.copy(
                date = localDate
            )
        }
        reload()
    }

    private fun viewModelStateLoadingUpdate(updateTall: Boolean? = null, loading: Boolean? = null) {
        updateTall?.let { it1 ->
            viewModelState.update {
                it.copy(updateTall = it1)
            }
        }
        loading?.let { it2 ->
            viewModelState.update {
                it.copy(loadingTall = it2)
            }
        }
    }

    fun reload() {
        when (viewModelState.value.measureType) {
            MeasureType.BODY -> {
                runBlocking {
                    loadTall()
                    loadBodyMeasure()
                    loadPhoto()
                }
            }
            MeasureType.MEAL -> {
                loadMealMeasureList()
            }
            else -> {}
        }
    }

    fun updateMessage(message: String) {
        viewModelState.update {
            it.copy(message = message)
        }
    }

    fun resetMessage() {
        viewModelState.update {
            it.copy(message = "")
        }
    }

    fun updateTall() {
        if (viewModelState.value.updateTall) return
        viewModelStateLoadingUpdate(updateTall = true)
        val tall = viewModelState.value.tall.toFloat()
        viewModelScope.launch {
            runCatching {
                bodyMeasureRepository.updateTallByDate(
                    tall = tall,
                    calendarDate = localDate,
                )
            }.onFailure {
                Timber.e(it)
            }.onSuccess {
                updateUserPrefTall(tall)
                reload()
                updateMessage("身長を更新し、BMIを再計算しました")
            }.also {
                viewModelStateLoadingUpdate(updateTall = false)
            }
        }
    }

    private fun loadPhoto() {
        viewModelScope.launch {
            runCatching { bodyMeasurePhotoRepository.selectPhotosByDate(viewModelState.value.date) }
                .onFailure { Timber.e(it) }
                .onSuccess { dbResponse ->
                    viewModelState.update {
                        it.copy(photoList = dbResponse)
                    }
                }
        }
    }

    private fun updateUserPrefTall(tall: Float) {
        viewModelScope.launch {
            runCatching { userPreferenceRepository.putTall(tall) }
                .onFailure {
                    Timber.e(it)
                }
                .onSuccess {}
        }
    }

    fun setTall(tall: String) {
        viewModelState.update {
            it.copy(tall = tall)
        }
    }

    private fun loadTall() {
        viewModelScope.launch {
            userPreferenceRepository.userPref.collect {
                if (it.tall != null) {
                    setTall(it.tall.toString())
                }
            }
        }
    }

    private fun loadBodyMeasure() {
        if (viewModelState.value.loadingTall) return
        viewModelStateLoadingUpdate(loading = true)
        viewModelScope.launch {
            runCatching {
                bodyMeasureRepository.getEntityListByDate(viewModelState.value.date)
            }.onFailure { e ->
                Timber.e(e)
            }.onSuccess { loadedResult ->
                // 当日の記録の身長を優先して利用する、未設定の場合は前回保存の身長を利用する。
                var tall = loadedResult.firstOrNull()?.tall
                if (tall == null) tall = viewModelState.value.tall.toFloat()
                viewModelState.update {
                    it.copy(
                        date = localDate,
                        measureType = mealType,
                        bodyMeasureList = loadedResult,
                        mealMeasureList = mutableListOf(),
                        tall = tall.toString()
                    )
                }
            }.also {
                viewModelStateLoadingUpdate(loading = false)
            }
        }
    }

    private fun loadMealMeasureList() {
        val result = listOf(
            MealMeasureEntity(ui = 1),
            MealMeasureEntity(ui = 2),
            MealMeasureEntity(ui = 3)
        )
        viewModelState.update {
            it.copy(
                mealMeasureList = result
            )
        }
    }

}