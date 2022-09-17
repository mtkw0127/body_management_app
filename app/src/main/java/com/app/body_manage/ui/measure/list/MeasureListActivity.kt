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
import com.app.body_manage.data.repository.BodyMeasurePhotoRepository
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.ui.calendar.CalendarActivity
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.measure.form.BodyMeasureEditFormActivity
import com.app.body_manage.ui.measure.form.BodyMeasureEditFormViewModel
import com.app.body_manage.ui.photoDetail.PhotoDetailActivity
import com.app.body_manage.ui.photoList.PhotoListActivity
import com.app.body_manage.util.DateUtil
import java.time.LocalDate

class MeasureListActivity : AppCompatActivity() {

    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (application as TrainingApplication).bodyMeasureRepository
    }

    private val bodyMeasurePhotoRepository: BodyMeasurePhotoRepository by lazy {
        (application as TrainingApplication).bodyMeasurePhotoRepository
    }

    private val localDate: LocalDate by lazy { intent.getSerializableExtra(INTENT_KEY) as LocalDate }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    private val measureFormLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val message = when (it.resultCode) {
                BodyMeasureEditFormActivity.RESULT_DELETE -> "削除しました"
                BodyMeasureEditFormActivity.RESULT_UPDATE -> "更新しました"
                BodyMeasureEditFormActivity.RESULT_CREATE -> "追加しました"
                else -> ""
            }
            viewModel.updateMessage(message)
            viewModel.reload()
        }

    private lateinit var viewModel: MeasureListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()

        supportActionBar?.title = DateUtil.localDateConvertJapaneseFormatYearMonthDay(localDate)

        setContent {
            val state: MeasureListState by viewModel.uiState.collectAsState()

            val bottomSheetDataList = createBottomDataList(
                calendarAction = { launcher.launch(CalendarActivity.createIntent(this)) },
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
                    applicationContext
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
                resetSnackBarMessage = {
                    viewModel.resetMessage()
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
                showPhotoDetail = {
                    val intent = PhotoDetailActivity.createIntent(
                        baseContext,
                        BodyMeasureEditFormViewModel.PhotoModel.Id(it)
                    )
                    launcher.launch(intent)
                }
            )
        }
    }

    private fun initViewModel() {
        viewModel = MeasureListViewModel(
            localDate = localDate,
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