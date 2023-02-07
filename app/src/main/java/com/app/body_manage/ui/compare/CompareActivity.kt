package com.app.body_manage.ui.compare

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.app.body_manage.TrainingApplication
import com.app.body_manage.common.createBottomDataList
import com.app.body_manage.data.repository.BodyMeasurePhotoRepository
import com.app.body_manage.data.repository.CompareHistoryRepository
import com.app.body_manage.ui.calendar.CalendarActivity
import com.app.body_manage.ui.choosePhoto.ChoosePhotoActivity
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.photoList.PhotoListActivity
import kotlinx.coroutines.launch

class CompareActivity : AppCompatActivity() {

    private val bodyMeasurePhotoRepository: BodyMeasurePhotoRepository by lazy {
        (application as TrainingApplication).bodyMeasurePhotoRepository
    }

    private val compareBodyMeasureHistoryRepository: CompareHistoryRepository by lazy {
        (application as TrainingApplication).compareBodyMeasureHistoryRepository
    }

    private val simpleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    private val beforeSearchLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val photoId =
                    requireNotNull(it.data).getIntExtra(
                        ChoosePhotoActivity.RESULT_SELECT_PHOTO_ID,
                        0
                    )
                viewModel.loadBodyMeasure(photoId = photoId, CompareItemType.BEFORE)
            }
        }

    private val afterSearchLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val photoId =
                    requireNotNull(it.data).getIntExtra(
                        ChoosePhotoActivity.RESULT_SELECT_PHOTO_ID,
                        0
                    )
                viewModel.loadBodyMeasure(photoId = photoId, CompareItemType.AFTER)
            }
        }

    private lateinit var viewModel: CompareViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bottomSheetDataList = createBottomDataList(
            calendarAction = { simpleLauncher.launch(CalendarActivity.createIntent(this)) },
            compareAction = { simpleLauncher.launch(createIntent(this)) },
            photoListAction = { simpleLauncher.launch(PhotoListActivity.createIntent(this)) },
            graphAction = { simpleLauncher.launch(GraphActivity.createIntent(this)) }
        )

        viewModel =
            CompareViewModel(bodyMeasurePhotoRepository, compareBodyMeasureHistoryRepository)

        lifecycleScope.launch {
            viewModel.uiState.collect {
                if (it?.saveFail == true) {
                    Toast.makeText(this@CompareActivity, "保存に失敗しました", Toast.LENGTH_LONG).show()
                }
                if (it?.saveSuccess == true) {
                    Toast.makeText(this@CompareActivity, "保存に成功しました", Toast.LENGTH_LONG).show()
                }
            }
        }

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            CompareScreen(
                uiState = uiState ?: return@setContent,
                beforeSearchLauncher = {
                    beforeSearchLauncher.launch(
                        ChoosePhotoActivity.createIntent(this)
                    )
                },
                afterSearchLauncher = {
                    afterSearchLauncher.launch(
                        ChoosePhotoActivity.createIntent(this)
                    )
                },
                saveHistory = {
                    viewModel.saveHistory()
                },
                loadHistory = {
                    viewModel.loadHistory()
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