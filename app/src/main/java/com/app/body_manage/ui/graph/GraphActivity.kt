package com.app.body_manage.ui.graph

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
import com.app.body_manage.common.createBottomDataList
import com.app.body_manage.ui.calendar.CalendarActivity
import com.app.body_manage.ui.top.TopActivity

class GraphActivity : AppCompatActivity() {

    private val launcher =
        registerForActivityResult(StartActivityForResult()) {}

    private lateinit var viewModel: GraphViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        viewModel = GraphViewModel(application = application)
        viewModel.loadBodyMeasure()

        setContent {
            val state: GraphState by viewModel.uiState.collectAsState()
            val bottomSheetDataList = createBottomDataList(
                context = this,
                topAction = { launcher.launch(TopActivity.createIntent(this)) },
                openCalendar = { launcher.launch(CalendarActivity.createIntent(this)) },
                graphAction = { },
                isGraph = true
            )
            GraphScreen(
                state,
                bottomSheetDataList,
                onClickDataType = viewModel::setDataType,
                onClickDuration = viewModel::setDuration,
            )
        }
    }

    companion object {
        fun createIntent(context: Context) =
            Intent(context, GraphActivity::class.java)
    }
}
