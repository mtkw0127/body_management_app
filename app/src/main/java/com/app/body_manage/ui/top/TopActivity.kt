package com.app.body_manage.ui.top

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.app.body_manage.common.createBottomDataList
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.ui.calendar.CalendarActivity
import com.app.body_manage.ui.compare.CompareActivity
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.measure.form.MeasureFormActivity
import com.app.body_manage.ui.photoList.PhotoListActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class TopActivity : AppCompatActivity() {
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.load()
        }

    private lateinit var viewModel: TopViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback {}
        supportFragmentManager.addOnBackStackChangedListener {
            viewModel.load()
        }
        val bottomSheetDataList = createBottomDataList(
            topAction = { },
            compareAction = { launcher.launch(CompareActivity.createIntent(this)) },
            graphAction = { launcher.launch(GraphActivity.createIntent(this)) },
            photoListAction = { launcher.launch(PhotoListActivity.createIntent(this)) },
            isTop = true,
        )
        viewModel = TopViewModel(UserPreferenceRepository(this))
        viewModel.checkSetUpUserPref()
        viewModel.load()
        lifecycleScope.launch {
            viewModel.showUserPrefDialog.collectLatest { show ->
                if (show) {
                    UserPreferenceSettingDialog
                        .createInstance()
                        .show(supportFragmentManager, null)
                }
            }
        }
        setContent {
            val userPreference by viewModel.userPreference.collectAsState()
            val healthyDuration by viewModel.healthyDuration.collectAsState()
            TopScreen(
                userPreference = userPreference,
                healthyDuration = healthyDuration,
                bottomSheetDataList = bottomSheetDataList,
                onClickCalendar = {
                    launcher.launch(CalendarActivity.createIntent(this))
                },
                onClickAdd = {
                    launcher.launch(
                        MeasureFormActivity.createMeasureFormIntent(
                            this,
                            LocalDate.now()
                        )
                    )
                }
            )
        }
    }

    companion object {
        fun createIntent(
            context: Context
        ): Intent {
            return Intent(context, TopActivity::class.java)
        }
    }
}