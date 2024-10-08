package com.app.body_manage.ui.top

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.app.body_manage.R
import com.app.body_manage.TrainingApplication
import com.app.body_manage.common.createBottomDataList
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.data.repository.LogRepository
import com.app.body_manage.data.repository.LogRepository.Companion.KEY_OPEN_OBJECT_KCAL
import com.app.body_manage.data.repository.LogRepository.Companion.KEY_OPEN_OBJECT_WEIGHT
import com.app.body_manage.data.repository.LogRepository.Companion.KEY_REVIEW_REQUEST
import com.app.body_manage.data.repository.LogRepository.Companion.KEY_USER_SETTINGS
import com.app.body_manage.data.repository.MealRepository
import com.app.body_manage.dialog.Digit
import com.app.body_manage.dialog.FloatNumberPickerDialog
import com.app.body_manage.dialog.IntNumberPickerDialog
import com.app.body_manage.ui.calendar.CalendarActivity
import com.app.body_manage.ui.compare.CompareActivity
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.mealForm.MealFormActivity
import com.app.body_manage.ui.mealList.MealListActivity
import com.app.body_manage.ui.measure.form.MeasureFormActivity
import com.app.body_manage.ui.measure.list.MeasureListActivity
import com.app.body_manage.ui.measure.list.MeasureListActivity.Companion.RESULT_CODE_ADD
import com.app.body_manage.ui.photoList.PhotoListActivity
import com.app.body_manage.ui.top.UserPreferenceSettingDialog.Companion.REQUEST_KEY
import com.app.body_manage.ui.trainingForm.form.TrainingFormActivity
import com.app.body_manage.ui.trainingMenu.TrainingMenuListActivity
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class TopActivity : AppCompatActivity() {
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.load()
            if (it.resultCode == RESULT_CODE_ADD) {
                Toast.makeText(this, getString(R.string.message_saved), Toast.LENGTH_LONG).show()
                startActivity(
                    MeasureListActivity.createIntent(
                        this,
                        LocalDate.now()
                    )
                )
                showReviewRequest()
            }
        }

    private fun showReviewRequest() {
        // 登録系の処理が終わったタイミングでレビューを依頼する
        // 5回毎に表示
        val bodyMeasureNum = runBlocking { bodyMeasureRepository.getBodyMeasureList() }
        val mealNum = runBlocking { mealRepository.getMealCount() }
        val trainingNum = runBlocking { trainingRepository.getTrainingCount() }
        val total = bodyMeasureNum + mealNum + trainingNum
        if (
            runBlocking { total % 10 == 0 || total == 1 }
        ) {
            LogRepository().sendLog(
                context = this,
                key = KEY_REVIEW_REQUEST,
                bundle = Bundle(),
            )
            ReviewManagerFactory.create(this).requestReviewFlow()
                .addOnSuccessListener { reviewInfo ->
                    ReviewManagerFactory.create(this).launchReviewFlow(this, reviewInfo)
                        .addOnSuccessListener {
                        }.addOnFailureListener {
                        }
                }
        }
    }

    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (application as TrainingApplication).bodyMeasureRepository
    }

    private val mealRepository: MealRepository by lazy {
        (application as TrainingApplication).mealFoodsRepository
    }

    private val trainingRepository by lazy {
        (application as TrainingApplication).trainingRepository
    }

    private lateinit var viewModel: TopViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        onBackPressedDispatcher.addCallback {}
        supportFragmentManager.setFragmentResultListener(REQUEST_KEY, this) { _, _ ->
            LogRepository().sendLog(
                this,
                KEY_USER_SETTINGS,
                Bundle().apply {
                    putString("name", viewModel.userPreference.value?.name)
                    putBoolean("meal", viewModel.userPreference.value?.optionFeature?.meal == true)
                    putBoolean(
                        "training",
                        viewModel.userPreference.value?.optionFeature?.training == true
                    )
                }
            )
            viewModel.load()
        }
        val bottomSheetDataList = createBottomDataList(
            context = this,
            topAction = { },
            openCalendar = { launcher.launch(CalendarActivity.createIntent(this)) },
            graphAction = { launcher.launch(GraphActivity.createIntent(this)) },
            isTop = true,
        )
        viewModel = TopViewModel(
            userPreferenceRepository = UserPreferenceRepository(this),
            bodyMeasureRepository = bodyMeasureRepository,
            mealRepository = mealRepository,
            trainingRepository = trainingRepository
        )
        viewModel.checkSetUpUserPref()
        lifecycleScope.launch {
            viewModel.showUserPrefDialog.collectLatest { show ->
                if (show) {
                    UserPreferenceSettingDialog
                        .createInstance()
                        .show(supportFragmentManager, null)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.openMeasureForm.collectLatest {
                Toast.makeText(
                    this@TopActivity,
                    getString(R.string.label_first_save_your_body),
                    Toast.LENGTH_LONG
                ).show()
                launcher.launch(
                    MeasureFormActivity.createMeasureFormIntent(
                        this@TopActivity,
                        LocalDate.now()
                    )
                )
            }
        }
        // 開始体重設定後に目標体重を設定
        supportFragmentManager.setFragmentResultListener("START_WEIGHT", this) { key, bundle ->
            val weight =
                viewModel.userPreference.value?.goalWeight
                    ?: viewModel.lastMeasure.value?.weight
                    ?: return@setFragmentResultListener
            FloatNumberPickerDialog.createDialog(
                label = getString(R.string.label_target_weight),
                number = weight,
                unit = getString(R.string.unit_kg),
                requestKey = "GOAL",
                supportOneHundred = true,
                buttonTextResource = R.string.settings,
            ) {
                viewModel.setGoalWeight(it)
                LogRepository().sendLog(
                    this,
                    KEY_OPEN_OBJECT_WEIGHT,
                    Bundle().apply {
                        putFloat("weight", it)
                    }
                )
            }.show(supportFragmentManager, null)
        }

        // 目標体重設定後に１日の目標カロリー設定
        supportFragmentManager.setFragmentResultListener("GOAL", this) { key, bundle ->
            if (viewModel.userPreference.value?.optionFeature?.meal == true) {
                IntNumberPickerDialog.createDialog(
                    label = getString(R.string.kcal_per_day),
                    number = viewModel.userPreference.value?.goalKcal ?: 2000,
                    unit = getString(R.string.unit_kcal),
                    maxDigit = Digit.THOUSAND,
                    initialDigit = Digit.THOUSAND,
                ) {
                    viewModel.setGoalKcal(it)
                    LogRepository().sendLog(
                        this,
                        KEY_OPEN_OBJECT_KCAL,
                        Bundle().apply {
                            putInt("kcal", it.toInt())
                        }
                    )
                }.show(supportFragmentManager, null)
            }
        }

        setContent {
            val userPreference by viewModel.userPreference.collectAsState()
            val lastMeasure by viewModel.lastMeasure.collectAsState()
            val initialMeasure by viewModel.initialMeasure.collectAsState()
            val todayMeasure by viewModel.todayMeasure.collectAsState()
            val enableUpdate by viewModel.enableUpdate.collectAsState(false)

            TopScreen(
                userPreference = userPreference,
                lastMeasure = lastMeasure,
                initialMeasure = initialMeasure,
                todayMeasure = todayMeasure,
                enableUpdate = enableUpdate,
                bottomSheetDataList = bottomSheetDataList,
                onClickSeeTrainingMenu = {
                    launcher.launch(TrainingMenuListActivity.createIntent(this))
                },
                onClickMeal = {
                    launcher.launch(MealListActivity.createIntent(this))
                },
                onClickCompare = {
                    launcher.launch(CompareActivity.createIntent(this))
                },
                onClickPhotos = {
                    launcher.launch(PhotoListActivity.createIntent(this))
                },
                onClickToday = {
                    launcher.launch(MeasureListActivity.createIntent(this, LocalDate.now()))
                },
                onClickAddMeasure = {
                    launcher.launch(
                        MeasureFormActivity.createMeasureFormIntent(
                            this,
                            LocalDate.now()
                        )
                    )
                },
                onClickSetGoal = {
                    val weight = viewModel.userPreference.value?.startWeight
                        ?: viewModel.lastMeasure.value?.weight ?: return@TopScreen
                    FloatNumberPickerDialog.createDialog(
                        label = getString(R.string.label_start_weight),
                        number = weight,
                        unit = getString(R.string.unit_kg),
                        requestKey = "START_WEIGHT",
                        buttonTextResource = R.string.settings,
                        supportOneHundred = true,
                    ) {
                        viewModel.setStartWeight(it)
                        LogRepository().sendLog(
                            this,
                            KEY_OPEN_OBJECT_WEIGHT,
                            Bundle().apply {
                                putFloat("start_weight", it)
                            }
                        )
                    }.show(supportFragmentManager, null)
                },
                onClickSetting = {
                    UserPreferenceSettingDialog
                        .createInstance()
                        .show(supportFragmentManager, null)
                },
                onClickAddTraining = {
                    launcher.launch(
                        TrainingFormActivity.createInstance(
                            this,
                            LocalDate.now()
                        )
                    )
                },
                onClickAddMeal = {
                    launcher.launch(
                        MealFormActivity.createIntentAdd(
                            this,
                            LocalDate.now()
                        )
                    )
                },
                onClickStore = {
                    openPlayStore()
                }
            )
        }
    }

    private fun openPlayStore() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName")
            )
        )
    }

    companion object {
        fun createIntent(
            context: Context
        ): Intent {
            return Intent(context, TopActivity::class.java)
        }
    }
}
