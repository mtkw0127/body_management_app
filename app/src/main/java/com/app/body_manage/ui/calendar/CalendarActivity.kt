package com.app.body_manage.ui.calendar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.app.body_manage.TrainingApplication
import com.app.body_manage.common.createBottomDataList
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.measure.list.MeasureListActivity
import com.app.body_manage.ui.top.TopActivity

class CalendarActivity : AppCompatActivity() {

    private val measureListLauncher = registerForActivityResult(StartActivityForResult()) {
        viewModel.updateCurrentMonth()
    }

    private val launcher =
        registerForActivityResult(StartActivityForResult()) {
        }

    lateinit var viewModel: CalendarListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        viewModel = CalendarListViewModel(
            (application as TrainingApplication).bodyMeasureRepository,
            (application as TrainingApplication).mealFoodsRepository,
            (application as TrainingApplication).trainingRepository,
        )

        val bottomSheetDataList = createBottomDataList(
            context = this,
            topAction = { launcher.launch(TopActivity.createIntent(this)) },
            openCalendar = { },
            graphAction = { launcher.launch(GraphActivity.createIntent(this)) },
            isCalendar = true,
        )

        setContent {
            val months by viewModel.months.collectAsState()
            val focusedMonth by viewModel.focusedMonth.collectAsState()
            CalendarScreen(
                months = months,
                focusedMonth = focusedMonth,
                bottomSheetDataList = bottomSheetDataList,
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
