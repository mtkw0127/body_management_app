package com.app.body_manage.ui.photoDetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.app.body_manage.data.model.PhotoModel

class PhotoDetailActivity : AppCompatActivity() {
    private val photoId by lazy {
        intent.getSerializableExtra(KEY_PHOTO_ID) as? PhotoModel.Id
    }

    private val photoUri by lazy {
        intent.getParcelableExtra(KEY_PHOTO_URI) as? Uri
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val photoDetailViewModel = PhotoDetailViewModel(application)
        photoId?.let { photoId ->
            photoDetailViewModel.loadPhoto(photoId)
        }
        photoUri?.let { uri ->
            photoDetailViewModel.setUri(uri)
        }
        setContent {
            val state by photoDetailViewModel.uiState.collectAsState()
            PhotoDetailScreen(
                state = state,
                onClickBackPress = { finish() }
            )
        }
    }

    companion object {
        private const val KEY_PHOTO_ID = "KEY_PHOTO_ID"
        private const val KEY_PHOTO_URI = "KEY_PHOTO_URI"

        fun createIntent(
            context: Context,
            photoId: PhotoModel.Id,
        ): Intent {
            val intent = Intent(context, PhotoDetailActivity::class.java)
            intent.putExtra(KEY_PHOTO_ID, photoId)
            return intent
        }

        fun createIntent(
            context: Context,
            uri: Uri
        ): Intent {
            val intent = Intent(context, PhotoDetailActivity::class.java)
            intent.putExtra(KEY_PHOTO_URI, uri)
            return intent
        }
    }
}
