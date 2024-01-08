package com.app.body_manage.ui.mealForm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.model.Food
import com.app.body_manage.data.model.Meal
import com.app.body_manage.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MealFormViewModel(
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _mealFoods: MutableStateFlow<Meal> = MutableStateFlow(Meal.init())
    val mealFoods = _mealFoods.stateIn(viewModelScope, SharingStarted.Eagerly, Meal.init())

    private val _foodCandidates: MutableStateFlow<List<Food>> = MutableStateFlow(emptyList())
    val foodCandidates =
        _foodCandidates.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun save() {
        viewModelScope.launch {
            mealRepository.saveMeal(_mealFoods.value)
        }
    }

    fun addFood(food: Food) {
        _mealFoods.update { meal ->
            meal.copy(foods = meal.foods + food)
        }
        // 追加した食べ物は候補から除外する
        _foodCandidates.value = _foodCandidates.value.filter { it.name != food.name }
    }

    fun removeFood(food: Food) {
        _mealFoods.update { meal ->
            meal.copy(foods = meal.foods.filterNot { it == food })
        }
    }

    fun updateTiming(timing: Meal.Timing) {
        _mealFoods.update { meal ->
            meal.copy(timing = timing)
        }
    }

    fun searchFood(text: String) {
        if (text.isEmpty()) {
            _foodCandidates.value = emptyList()
            return
        }
        viewModelScope.launch {
            val searchResults = mealRepository.getFoods(text).toMutableList()
            // 完全に一致したものがない場合は新規追加として追加
            if (
                searchResults.any { it.name == text }.not() &&
                _mealFoods.value.foods.any { it.name == text }.not()
            ) {
                searchResults.add(Food.createNewFood(text))
            }
            _foodCandidates.value = searchResults
        }
    }
}
