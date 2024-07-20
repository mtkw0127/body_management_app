package com.app.body_manage.ui.calendar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.app.body_manage.TrainingApplication
import com.app.body_manage.ui.measure.list.MeasureListActivity

class CalendarActivity : AppCompatActivity() {

    private val measureListLauncher = registerForActivityResult(StartActivityForResult()) {
        viewModel.updateCurrentMonth()
    }

    lateinit var viewModel: CalendarListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        viewModel = CalendarListViewModel(
            (application as TrainingApplication).bodyMeasureRepository,
            (application as TrainingApplication).mealFoodsRepository,
            (application as TrainingApplication).trainingRepository,
        )

        setContent {
            val months by viewModel.months.collectAsState()
            val focusedMonth by viewModel.focusedMonth.collectAsState()
            CalendarScreen(
                months = months,
                focusedMonth = focusedMonth,
                moveToPrev = viewModel::moveToPrev,
                moveToNext = viewModel::moveToNext,
                onClickBackPress = ::finish,
                onClickDate = {
                    val intent = MeasureListActivity.createIntent(this, it.value)
                    measureListLauncher.launch(intent)
                }
            )
        }
    }

    companion object {
        fun createIntent(context: Context) = Intent(context, CalendarActivity::class.java)
    }
}
