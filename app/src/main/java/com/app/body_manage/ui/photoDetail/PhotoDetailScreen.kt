package com.app.body_manage.ui.photoDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage

@Composable
fun PhotoDetailScreen(
    state: PhotoDetailState
) = when (state) {
    is PhotoDetailState.ShowPhotoDetail -> {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(model = state.photoModel.uri, contentDescription = "写真詳細")
        }
    }
    is PhotoDetailState.LoadingPhotoDetail -> {
        CircularProgressIndicator()
    }
    is PhotoDetailState.NotFoundPhoto -> {
        Text(text = "写真が見つかりません。")
    }
}