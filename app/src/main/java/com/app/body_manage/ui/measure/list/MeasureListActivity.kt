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
import com.app.body_manage.TrainingApplication
import com.app.body_manage.common.createBottomDataList
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.data.model.PhotoModel
import com.app.body_manage.data.repository.BodyMeasurePhotoRepository
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.ui.calendar.CalendarActivity
import com.app.body_manage.ui.compare.CompareActivity
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.measure.form.MeasureFormActivity
import com.app.body_manage.ui.photoDetail.PhotoDetailActivity
import com.app.body_manage.ui.photoList.PhotoListActivity
import java.time.LocalDate

class MeasureListActivity : AppCompatActivity() {

    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (application as TrainingApplication).bodyMeasureRepository
    }

    private val bodyMeasurePhotoRepository: BodyMeasurePhotoRepository by lazy {
        (application as TrainingApplication).bodyMeasurePhotoRepository
    }

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

            val bottomSheetDataList = createBottomDataList(
                calendarAction = { launcher.launch(CalendarActivity.createIntent(this)) },
                compareAction = { launcher.launch(CompareActivity.createIntent(this)) },
                photoListAction = { launcher.launch(PhotoListActivity.createIntent(this)) },
                graphAction = { launcher.launch(GraphActivity.createIntent(this)) }
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
                setLocalDate = {
                    viewModel.setDate(it)
                },
                clickBodyMeasureEdit = {
                    measureFormLauncher.launch(
                        MeasureFormActivity.createMeasureEditIntent(
                            context = this,
                            measureTime = it,
                        )
                    )
                },
                resetSnackBarMessage = {
                    viewModel.resetMessage()
                },
                updateDate = {
                    viewModel.updateDate(it)
                },
                clickFab = {
                    when (viewModel.uiState.value.measureType) {
                        MeasureType.BODY -> {
                            measureFormLauncher.launch(
                                MeasureFormActivity.createMeasureFormIntent(
                                    context = this,
                                    measureDate = viewModel.uiState.value.date
                                )
                            )
                        }

                        MeasureType.MEAL -> {
                            Toast.makeText(this, "今後機能追加する！", Toast.LENGTH_LONG).show()
                        }

                        else -> {}
                    }
                },
                showPhotoDetail = {
                    val intent = PhotoDetailActivity.createIntent(
                        baseContext,
                        PhotoModel.Id(it)
                    )
                    launcher.launch(intent)
                },
                onChangeCurrentMonth = {
                    viewModel.setCurrentYearMonth(it)
                }
            )
        }
    }

    private fun initViewModel() {
        viewModel = MeasureListViewModel(
            localDate = intent.getSerializableExtra(INTENT_KEY) as LocalDate,
            mealType = MeasureType.BODY,
            bodyMeasureRepository = bodyMeasureRepository,
            bodyMeasurePhotoRepository = bodyMeasurePhotoRepository,
            userPreferenceRepository = UserPreferenceRepository(this),
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
