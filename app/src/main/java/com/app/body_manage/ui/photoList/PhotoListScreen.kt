package com.app.body_manage.ui.photoList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.body_manage.R
import com.app.body_manage.data.dao.BodyMeasurePhotoDao
import com.app.body_manage.ui.photoList.PhotoListActivity.BottomSheetData
import com.app.body_manage.ui.photoList.PhotoListState.HasPhoto
import com.app.body_manage.ui.photoList.PhotoListState.NoPhoto

@Composable
fun PhotoListScreen(
    state: PhotoListState,
    photoDetailAction: (Int) -> Unit,
    bottomSheetDataList: List<BottomSheetData>,
) {
    Scaffold(
        bottomBar = {
            BottomSheet(bottomSheetDataList)
        },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                when (state) {
                    is HasPhoto -> {
                        PhotoList(
                            photos = state.photos,
                            photoDetailAction = photoDetailAction,
                        )
                    }
                    is NoPhoto -> {
                        NoPhotoMessage()
                    }
                }
            }
        }
    )
}

@Composable
private fun PhotoList(
    photos: Map<String, List<BodyMeasurePhotoDao.PhotoData>>,
    photoDetailAction: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(200.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        photos.forEach { (date, photos) ->
            item(span = { GridItemSpan(3) }) {
                Text(
                    text = date,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(colorResource(id = R.color.purple_200))
                        .padding(2.dp)
                )
            }
            items(photos.size) {
                AsyncImage(
                    model = photos[it].photoUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clickable {
                            photoDetailAction.invoke(photos[it].photoId)
                        }
                        .height(200.dp)
                        .background(Color.Black),

                    )
            }
        }
    }
}

@Composable
private fun NoPhotoMessage() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "未登録です\n体型登録時に撮影した写真がここに表示されます",
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BottomSheet(bottomSheetDataList: List<BottomSheetData>) {
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
}