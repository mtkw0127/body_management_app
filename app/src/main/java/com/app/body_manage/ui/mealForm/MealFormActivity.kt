package com.app.body_manage.ui.mealForm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.app.body_manage.TrainingApplication

class MealFormActivity : AppCompatActivity() {

    lateinit var viewModel: MealFormViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = MealFormViewModel(
            mealRepository = (application as TrainingApplication).mealFoodsRepository
        )

        setContent {
            val mealFoods by viewModel.mealFoods.collectAsState()
            val foodCandidates by viewModel.foodCandidates.collectAsState()
            MealFormScreen(
                mealFoods = mealFoods,
                foodCandidates = foodCandidates,
                onClickTime = {},
                onClickMealTiming = viewModel::updateTiming,
                onSearchTextChange = viewModel::searchFood,
                onClickSave = viewModel::save,
                onClickSearchedFood = viewModel::addFood,
                onClickDeleteFood = viewModel::removeFood,
                onClickBackPress = this@MealFormActivity::finish,
                onClickTakePhoto = {},
            )
        }
    }

    companion object {
        fun createIntent(context: Context) = Intent(context, MealFormActivity::class.java)
    }
}
