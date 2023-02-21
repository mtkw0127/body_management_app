package com.app.body_manage.ui.photoList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.body_manage.common.BottomSheet
import com.app.body_manage.common.BottomSheetData
import com.app.body_manage.style.Colors.Companion.accentColor
import com.app.body_manage.style.Colors.Companion.nonAccentColor
import com.app.body_manage.ui.photoList.PhotoListState.HasPhoto
import com.app.body_manage.ui.photoList.PhotoListState.NoPhoto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PhotoListScreen(
    state: PhotoListState,
    photoDetailAction: (Int) -> Unit,
    onClickSortType: (SortType) -> Unit,
    bottomSheetDataList: List<BottomSheetData>,
) {
    val modalSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            Column(modifier = Modifier.padding(top = 16.dp, start = 4.dp, end = 4.dp)) {
                Text(
                    "並び替え",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
                val accentTextColor = Color.White
                val nonAccentTextColor = Color.Black
                Row {
                    Button(
                        onClick = {
                            onClickSortType(SortType.DATE)
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (state.type == SortType.DATE) {
                                accentColor
                            } else {
                                nonAccentColor
                            }
                        ),
                        modifier = Modifier
                            .weight(1F)
                            .padding(10.dp)
                    ) {
                        Text(
                            "日付", fontSize = 14.sp, color = if (state.type == SortType.DATE) {
                                accentTextColor
                            } else {
                                nonAccentTextColor
                            }
                        )
                    }
                    Button(
                        onClick = {
                            onClickSortType(SortType.WEIGHT)
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (state.type == SortType.WEIGHT) {
                                accentColor
                            } else {
                                nonAccentColor
                            }
                        ),
                        modifier = Modifier
                            .weight(1F)
                            .padding(10.dp)
                    ) {
                        Text(
                            "体重", fontSize = 14.sp, color = if (state.type == SortType.WEIGHT) {
                                accentTextColor
                            } else {
                                nonAccentTextColor
                            }
                        )
                    }
                }
            }
        }
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
                                state = state,
                                photoDetailAction = photoDetailAction,
                            )
                        }
                        is NoPhoto -> {
                            NoPhotoMessage()
                        }
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    scope.launch {
                        if (modalSheetState.currentValue == ModalBottomSheetValue.Hidden) {
                            modalSheetState.show()
                        } else {
                            modalSheetState.hide()
                        }
                    }
                }) {
                    Icon(Icons.Filled.Sort, contentDescription = null)
                }
            }
        )
    }
}

@Composable
private fun PhotoList(
    photoDetailAction: (Int) -> Unit,
    state: HasPhoto
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(200.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        state.photos.forEach { (label, photos) ->
            item(span = { GridItemSpan(3) }) {
                val labelWithUnit = when (state.type) {
                    SortType.WEIGHT -> {
                        label + "kg"
                    }
                    else -> label
                }
                Text(
                    text = labelWithUnit,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(nonAccentColor)
                        .padding(4.dp)
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
                        .height(250.dp)
                )
                Text(
                    text = when (state.type) {
                        SortType.DATE -> {
                            "${photos[it].weight}kg"
                        }
                        SortType.WEIGHT -> {
                            "${photos[it].calendarDate}"
                        }
                    },
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(3.dp)
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
