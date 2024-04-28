package com.app.body_manage.ui.trainingForm.form

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
import com.app.body_manage.dialog.IntNumberPickerDialog
import com.app.body_manage.dialog.TimePickerDialog
import com.app.body_manage.ui.measure.list.MeasureListActivity
import com.app.body_manage.ui.selectTrainingMenu.SelectTrainingMenuActivity
import com.app.body_manage.ui.trainingForm.TrainingFormScreen
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class TrainingFormActivity : AppCompatActivity() {

    private lateinit var viewModel: TrainingFormViewModel

    private val trainingMenuLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    val trainingMenu = SelectTrainingMenuActivity.obtainResult(data)
                    viewModel.addMenu(trainingMenu)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = TrainingFormViewModel(
            trainingRepository = (application as TrainingApplication).trainingRepository
        )

        viewModel.init(intent.getSerializableExtra(KEY_DATE) as LocalDate)

        lifecycleScope.launch {
            viewModel.isSuccessForSavingTraining.collect {
                setResult(MeasureListActivity.RESULT_CODE_ADD)
                finish()
            }
        }

        setContent {
            val training by viewModel.training.collectAsState()

            TrainingFormScreen(
                training = training,
                registerTextResourceId = R.string.label_register_training,
                onClickDeleteMenu = { menuIndex ->
                    viewModel.deleteMenu(menuIndex)
                },
                onClickRegister = {
                    if (viewModel.training.value.menus.isNotEmpty()) {
                        viewModel.registerTraining()
                    } else {
                        finish()
                    }
                },
                onClickBackPress = ::finish,
                onClickFab = {
                    trainingMenuLauncher.launch(SelectTrainingMenuActivity.createInstance(this))
                },
                onClickRep = { menuIndex, setIndex ->
                    val menu = viewModel.training.value.menus[menuIndex]
                    val set = menu.sets[setIndex]
                    IntNumberPickerDialog.createDialog(
                        label = getString(R.string.label_rep_num),
                        number = set.number,
                        unit = getString(R.string.label_count),
                        maxDigit = IntNumberPickerDialog.Digit.TENS,
                        initialDigit = IntNumberPickerDialog.Digit.TENS,
                        callBack = { number ->
                            viewModel.updateRep(menuIndex, setIndex, number)
                        }
                    ).show(supportFragmentManager, "rep")
                },
                onClickWeight = { menuIndex, setIndex ->
                    val menu = viewModel.training.value.menus[menuIndex]
                    val set = menu.sets[setIndex]
                    IntNumberPickerDialog.createDialog(
                        label = getString(R.string.label_weight),
                        number = set.number,
                        unit = getString(R.string.label_weight_unit),
                        maxDigit = IntNumberPickerDialog.Digit.TENS,
                        initialDigit = IntNumberPickerDialog.Digit.TENS,
                        callBack = { weight ->
                            viewModel.updateWeight(menuIndex, setIndex, weight)
                        }
                    ).show(supportFragmentManager, "weight")
                },
                onClickDelete = { menuIndex, setIndex ->
                    viewModel.deleteSet(menuIndex, setIndex)
                },
                onClickStartTime = {
                    TimePickerDialog.createTimePickerDialog(
                        localTime = training.startTime,
                    ) { hour, minute ->
                        viewModel.updateStartTime(LocalTime.of(hour, minute))
                    }.show(supportFragmentManager, "start_time")
                },
                onClickEndTime = {
                    TimePickerDialog.createTimePickerDialog(
                        localTime = training.endTime,
                    ) { hour, minute ->
                        viewModel.updateEndTime(LocalTime.of(hour, minute))
                    }.show(supportFragmentManager, "end_time")
                },
                onClickTrainingDelete = null, //登録画面では削除できない
            )
        }
    }

    companion object {
        private const val KEY_DATE = "KEY_DATE"

        fun createInstance(context: Context, date: LocalDate): Intent {
            return Intent(context, TrainingFormActivity::class.java)
                .putExtra(KEY_DATE, date)
        }
    }
}
