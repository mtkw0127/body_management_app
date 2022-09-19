package com.app.body_manage.ui.photoDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.app.body_manage.data.model.PhotoModel

class PhotoDetailActivity : AppCompatActivity() {
    private val photoId by lazy {
        intent.getSerializableExtra(KEY_PHOTO_ID) as PhotoModel.Id
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val photoDetailViewModel = PhotoDetailViewModel(application)
        photoDetailViewModel.loadPhoto(photoId)
        setContent {
            val state by photoDetailViewModel.uiState.collectAsState()
            PhotoDetailScreen(
                state = state,
            )
        }
    }

    companion object {
        private const val KEY_PHOTO_ID = "KEY_PHOTO_ID"

        fun createIntent(
            context: Context,
            photoId: PhotoModel.Id,
        ): Intent {
            val intent = Intent(context.applicationContext, PhotoDetailActivity::class.java)
            intent.putExtra(KEY_PHOTO_ID, photoId)
            return intent
        }
    }
}