package com.app.body_manage.ui.graph

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.entity.BodyMeasureEntity
import com.app.body_manage.data.repository.BodyMeasureRepository
import java.io.Serializable
import java.time.LocalDateTime
import kotlinx.coroutines.launch

class GraphViewModel(application: Application) : AndroidViewModel(application) {

    data class MyEntry(val axisLocalDateTime: LocalDateTime, val y: Float) : Serializable

    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (application as TrainingApplication).bodyMeasureRepository
    }

    var entryList = MutableLiveData<MutableList<List<MyEntry>>>(mutableListOf())

    private fun createEntryList(bodyMeasureList: List<BodyMeasureEntity>) {
        val weight = bodyMeasureList.asSequence()
            .map {
                MyEntry(axisLocalDateTime = it.capturedTime, y = it.weight)
            }.toList()

        val fat = bodyMeasureList.asSequence()
            .map {
                MyEntry(axisLocalDateTime = it.capturedTime, y = it.fatRate)
            }.toList()

        entryList.value?.add(weight)
        entryList.value?.add(fat)
        entryList.value = entryList.value
    }

    private var loadBodyMeasure = false
    fun loadBodyMeasure() {
        if (loadBodyMeasure) return
        loadBodyMeasure = true

        viewModelScope.launch {
            runCatching {
                bodyMeasureRepository.getEntityListBetween()
            }
                .onFailure { e ->
                    e.printStackTrace()
                }
                .onSuccess {
                    createEntryList(bodyMeasureList = it)
                }
                .also {
                    loadBodyMeasure = false
                }
        }
    }
}