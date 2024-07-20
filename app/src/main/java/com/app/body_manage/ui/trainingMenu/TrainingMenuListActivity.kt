package com.app.body_manage.ui.trainingMenu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.core.view.WindowCompat
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.repository.TrainingRepository

class TrainingMenuListActivity : AppCompatActivity() {

    private lateinit var viewModel: TrainingMenuListViewModel

    private val trainingRepository: TrainingRepository
        get() = (application as TrainingApplication).trainingRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        viewModel = TrainingMenuListViewModel(trainingRepository)

        setContent {
            val trainingMenuList = viewModel.trainingMenuList.collectAsState()

            TrainingMenuListScreen(
                selectedPart = viewModel.selectedPart.collectAsState().value,
                selectedType = viewModel.selectedType.collectAsState().value,
                trainingMenus = trainingMenuList.value,
                onClickBackPress = ::finish,
                onSaveMenu = {
                    viewModel.saveTrainingMenu(it)
                },
                onEditMenu = {
                    viewModel.updateTrainingMenu(it)
                },
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
        fun createIntent(context: Context): Intent =
            Intent(context, TrainingMenuListActivity::class.java)
    }
}
