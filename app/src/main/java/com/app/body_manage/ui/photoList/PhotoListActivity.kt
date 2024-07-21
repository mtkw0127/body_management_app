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
import com.app.body_manage.data.model.Photo
import com.app.body_manage.ui.photoDetail.PhotoDetailActivity

class PhotoListActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context) = Intent(context, PhotoListActivity::class.java)
    }

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
                    onClickBack = ::finish
                )
            }
        }
    }
}
