package com.app.body_manage.ui.measure.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.common.BottomSheet
import com.app.body_manage.common.BottomSheetData
import com.app.body_manage.data.entity.BodyMeasureModel
import com.app.body_manage.extension.toJapaneseTime
import java.time.LocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MeasureListScreen(
    uiState: MeasureListState,
    clickSaveBodyInfo: () -> Unit,
    bottomSheetDataList: List<BottomSheetData>,
    setTall: (String) -> Unit,
    resetSnackBarMessage: () -> Unit,
    clickBodyMeasureEdit: (LocalDateTime) -> Unit,
    clickFab: () -> Unit,
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val state = rememberScaffoldState()
    Scaffold(
        scaffoldState = state,
        content = { padding ->
            Column {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(top = 10.dp)
                        .fillMaxHeight()
                ) {
                    when (uiState) {
                        is MeasureListState.BodyMeasureListState -> {
                            if (uiState.message.isNotEmpty()) {
                                LaunchedEffect(uiState.message) {
                                    coroutineScope.launch {
                                        state.snackbarHostState.showSnackbar(
                                            message = uiState.message,
                                            duration = SnackbarDuration.Short
                                        )
                                        resetSnackBarMessage.invoke()
                                    }
                                }
                            }
                            if (uiState.list.isNotEmpty()) {
                                TallSetField(
                                    tall = uiState.tall,
                                    setTall = setTall,
                                    clickSaveBodyInfo = clickSaveBodyInfo,
                                )
                                Divider(modifier = Modifier.padding(12.dp))
                                BodyMeasureList(
                                    list = uiState.list,
                                    clickBodyMeasureEdit = clickBodyMeasureEdit,
                                )
                            } else {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = "未登録です\n右下のボタンから登録してください",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = clickFab) {
                Icon(Icons.Filled.Add, contentDescription = "体型登録")
            }
        },
        bottomBar = {
            BottomSheet(bottomSheetDataList)
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TallSetField(
    tall: String,
    setTall: (String) -> Unit,
    clickSaveBodyInfo: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column {
        Row(
            modifier = Modifier
                .height(60.dp)
                .padding(start = 12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 16.dp)
            ) {
                Text(text = "身長[cm]")
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 16.dp)
            ) {
                TextField(
                    value = tall,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = {
                        if (it.toDoubleOrNull() != null ||
                            it.startsWith("0")
                                .not()
                        ) {
                            setTall.invoke(it)
                        }
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .height(48.dp)
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                Button(onClick = {
                    keyboardController?.hide()
                    clickSaveBodyInfo.invoke()
                }) {
                    Text(text = "保存")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BodyMeasureList(
    list: List<BodyMeasureModel>,
    clickBodyMeasureEdit: (LocalDateTime) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        content = {
            stickyHeader {
                Row {
                    DisplayMeasureColumn.values().forEach {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1F)
                                .padding(
                                    start = 3.dp,
                                    end = 3.dp,
                                    bottom = 3.dp,
                                )
                        ) {
                            Text(
                                text = it.display,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
            items(list) { item ->
                Row(
                    Modifier
                        .wrapContentHeight()
                        .padding(top = 3.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .weight(1F)
                    ) {
                        Text(
                            text = item.capturedLocalDateTime.toJapaneseTime(),
                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .weight(1F)
                    ) {
                        Text(
                            text = item.weight.toString() + "Kg",
                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .weight(1F)
                    ) {
                        Text(
                            text = item.fat.toString() + "%",
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .weight(1F)
                    ) {
                        Text(
                            text = item.bmi,
                        )
                    }
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "体型登録",
                        modifier = Modifier
                            .weight(1F)
                            .padding(3.dp)
                            .clickable {
                                clickBodyMeasureEdit.invoke(
                                    item.capturedLocalDateTime
                                )
                            },
                        tint = Color.Gray,
                    )
                }
            }
        },
    )
}