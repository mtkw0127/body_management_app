package com.app.body_manage.ui.trainingMenu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.app.body_manage.data.model.createSampleOwnWeightTrainingMenu
import com.app.body_manage.data.model.createSampleTrainingMenu

class TrainingMenuListActivity : AppCompatActivity() {

    private lateinit var viewModel: TrainingMenuListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = TrainingMenuListViewModel()

        setContent {
            TrainingMenuListScreen(
                trainingMenus = List(3) {
                    createSampleTrainingMenu()
                } + List(3) {
                    createSampleOwnWeightTrainingMenu()
                },
                onClickBackPress = ::finish,
                onClickHistory = {

                },
                onClickEdit = {
                    
                }
            )
        }
    }

    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, TrainingMenuListActivity::class.java)
    }
}