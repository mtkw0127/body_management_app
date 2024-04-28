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
import com.app.body_manage.R
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.model.Meal
import com.app.body_manage.data.model.MealPhoto
import com.app.body_manage.dialog.IntNumberPickerDialog
import com.app.body_manage.dialog.TimePickerDialog
import com.app.body_manage.ui.camera.CameraActivity
import com.app.body_manage.ui.measure.list.MeasureListActivity
import com.app.body_manage.ui.photoDetail.PhotoDetailActivity
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
                    photoList.map { uri -> MealPhoto(uri = uri) }.toList()
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
            MealFormScreen(
                type = viewModel.type,
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
                onClickDeleteForm = viewModel::deleteForm,
                onUpdateMealKcal = { food ->
                    IntNumberPickerDialog.createDialog(
                        label = getString(R.string.kcal),
                        number = food.kcal.toLong(),
                        unit = getString(R.string.unit_kcal),
                        maxDigit = IntNumberPickerDialog.Digit.THOUSAND,
                        initialDigit = IntNumberPickerDialog.Digit.HUNDRED,
                    ) { kcal ->
                        viewModel.updateFood(food, kcal)
                    }.show(supportFragmentManager, null)
                },
                onUpdateMealNumber = { food ->
                    IntNumberPickerDialog.createDialog(
                        label = getString(R.string.label_number),
                        number = food.number,
                        unit = getString(R.string.unit_number),
                        maxDigit = IntNumberPickerDialog.Digit.ONES,
                        initialDigit = IntNumberPickerDialog.Digit.ONES,
                    ) { number ->
                        viewModel.updateFoodNumber(food, number)
                    }.show(supportFragmentManager, null)
                },
                onClickPhotoDetail = {
                    startActivity(
                        PhotoDetailActivity.createIntent(
                            this,
                            it.uri,
                        )
                    )
                },
                onClickDeletePhoto = {
                    viewModel.deletePhoto(it)
                }
            )
        }
    }

    private fun initListener() {
        lifecycleScope.launch {
            viewModel.saved.collectLatest {
                if (it) {
                    val resultCode = when (viewModel.type) {
                        MealFormViewModel.Type.Add -> MeasureListActivity.RESULT_CODE_ADD
                        MealFormViewModel.Type.Edit -> MeasureListActivity.RESULT_CODE_EDIT
                    }
                    setResult(resultCode)
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.deleted.collectLatest {
                if (it) {
                    setResult(MeasureListActivity.RESULT_CODE_DELETE)
                    finish()
                }
            }
        }
    }

    companion object {
        const val KEY_DATE = "KEY_DATE"
        const val KEY_MEAL_ID = "KEY_MEAL_ID"
        const val KEY_TYPE = "KEY_TYPE"

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
