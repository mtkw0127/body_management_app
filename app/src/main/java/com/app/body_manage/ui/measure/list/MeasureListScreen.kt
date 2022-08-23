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
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.extension.toJapaneseTime
import com.app.body_manage.ui.photoList.PhotoListActivity
import com.google.accompanist.pager.ExperimentalPagerApi
import java.time.LocalDateTime
import kotlin.math.ceil
import kotlin.math.pow

@OptIn(ExperimentalPagerApi::class, ExperimentalFoundationApi::class)
@Composable
fun MeasureListScreen(
    uiState: MeasureListState,
    clickSaveBodyInfo: () -> Unit,
    bottomSheetDataList: List<PhotoListActivity.BottomSheetData>,
    clickBodyMeasureEdit: (LocalDateTime) -> Unit,
    clickFab: () -> Unit,
) {

    val tall = rememberSaveable { mutableStateOf("169") }

    Scaffold(
        content = { padding ->
            Column {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxHeight()
                ) {
                    when (uiState) {
                        is MeasureListState.BodyMeasureListState -> {
                            if (uiState.list.isNotEmpty()) {
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
                                            value = tall.value,
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            onValueChange = {
                                                tall.value = if (it.toDoubleOrNull() != null) {
                                                    it
                                                } else tall.value
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
                                        Button(onClick = { clickSaveBodyInfo.invoke() }) {
                                            Text(text = "保存")
                                        }
                                    }
                                }
                                Divider(modifier = Modifier.padding(12.dp))
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
                                        items(uiState.list) { item ->
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

                                                val tall2 = (tall.value.toDouble() / 100)
                                                    .pow(2)
                                                val bmi =
                                                    (ceil(item.weight / tall2 * 100) / 100).toString()
                                                Box(
                                                    contentAlignment = Alignment.Center,
                                                    modifier = Modifier
                                                        .padding(top = 6.dp)
                                                        .weight(1F)
                                                ) {
                                                    Text(
                                                        text = bmi,
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
                                    })
                            } else {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = "未登録です\n右下のボタンから登録してください",
                                        fontWeight = FontWeight.Bold,
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
    )
}