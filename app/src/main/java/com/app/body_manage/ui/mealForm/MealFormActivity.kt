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
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = MealFormViewModel(
            mealRepository = (application as TrainingApplication).mealFoodsRepository
        )
        val type = checkNotNull(intent.getSerializableExtra(KEY_TYPE) as? MealFormViewModel.Type)
        val date = checkNotNull(intent.getSerializableExtra(KEY_DATE) as? LocalDate)

        viewModel.init(
            type,
            date,
        )

        lifecycleScope.launch {
            viewModel.saved.collectLatest {
                if (it) {
                    setResult(RESULT_KEY_MEAL_ADD)
                    finish()
                }
            }
        }

        setContent {
            val mealFoods by viewModel.mealFoods.collectAsState()
            val foodCandidates by viewModel.foodCandidates.collectAsState()
            MealFormScreen(
                mealFoods = mealFoods,
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
            )
        }
    }

    companion object {
        private const val KEY_DATE = "KEY_DATE"
        private const val KEY_TYPE = "KEY_TYPE"

        const val RESULT_KEY_MEAL_ADD = Activity.RESULT_FIRST_USER + 100

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
            localDate: LocalDate,
        ) = Intent(
            context,
            MealFormActivity::class.java
        ).apply {
            putExtra(KEY_DATE, localDate)
            putExtra(KEY_TYPE, MealFormViewModel.Type.Edit)
        }
    }
}
