package com.app.body_manage.ui.trainingForm.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.app.body_manage.R
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.model.Training
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.dialog.Digit
import com.app.body_manage.dialog.FloatNumberPickerDialog
import com.app.body_manage.dialog.IntNumberPickerDialog
import com.app.body_manage.dialog.TimePickerDialog
import com.app.body_manage.ui.measure.list.MeasureListActivity.Companion.RESULT_CODE_DELETE
import com.app.body_manage.ui.measure.list.MeasureListActivity.Companion.RESULT_CODE_EDIT
import com.app.body_manage.ui.selectTrainingMenu.SelectTrainingMenuActivity
import com.app.body_manage.ui.trainingForm.TrainingFormScreen
import java.time.LocalTime

class TrainingDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: TrainingDetailViewModel

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

        viewModel = TrainingDetailViewModel(
            trainingRepository = (application as TrainingApplication).trainingRepository
        )

        viewModel.init(
            training = checkNotNull(intent.getSerializableExtra(KEY_TRAINING) as? Training)
        )

        setContent {
            val training by viewModel.training.collectAsState()

            TrainingFormScreen(
                training = training,
                registerTextResourceId = R.string.label_update_training,
                updateMemo = viewModel::updateMemo,
                onClickBackPress = ::finish,
                onClickDeleteMenu = { menuIndex ->
                    viewModel.deleteMenu(menuIndex)
                },
                onClickFab = {
                    trainingMenuLauncher.launch(SelectTrainingMenuActivity.createInstance(this))
                },
                onClickDelete = { menuIndex, setIndex ->
                    viewModel.deleteSet(menuIndex, setIndex)
                },
                onClickStartTime = {
                    val time = training.startTime
                    TimePickerDialog.createTimePickerDialog(
                        localTime = time,
                    ) { hour, minute ->
                        viewModel.updateStartTime(LocalTime.of(hour, minute))
                    }.show(supportFragmentManager, "start_time")
                },
                onClickEndTime = {
                    val time = training.endTime
                    TimePickerDialog.createTimePickerDialog(
                        localTime = time,
                    ) { hour, minute ->
                        viewModel.updateEndTime(LocalTime.of(hour, minute))
                    }.show(supportFragmentManager, "end_time")
                },
                onClickRep = { menuIndex, setIndex ->
                    val menu = viewModel.training.value.menus[menuIndex]
                    val set = menu.sets[setIndex]
                    if (set is TrainingMenu.Set) {
                        IntNumberPickerDialog.createDialog(
                            label = getString(R.string.label_rep_num),
                            number = set.number,
                            unit = getString(R.string.label_count),
                            maxDigit = Digit.TENS,
                            initialDigit = Digit.TENS,
                            callBack = { number ->
                                viewModel.updateRep(menuIndex, setIndex, number)
                            }
                        ).show(supportFragmentManager, "rep")
                    }
                },
                onClickWeight = { menuIndex, setIndex ->
                    val menu = viewModel.training.value.menus[menuIndex]
                    val set = menu.sets[setIndex]
                    if (set is TrainingMenu.Set) {
                        IntNumberPickerDialog.createDialog(
                            label = getString(R.string.label_weight),
                            number = set.number,
                            unit = getString(R.string.label_weight_unit),
                            maxDigit = Digit.TENS,
                            initialDigit = Digit.TENS,
                            callBack = { weight ->
                                viewModel.updateWeight(menuIndex, setIndex, weight)
                            }
                        ).show(supportFragmentManager, "weight")
                    }
                },
                onClickRegister = {
                    viewModel.updateTraining()
                    setResult(RESULT_CODE_EDIT)
                    finish()
                },
                onClickTrainingDelete = {
                    viewModel.deleteTraining()
                    setResult(RESULT_CODE_DELETE)
                    finish()
                },
                onClickCardioDistance = { menuIndex, index ->
                    val menu = viewModel.training.value.menus[menuIndex]
                    val set = menu.sets[index]
                    if (set is TrainingMenu.CardioSet) {
                        FloatNumberPickerDialog.createDialog(
                            label = getString(R.string.label_distance),
                            number = set.distance,
                            unit = getString(R.string.label_distance_unit),
                            initialDigit = Digit.ONES,
                            callBack = { distance ->
                                viewModel.updateCardioDistance(menuIndex, distance)
                            },
                            supportOneHundred = true,
                        ).show(supportFragmentManager, "distance")
                    }
                },
                onClickCardioMinutes = { menuIndex, index ->
                    val menu = viewModel.training.value.menus[menuIndex]
                    val set = menu.sets[index]
                    if (set is TrainingMenu.CardioSet) {
                        IntNumberPickerDialog.createDialog(
                            label = getString(R.string.label_minutes),
                            number = set.minutes,
                            unit = getString(R.string.label_minute_unit),
                            maxDigit = Digit.HUNDRED,
                            initialDigit = Digit.TENS,
                            callBack = { minutes ->
                                viewModel.updateCardioMinutes(menuIndex, minutes)
                            }
                        ).show(supportFragmentManager, "minutes")
                    }
                }
            )
        }
    }

    companion object {
        private const val KEY_TRAINING = "KEY_TRAINING"
        fun createInstance(context: Context, training: Training): Intent =
            Intent(context, TrainingDetailActivity::class.java)
                .putExtra(KEY_TRAINING, training)
    }
}
