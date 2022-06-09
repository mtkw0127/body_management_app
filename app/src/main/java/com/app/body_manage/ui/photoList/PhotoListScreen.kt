package com.app.body_manage.ui.photoList

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import coil.compose.AsyncImage
import com.app.body_manage.ui.photoList.PhotoListState.HasPhoto
import com.app.body_manage.ui.photoList.PhotoListState.NoPhoto

@Composable
fun PhotoListScreen(
    state: PhotoListState
) {
    when (state) {
        is HasPhoto -> {
            LazyColumn {
                state.photos.forEach { (date, photos) ->
                    item {
                        Text(text = date.calendarDate.toString())
                    }
                    items(photos.size) {
                        AsyncImage(
                            model = photos[it].photoUri,
                            contentDescription = null
                        )
                    }
                }
            }
        }
        is NoPhoto -> {
            Text(text = "画像は未登録です。")
        }
    }
}