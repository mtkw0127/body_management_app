package com.app.body_manage.ui.mealForm

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.model.Food
import com.app.body_manage.data.model.Meal
import com.app.body_manage.data.model.MealPhoto
import com.app.body_manage.data.model.Photo
import com.app.body_manage.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MealFormViewModel(
    private val mealRepository: MealRepository
) : ViewModel() {

    enum class Type {
        Add, Edit
    }

    private val _mealFoods: MutableStateFlow<Meal> = MutableStateFlow(Meal.init())
    val mealFoods = _mealFoods.stateIn(viewModelScope, SharingStarted.Eagerly, Meal.init())

    private val _foodCandidates: MutableStateFlow<List<Food>> = MutableStateFlow(emptyList())
    val foodCandidates =
        _foodCandidates.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    private val _deleted = MutableStateFlow(false)
    val deleted: StateFlow<Boolean> = _deleted

    lateinit var type: Type

    fun init(intent: Intent) {
        type = checkNotNull(intent.getSerializableExtra(MealFormActivity.KEY_TYPE) as? Type)
        when (type) {
            Type.Add -> {
                val date =
                    checkNotNull(intent.getSerializableExtra(MealFormActivity.KEY_DATE) as? LocalDate)
                _mealFoods.update {
                    it.copy(time = LocalDateTime.of(date, it.time.toLocalTime()))
                }
            }

            Type.Edit -> {
                viewModelScope.launch {
                    val id =
                        checkNotNull(intent.getSerializableExtra(MealFormActivity.KEY_MEAL_ID) as? Meal.Id)
                    val meal = checkNotNull(mealRepository.getMeal(id))
                    _mealFoods.update { meal }
                }
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            when (type) {
                Type.Add -> {
                    mealRepository.saveMeal(_mealFoods.value)
                }

                Type.Edit -> {
                    mealRepository.updateMeal(_mealFoods.value)
                }
            }
            _saved.value = true
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
        val trimText = text.trim()
        if (trimText.isEmpty()) {
            _foodCandidates.value = emptyList()
            return
        }
        viewModelScope.launch {
            val searchResults = mealRepository.getFoods(trimText).toMutableList()
            // 完全に一致したものがない場合は新規追加として追加
            if (
                searchResults.any { it.name == trimText }.not() &&
                _mealFoods.value.foods.any { it.name == trimText }.not()
            ) {
                searchResults.add(Food.createNewFood(trimText))
            }
            // その日の食事に登録済みのものは除く
            val filtered =
                searchResults.filterNot { _mealFoods.value.foods.contains(it) }.toMutableList()
            _foodCandidates.value = filtered
        }
    }

    fun updateTime(time: LocalTime) {
        _mealFoods.update {
            val dateTime = LocalDateTime.of(it.time.toLocalDate(), time)
            it.copy(time = dateTime)
        }
    }

    fun deleteForm() {
        viewModelScope.launch {
            mealRepository.deleteMeal(_mealFoods.value)
            _deleted.value = true
        }
    }

    fun updateFood(targetFood: Food, kcal: Long) {
        _mealFoods.update {
            val updatedFoods = it.foods.map { food ->
                if (targetFood.name == food.name && targetFood.id == food.id) {
                    food.copy(kcal = kcal)
                } else {
                    food
                }
            }
            it.copy(foods = updatedFoods)
        }
    }

    fun addPhotos(photoModels: List<MealPhoto>) {
        _mealFoods.update { meal ->
            meal.copy(photos = meal.photos + photoModels)
        }
    }

    fun updateFoodNumber(targetFood: Food, number: Long) {
        _mealFoods.update {
            val updatedFood = targetFood.copy(number = number)
            val updatedFoods = it.foods.map { food ->
                if (food.name == targetFood.name) {
                    updatedFood
                } else {
                    food
                }
            }
            it.copy(foods = updatedFoods)
        }
    }

    fun deletePhoto(targetPhoto: Photo) {
        _mealFoods.update { meal ->
            val photos = meal.photos.filterNot { photo ->
                photo.id == targetPhoto.id && photo.uri == targetPhoto.uri
            }
            meal.copy(photos = photos)
        }
    }
}
