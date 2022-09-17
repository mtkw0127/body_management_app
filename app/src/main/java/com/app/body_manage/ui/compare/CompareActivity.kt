package com.app.body_manage.ui.compare

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.app.body_manage.common.createBottomDataList
import com.app.body_manage.ui.calendar.CalendarActivity
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.measure.list.MeasureListActivity
import com.app.body_manage.ui.photoList.PhotoListActivity
import java.time.LocalDate

class CompareActivity : AppCompatActivity() {

    private val simpleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    private val beforeSearchLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        }

    private val afterSearchLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            
        }

    private val viewModel = CompareViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bottomSheetDataList = createBottomDataList(
            calendarAction = { simpleLauncher.launch(CalendarActivity.createIntent(this)) },
            compareAction = { simpleLauncher.launch(createIntent(this)) },
            photoListAction = { simpleLauncher.launch(PhotoListActivity.createIntent(this)) },
            graphAction = { simpleLauncher.launch(GraphActivity.createIntent(this)) }
        )
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            CompareScreen(
                uiState = uiState,
                beforeSearchLauncher = {
                    beforeSearchLauncher.launch(
                        MeasureListActivity.createTrainingMeasureListIntent(
                            context = baseContext,
                            localDate = LocalDate.now(),// TODO
                        )
                    )
                },
                afterSearchLauncher = {
                    afterSearchLauncher.launch(
                        MeasureListActivity.createTrainingMeasureListIntent(
                            context = baseContext,
                            localDate = LocalDate.now(),// TODO
                        )
                    )
                },
                bottomSheetDataList = bottomSheetDataList
            )
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, CompareActivity::class.java)
        }
    }
}