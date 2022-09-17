package com.app.body_manage.ui.photoList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.app.body_manage.common.createBottomDataList
import com.app.body_manage.ui.calendar.CalendarActivity
import com.app.body_manage.ui.compare.CompareActivity
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.measure.form.BodyMeasureEditFormViewModel
import com.app.body_manage.ui.photoDetail.PhotoDetailActivity

class PhotoListActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context) = Intent(context, PhotoListActivity::class.java)
    }

    private val launcher =
        registerForActivityResult(StartActivityForResult()) {}

    private val photoDetailLauncher =
        registerForActivityResult(StartActivityForResult()) {}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = PhotoListViewModel(application = application)
        vm.loadPhotoRegisteredDates()
        setContent {
            MaterialTheme {
                val state: PhotoListState by vm.uiState.collectAsState()
                val bottomSheetDataList = createBottomDataList(
                    calendarAction = { launcher.launch(CalendarActivity.createIntent(this)) },
                    compareAction = { launcher.launch(CompareActivity.createIntent(this)) },
                    graphAction = { launcher.launch(GraphActivity.createIntent(this)) },
                    photoListAction = {}
                )
                PhotoListScreen(
                    state = state,
                    photoDetailAction = { photoId ->
                        photoDetailLauncher.launch(
                            PhotoDetailActivity.createIntent(
                                context = this,
                                photoId = BodyMeasureEditFormViewModel.PhotoModel.Id(photoId)
                            )
                        )
                    },
                    bottomSheetDataList = bottomSheetDataList
                )
            }
        }
    }
}