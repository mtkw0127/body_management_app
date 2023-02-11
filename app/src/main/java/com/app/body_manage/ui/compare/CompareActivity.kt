package com.app.body_manage.ui.compare

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.app.body_manage.TrainingApplication
import com.app.body_manage.common.createBottomDataList
import com.app.body_manage.data.dao.ComparePhotoHistoryDao
import com.app.body_manage.data.model.PhotoModel
import com.app.body_manage.data.repository.BodyMeasurePhotoRepository
import com.app.body_manage.data.repository.CompareHistoryRepository
import com.app.body_manage.ui.calendar.CalendarActivity
import com.app.body_manage.ui.choosePhoto.ChoosePhotoActivity
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.photoDetail.PhotoDetailActivity
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
        lifecycleScope.launch {
            viewModel.notSetCompareItem.collect {
                if (it) {
                    Toast.makeText(this@CompareActivity, "比較画像を選択してからクリックしてください", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            var showDeleteConfirmDialog by remember { mutableStateOf(false) }
            var deleteTarget by remember {
                mutableStateOf<ComparePhotoHistoryDao.PhotoAndBodyMeasure?>(
                    null
                )
            }
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
                bottomSheetDataList = bottomSheetDataList,
                onClickDelete = {
                    showDeleteConfirmDialog = true
                    deleteTarget = it
                },
                onClickPhoto = {
                    startActivity(
                        PhotoDetailActivity.createIntent(
                            this@CompareActivity,
                            PhotoModel.Id(it)
                        )
                    )
                }
            )
            if (showDeleteConfirmDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDeleteConfirmDialog = false
                    },
                    title = {
                        Text(
                            text = "履歴を削除しますがよろしいですか？",
                            lineHeight = 24.sp,
                            fontSize = 16.sp
                        )
                    },
                    buttons = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth()
                        ) {
                            Text("キャンセル", modifier = Modifier
                                .padding(5.dp, end = 10.dp)
                                .clickable {
                                    showDeleteConfirmDialog = false
                                })
                            Text("削除", modifier = Modifier
                                .padding(5.dp)
                                .clickable {
                                    viewModel.deleteHistory(deleteTarget ?: return@clickable)
                                    showDeleteConfirmDialog = false
                                })
                        }
                    }
                )
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, CompareActivity::class.java)
        }
    }
}