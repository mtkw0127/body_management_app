package com.app.body_manage.ui.measure.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.app.body_manage.R
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.data.model.Photo
import com.app.body_manage.data.repository.BodyMeasurePhotoRepository
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.data.repository.LogRepository
import com.app.body_manage.data.repository.LogRepository.Companion.KEY_REVIEW_REQUEST
import com.app.body_manage.data.repository.MealRepository
import com.app.body_manage.data.repository.TrainingRepository
import com.app.body_manage.ui.mealForm.MealFormActivity
import com.app.body_manage.ui.measure.form.MeasureFormActivity
import com.app.body_manage.ui.photoDetail.PhotoDetailActivity
import com.app.body_manage.ui.trainingForm.detail.TrainingDetailActivity
import com.app.body_manage.ui.trainingForm.form.TrainingFormActivity
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class MeasureListActivity : AppCompatActivity() {

    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (application as TrainingApplication).bodyMeasureRepository
    }

    private val bodyMeasurePhotoRepository: BodyMeasurePhotoRepository by lazy {
        (application as TrainingApplication).bodyMeasurePhotoRepository
    }

    private val mealRepository: MealRepository by lazy {
        (application as TrainingApplication).mealFoodsRepository
    }

    private val trainingRepository: TrainingRepository by lazy {
        (application as TrainingApplication).trainingRepository
    }

    private val userPreferenceRepository: UserPreferenceRepository by lazy {
        UserPreferenceRepository(this)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_CODE_ADD) {
                Toast.makeText(this, getString(R.string.message_saved), Toast.LENGTH_LONG).show()
                showReviewRequest()
            }
            if (it.resultCode == RESULT_CODE_EDIT) {
                Toast.makeText(this, getString(R.string.message_edited), Toast.LENGTH_LONG).show()
            }
            if (it.resultCode == RESULT_CODE_DELETE) {
                Toast.makeText(this, getString(R.string.message_deleted), Toast.LENGTH_LONG).show()
            }
            viewModel.reload()
        }

    private fun showReviewRequest() {
        // 登録系の処理が終わったタイミングでレビューを依頼する
        // 3日以上登録している場合に依頼する
        if (
            runBlocking {
                userPreferenceRepository.getRequestedReview().not()
            } && runBlocking { bodyMeasureRepository.getEntityListAll().size >= 3 }
        ) {
            ReviewManagerFactory.create(this).requestReviewFlow()
                .addOnSuccessListener {
                    ReviewManagerFactory.create(this).launchReviewFlow(this, it)
                }
                .addOnCompleteListener {
                    LogRepository().sendLog(
                        context = this,
                        key = KEY_REVIEW_REQUEST,
                        bundle = Bundle(),
                    )
                    runBlocking { userPreferenceRepository.setRequestedReview() }
                }
        }
    }

    private lateinit var viewModel: MeasureListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        initViewModel()

        setContent {
            val state: MeasureListState.BodyMeasureListState by viewModel.uiState.collectAsState()

            MeasureListScreen(
                uiState = state,
                setLocalDate = {
                    viewModel.setDate(it)
                },
                clickBodyMeasureEdit = {
                    launcher.launch(
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
                onClickAddMeasure = {
                    launcher.launch(
                        MeasureFormActivity.createMeasureFormIntent(
                            context = this,
                            measureDate = viewModel.uiState.value.date
                        )
                    )
                },
                onClickAddMeal = {
                    launcher.launch(
                        MealFormActivity.createIntentAdd(
                            context = this,
                            localDate = viewModel.uiState.value.date,
                        )
                    )
                },
                showPhotoDetail = {
                    val intent = PhotoDetailActivity.createIntent(
                        baseContext,
                        Photo.Id(it)
                    )
                    launcher.launch(intent)
                },
                onChangeCurrentMonth = {
                    viewModel.setCurrentYearMonth(it)
                },
                onClickBack = { finish() },
                onClickMeal = {
                    launcher.launch(MealFormActivity.createIntentEdit(this, it.id))
                },
                onClickAddTraining = {
                    launcher.launch(
                        TrainingFormActivity.createInstance(
                            this,
                            viewModel.uiState.value.date
                        )
                    )
                },
                onClickTraining = {
                    launcher.launch(TrainingDetailActivity.createInstance(this, it))
                }
            )
        }
    }

    private fun initViewModel() {
        viewModel = MeasureListViewModel(
            localDate = intent.getSerializableExtra(INTENT_KEY) as LocalDate,
            bodyMeasureRepository = bodyMeasureRepository,
            bodyMeasurePhotoRepository = bodyMeasurePhotoRepository,
            userPreferenceRepository = UserPreferenceRepository(this),
            mealRepository = mealRepository,
            trainingRepository = trainingRepository,
        )
        viewModel.reload()
    }

    companion object {
        private const val INTENT_KEY = "DATE"

        const val RESULT_CODE_ADD = Activity.RESULT_FIRST_USER + 100
        const val RESULT_CODE_EDIT = Activity.RESULT_FIRST_USER + 101
        const val RESULT_CODE_DELETE = Activity.RESULT_FIRST_USER + 102

        fun createIntent(context: Context, localDate: LocalDate): Intent {
            val intent = Intent(context, MeasureListActivity::class.java)
            intent.putExtra(INTENT_KEY, localDate)
            return intent
        }
    }
}
