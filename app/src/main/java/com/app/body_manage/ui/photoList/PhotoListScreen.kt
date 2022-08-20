package com.app.body_manage.ui.photoList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.body_manage.ui.photoList.PhotoListActivity.BottomSheetData
import com.app.body_manage.ui.photoList.PhotoListState.HasPhoto
import com.app.body_manage.ui.photoList.PhotoListState.NoPhoto

@Composable
fun PhotoListScreen(
    state: PhotoListState,
    bottomSheetDataList: List<BottomSheetData>,
) {
    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color(red = 232, green = 222, blue = 248),
                modifier = Modifier
                    .height(60.dp)
                    .padding(0.dp)
            ) {
                bottomSheetDataList.forEach { item ->
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = item.resourceId),
                                contentDescription = item.name,
                                modifier = Modifier.padding(bottom = 5.dp),
                                tint = Color.Black.copy(alpha = 0.7f)
                            )
                        },
                        label = {
                            Text(
                                text = item.name,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(0.dp)
                            )
                        },
                        onClick = item.action,
                        selected = false
                    )
                }
            }
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                when (state) {
                    is HasPhoto -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
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
        }
    )
}