package com.app.body_manage.ui.selectTrainingMenu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.data.repository.TrainingRepository

class SelectTrainingMenuActivity : AppCompatActivity() {

    private lateinit var viewModel: SelectTrainingMenuViewModel

    private val trainingRepository: TrainingRepository
        get() = (application as TrainingApplication).trainingRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        viewModel = SelectTrainingMenuViewModel(trainingRepository)

        setContent {
            val trainingMenuList by viewModel.trainingMenuList.collectAsState()

            SelectTrainingMenuScreen(
                selectedPart = viewModel.selectedPart.collectAsState().value,
                selectedType = viewModel.selectedType.collectAsState().value,
                trainingMenuList = trainingMenuList,
                onClickMenu = { trainingMenu ->
                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                    intent.putExtra(SELECTED_TRAINING_MENU, trainingMenu)
                    finish()
                },
                onClickBackPress = ::finish,
                onClickPart = {
                    viewModel.updatePartFilter(it)
                },
                onClickType = {
                    viewModel.updateTypeFilter(it)
                },
            )
        }
        viewModel.loadMenu()
    }

    companion object {
        private const val SELECTED_TRAINING_MENU = "SELECTED_TRAINING_MENU"

        fun obtainResult(intent: Intent) =
            intent.getSerializableExtra(SELECTED_TRAINING_MENU) as TrainingMenu

        fun createInstance(context: Context): Intent {
            return Intent(context, SelectTrainingMenuActivity::class.java)
        }
    }
}
