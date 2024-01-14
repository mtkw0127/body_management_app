package com.app.body_manage.ui.statistics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.data.repository.BodyMeasureRepository

class StatisticsActivity : AppCompatActivity() {

    lateinit var viewModel: StatisticsViewModel

    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (application as TrainingApplication).bodyMeasureRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = StatisticsViewModel(UserPreferenceRepository((this)), bodyMeasureRepository)
        viewModel.init()
        setContent {
            val userPreference by viewModel.userPreference.collectAsState()
            val lastMeasure by viewModel.latestMeasure.collectAsState()
            StatisticsScreen(
                userPreference = userPreference,
                bodyMeasure = lastMeasure,
                onClickBackPress = ::finish
            )
        }
    }

    companion object {
        fun createIntent(context: Context) = Intent(context, StatisticsActivity::class.java)
    }
}