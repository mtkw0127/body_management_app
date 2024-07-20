package com.app.body_manage.ui.photoList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.app.body_manage.common.createBottomDataList
import com.app.body_manage.data.model.Photo
import com.app.body_manage.ui.compare.CompareActivity
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.photoDetail.PhotoDetailActivity
import com.app.body_manage.ui.top.TopActivity

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
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        val vm = PhotoListViewModel(application = application)
        vm.load()
        setContent {
            MaterialTheme {
                val state: PhotoListState by vm.uiState.collectAsState()
                val bottomSheetDataList = createBottomDataList(
                    context = this,
                    topAction = { launcher.launch(TopActivity.createIntent(this)) },
                    compareAction = { launcher.launch(CompareActivity.createIntent(this)) },
                    graphAction = { launcher.launch(GraphActivity.createIntent(this)) },
                    photoListAction = {},
                    isPhotos = true,
                )
                PhotoListScreen(
                    state = state,
                    photoDetailAction = { photoId ->
                        photoDetailLauncher.launch(
                            PhotoDetailActivity.createIntent(
                                context = this,
                                photoId = Photo.Id(photoId)
                            )
                        )
                    },
                    onClickSortType = {
                        vm.changeType(it)
                    },
                    bottomSheetDataList = bottomSheetDataList
                )
            }
        }
    }
}
