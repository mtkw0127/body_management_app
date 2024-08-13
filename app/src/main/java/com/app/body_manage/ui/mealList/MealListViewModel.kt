package com.app.body_manage.ui.mealList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.model.Food
import com.app.body_manage.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class MealListViewModel(
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _foods: MutableStateFlow<List<Food>> = MutableStateFlow(emptyList())
    val foods: StateFlow<List<Food>> = _foods

    fun reload() {
        viewModelScope.launch {
            try {
                _foods.value = mealRepository.getAllFoods().sortedByDescending { it.kcal }
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }
    }
}
