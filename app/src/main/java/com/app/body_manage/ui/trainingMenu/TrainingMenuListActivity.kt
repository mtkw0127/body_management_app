package com.app.body_manage.ui.trainingMenu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.repository.TrainingRepository

class TrainingMenuListActivity : AppCompatActivity() {

    private lateinit var viewModel: TrainingMenuListViewModel

    private val trainingRepository: TrainingRepository
        get() = (application as TrainingApplication).trainingRepository

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = TrainingMenuListViewModel(trainingRepository)

        setContent {
            val trainingMenuList = viewModel.trainingMenuList.collectAsState()

            TrainingMenuListScreen(
                trainingMenus = trainingMenuList.value,
                onClickBackPress = ::finish,
                onSaveMenu = {
                    viewModel.saveTrainingMenu(it)
                },
                onEditMenu = {
                    viewModel.updateTrainingMenu(it)
                }
            )
        }

        viewModel.loadMenu()
    }

    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, TrainingMenuListActivity::class.java)
    }
}
