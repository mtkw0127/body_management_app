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
import com.app.body_manage.R
import com.app.body_manage.ui.calendar.MainActivity
import com.app.body_manage.ui.graph.GraphActivity

class PhotoListActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context) = Intent(context, PhotoListActivity::class.java)
    }

    private val launcher =
        registerForActivityResult(StartActivityForResult()) {}

    data class BottomSheetData(val name: String, val resourceId: Int, val action: () -> Unit)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = PhotoListViewModel(application = application)
        vm.loadPhotoRegisteredDates()
        setContent {
            MaterialTheme {
                val state: PhotoListState by vm.uiState.collectAsState()
                val bottomSheetDataList = listOf(
                    BottomSheetData("カレンダー", R.drawable.ic_baseline_calendar_month_24) {
                        launcher.launch(
                            MainActivity.createIntent(this)
                        )
                    },
                    BottomSheetData("写真", R.drawable.ic_baseline_photo_library_24) {},
                    BottomSheetData("グラフ", R.drawable.ic_baseline_show_chart_24) {
                        launcher.launch(
                            GraphActivity.createIntent(this)
                        )
                    }
                )
                PhotoListScreen(
                    state = state,
                    bottomSheetDataList
                )
            }
        }
    }
}