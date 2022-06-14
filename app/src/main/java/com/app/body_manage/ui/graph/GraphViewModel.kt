package com.app.body_manage.ui.graph

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.body_manage.TrainingApplication
import com.app.body_manage.model.BodyMeasureEntity
import com.app.body_manage.repository.BodyMeasureRepository
import com.github.mikephil.charting.data.Entry
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlinx.coroutines.launch

class GraphViewModel(application: Application) : AndroidViewModel(application) {

    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (application as TrainingApplication).bodyMeasureRepository
    }

    var weightEntryList = MutableLiveData<List<Entry>>(mutableListOf())
    var fatEntryList = MutableLiveData<List<Entry>>(mutableListOf())

    private fun createEntryList(bodyMeasureList: List<BodyMeasureEntity>) {
        weightEntryList.value = bodyMeasureList.asSequence()
            .map {
                Entry().apply {
                    this.x = it.capturedTime.toEpochSecond(ZoneOffset.UTC).toFloat()
                    this.y = it.weight
                }
            }.toList()

        fatEntryList.value = bodyMeasureList.asSequence()
            .map {
                Entry().apply {
                    this.x = it.capturedTime.toEpochSecond(ZoneOffset.UTC).toFloat()
                    this.y = it.fatRate
                }
            }.toList()
    }

    private var loadBodyMeasure = false
    fun loadBodyMeasure(startDateTime: LocalDateTime? = null, endDateTime: LocalDateTime? = null) {
        if (loadBodyMeasure) return
        loadBodyMeasure = true

        viewModelScope.launch {
            runCatching { bodyMeasureRepository.getEntityListBetween(startDateTime, endDateTime) }
                .onFailure { e -> e.printStackTrace() }
                .onSuccess {
                    createEntryList(bodyMeasureList = it)
                }
                .also {
                    loadBodyMeasure = false
                }
        }
    }
}