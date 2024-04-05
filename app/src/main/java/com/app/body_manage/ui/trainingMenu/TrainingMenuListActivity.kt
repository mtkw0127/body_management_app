package com.app.body_manage.ui.trainingMenu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState

class TrainingMenuListActivity : AppCompatActivity() {

    private lateinit var viewModel: TrainingMenuListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = TrainingMenuListViewModel()

        setContent {
            val trainingMenuList = viewModel.trainingMenuList.collectAsState()

            TrainingMenuListScreen(
                trainingMenus = trainingMenuList.value,
                onClickBackPress = ::finish,
//                onClickHistory = {
//                },
                onClickEdit = {
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
