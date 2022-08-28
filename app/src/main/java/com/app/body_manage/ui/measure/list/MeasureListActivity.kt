package com.app.body_manage.ui.measure.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.app.body_manage.R
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.ui.calendar.CalendarActivity
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.measure.form.BodyMeasureEditFormActivity
import com.app.body_manage.ui.photoList.PhotoListActivity
import java.time.LocalDate

class MeasureListActivity : AppCompatActivity() {

    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (application as TrainingApplication).bodyMeasureRepository
    }

    private val localDate: LocalDate by lazy { intent.getSerializableExtra(INTENT_KEY) as LocalDate }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    private val measureFormLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.reload()
        }

    private lateinit var viewModel: MeasureListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        setContent {
            val state: MeasureListState by viewModel.uiState.collectAsState()

            val bottomSheetDataList = listOf(
                PhotoListActivity.BottomSheetData(
                    "カレンダー", R.drawable.ic_baseline_calendar_month_24
                ) {
                    launcher.launch(CalendarActivity.createIntent(this))
                },
                PhotoListActivity.BottomSheetData("写真", R.drawable.ic_baseline_photo_library_24) {
                    launcher.launch(PhotoListActivity.createIntent(this))
                },
                PhotoListActivity.BottomSheetData("グラフ", R.drawable.ic_baseline_show_chart_24) {
                    launcher.launch(GraphActivity.createIntent(this))
                }
            )

            MeasureListScreen(
                uiState = state,
                bottomSheetDataList = bottomSheetDataList,
                clickSaveBodyInfo = {
                    viewModel.updateTall()
                },
                setTall = {
                    viewModel.setTall(it)
                },
                clickBodyMeasureEdit = {
                    measureFormLauncher.launch(
                        BodyMeasureEditFormActivity.createMeasureEditIntent(
                            context = this,
                            captureTime = it,
                        )
                    )
                },
                clickFab = {
                    when (viewModel.uiState.value.measureType) {
                        MeasureType.BODY -> {
                            measureFormLauncher.launch(
                                BodyMeasureEditFormActivity.createMeasureFormIntent(
                                    context = this,
                                    formDate = viewModel.uiState.value.date,
                                )
                            )
                        }
                        MeasureType.MEAL -> {
                            Toast.makeText(this, "今後機能追加する！", Toast.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                },
            )
        }
    }

    private fun initViewModel() {
        viewModel = MeasureListViewModel(
            localDate = localDate,
            mealType = MeasureType.BODY,
            bodyMeasureRepository,
        )
        viewModel.reload()
    }

    companion object {
        private const val INTENT_KEY = "DATE"
        fun createTrainingMeasureListIntent(context: Context, localDate: LocalDate): Intent {
            val intent = Intent(context.applicationContext, MeasureListActivity::class.java)
            intent.putExtra(INTENT_KEY, localDate)
            return intent
        }
    }
}