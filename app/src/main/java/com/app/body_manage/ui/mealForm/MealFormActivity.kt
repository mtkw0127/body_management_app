package com.app.body_manage.ui.mealForm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.model.Meal
import com.app.body_manage.data.model.PhotoModel
import com.app.body_manage.dialog.TimePickerDialog
import com.app.body_manage.ui.camera.CameraActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class MealFormActivity : AppCompatActivity() {

    lateinit var viewModel: MealFormViewModel

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val photoList = CameraActivity.photoList.toList()
                val photoModels =
                    photoList.map { uri -> PhotoModel(uri = uri) }.toList()
                viewModel.addPhotos(photoModels)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = MealFormViewModel(
            mealRepository = (application as TrainingApplication).mealFoodsRepository
        )

        viewModel.init(intent)

        initListener()

        setContent {
            val mealFoods by viewModel.mealFoods.collectAsState()
            val foodCandidates by viewModel.foodCandidates.collectAsState()
            val photos by viewModel.photos.collectAsState()
            MealFormScreen(
                type = viewModel.type,
                mealFoods = mealFoods,
                photos = photos,
                foodCandidates = foodCandidates,
                onClickTime = {
                    TimePickerDialog.createTimePickerDialog(mealFoods.time) { hour, minute ->
                        viewModel.updateTime(LocalTime.of(hour, minute))
                    }.show(supportFragmentManager, null)
                },
                onClickMealTiming = viewModel::updateTiming,
                onSearchTextChange = viewModel::searchFood,
                onClickSave = viewModel::save,
                onClickSearchedFood = viewModel::addFood,
                onClickDeleteFood = viewModel::removeFood,
                onClickBackPress = this@MealFormActivity::finish,
                onClickTakePhoto = {
                    cameraLauncher.launch(CameraActivity.createCameraActivityIntent(this))
                },
                onClickDeleteForm = viewModel::deleteForm,
                onUpdateMealKcal = { food, kcal ->
                    viewModel.updateFood(food, kcal)
                }
            )
        }
    }

    private fun initListener() {
        lifecycleScope.launch {
            viewModel.saved.collectLatest {
                if (it) {
                    val resultCode = when (viewModel.type) {
                        MealFormViewModel.Type.Add -> RESULT_KEY_MEAL_ADD
                        MealFormViewModel.Type.Edit -> RESULT_KEY_MEAL_EDIT
                    }
                    setResult(resultCode)
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.deleted.collectLatest {
                if (it) {
                    setResult(RESULT_KEY_MEAL_DELETE)
                    finish()
                }
            }
        }
    }

    companion object {
        const val KEY_DATE = "KEY_DATE"
        const val KEY_MEAL_ID = "KEY_MEAL_ID"
        const val KEY_TYPE = "KEY_TYPE"

        const val RESULT_KEY_MEAL_ADD = Activity.RESULT_FIRST_USER + 100
        const val RESULT_KEY_MEAL_EDIT = Activity.RESULT_FIRST_USER + 101
        const val RESULT_KEY_MEAL_DELETE = Activity.RESULT_FIRST_USER + 102

        fun createIntentAdd(
            context: Context,
            localDate: LocalDate,
        ) = Intent(
            context,
            MealFormActivity::class.java
        ).apply {
            putExtra(KEY_DATE, localDate)
            putExtra(KEY_TYPE, MealFormViewModel.Type.Add)
        }

        fun createIntentEdit(
            context: Context,
            mealId: Meal.Id,
        ) = Intent(
            context,
            MealFormActivity::class.java
        ).apply {
            putExtra(KEY_MEAL_ID, mealId)
            putExtra(KEY_TYPE, MealFormViewModel.Type.Edit)
        }
    }
}
