package com.app.body_manage.ui.photoList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.body_manage.ui.photoList.PhotoListState.HasPhoto
import com.app.body_manage.ui.photoList.PhotoListState.NoPhoto

@Composable
fun PhotoListScreen(
    state: PhotoListState
) {
    when (state) {
        is HasPhoto -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.photos.forEach { (date, photos) ->
                    item(span = { GridItemSpan(3) }) {
                        Text(
                            text = date,
                            fontSize = 16.sp,
                        )
                    }
                    items(photos.size) {
                        AsyncImage(
                            model = photos[it],
                            contentDescription = null,
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