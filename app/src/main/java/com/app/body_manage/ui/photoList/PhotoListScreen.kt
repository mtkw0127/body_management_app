package com.app.body_manage.ui.photoList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.body_manage.R
import com.app.body_manage.common.BottomSheet
import com.app.body_manage.common.BottomSheetData
import com.app.body_manage.extension.toMMDD
import com.app.body_manage.extension.toWeight
import com.app.body_manage.style.Colors.Companion.accentColor
import com.app.body_manage.style.Colors.Companion.disable
import com.app.body_manage.ui.photoList.PhotoListState.HasPhoto
import com.app.body_manage.ui.photoList.PhotoListState.NoPhoto
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

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
                                disable
                            }
                        ),
                        modifier = Modifier
                            .weight(1F)
                            .padding(10.dp)
                    ) {
                        Text(
                            stringResource(id = R.string.date),
                            fontSize = 14.sp,
                            color = if (state.type == SortType.DATE) {
                                nonAccentTextColor
                            } else {
                                accentTextColor
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
                                disable
                            }
                        ),
                        modifier = Modifier
                            .weight(1F)
                            .padding(10.dp)
                    ) {
                        Text(
                            stringResource(id = R.string.weight),
                            fontSize = 14.sp,
                            color = if (state.type == SortType.WEIGHT) {
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
            modifier = Modifier.safeDrawingPadding(),
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
                if (state is HasPhoto) {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                if (modalSheetState.currentValue == ModalBottomSheetValue.Hidden) {
                                    modalSheetState.show()
                                } else {
                                    modalSheetState.hide()
                                }
                            }
                        },
                        backgroundColor = accentColor
                    ) {
                        Icon(Icons.Filled.Sort, contentDescription = null)
                    }
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
    val columns = 3
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(columns),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        state.photos.forEach { (label, photos) ->
            item(span = { GridItemSpan(columns) }) {
                val labelWithUnit = when (state.type) {
                    SortType.WEIGHT -> {
                        label + "kg"
                    }

                    SortType.DATE -> {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(label)?.let {
                            SimpleDateFormat("MM月dd日 (E)", Locale.JAPAN).format(it)
                        } ?: label
                    }
                }
                Text(
                    text = labelWithUnit,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .background(Color.White)
                        .padding(8.dp)
                )
            }
            items(photos) { photo ->
                Box(modifier = Modifier.sizeIn(maxHeight = 150.dp)) {
                    AsyncImage(
                        model = photo.photoUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clickable {
                                photoDetailAction.invoke(photo.photoId)
                            }
                    )
                    Box(
                        modifier = Modifier.offset(x = 5.dp, y = 5.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = when (state.type) {
                                SortType.DATE -> {
                                    photo.weight.toWeight()
                                }

                                SortType.WEIGHT -> {
                                    photo.calendarDate.toMMDD()
                                }
                            },
                            color = Color.Black,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .width(70.dp)
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(3.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NoPhotoMessage() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.message_no_photo),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
        )
    }
}
