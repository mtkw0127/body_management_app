package com.app.body_manage.ui.trainingForm

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
import com.app.body_manage.ui.selectTrainingMenu.SelectTrainingMenuActivity
import kotlinx.coroutines.launch

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

        lifecycleScope.launch {
            viewModel.isSuccessForSavingTraining.collect {
                finish()
            }
        }

        setContent {
            val training by viewModel.training.collectAsState()

            TrainingFormScreen(
                training = training,
                onClickRegister = viewModel::registerTraining,
                onClickBackPress = ::finish,
                onClickFab = {
                    trainingMenuLauncher.launch(SelectTrainingMenuActivity.createInstance(this))
                }
            )
        }
    }

    companion object {
        fun createInstance(context: Context): Intent {
            return Intent(context, TrainingFormActivity::class.java)
        }
    }
}
