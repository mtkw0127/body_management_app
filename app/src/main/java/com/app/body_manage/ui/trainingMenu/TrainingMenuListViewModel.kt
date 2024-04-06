package com.app.body_manage.ui.trainingMenu

import androidx.lifecycle.ViewModel
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.data.model.createSampleOwnWeightTrainingMenu
import com.app.body_manage.data.model.createSampleTrainingMenu
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TrainingMenuListViewModel : ViewModel() {

    private val _trainingMenuList: MutableStateFlow<List<TrainingMenu>> =
        MutableStateFlow(emptyList())
    val trainingMenuList: StateFlow<List<TrainingMenu>> = _trainingMenuList

    fun loadMenu() {
        val menu = List(3) {
            createSampleTrainingMenu()
        } + List(3) {
            createSampleOwnWeightTrainingMenu()
        }

        _trainingMenuList.value = menu
    }
}
