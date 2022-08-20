package com.app.body_manage.ui.measure.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.R
import com.app.body_manage.ui.measure.list.MeasureListState.BodyMeasureListState
import com.app.body_manage.ui.measure.list.MeasureListState.MealMeasureListState
import com.app.body_manage.ui.photoList.PhotoListActivity
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MeasureListScreen(
    uiState: MeasureListState,
    switchPage: (MeasureType) -> Unit,
    bottomSheetDataList: List<PhotoListActivity.BottomSheetData>,
) {
    val pages = MeasureType.values()
    val pagerState = rememberPagerState()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect {
            switchPage.invoke(pages[it])
        }
    }

    Scaffold(
        content = { padding ->
            Column {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxHeight()
                ) {
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                color = colorResource(id = R.color.purple_700),
                                modifier = Modifier.pagerTabIndicatorOffset(
                                    pagerState,
                                    tabPositions
                                )
                            )
                        },
                    ) {
                        pages.forEachIndexed { index, type ->
                            Tab(
                                text = { Text(text = type.title, color = Color.Black) },
                                selected = pagerState.currentPage == index,
                                selectedContentColor = Color.Black,
                                onClick = { },

                                modifier = Modifier.background(colorResource(id = R.color.purple_200)),
                            )
                        }
                    }
                    HorizontalPager(
                        count = pages.size,
                        state = pagerState,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        when (uiState) {
                            is BodyMeasureListState -> {
                                if (uiState.list.isNotEmpty()) {
                                    LazyColumn(content = {
                                        items(uiState.list) { item ->
                                            Row {
                                                Text(item.ui.toString())
                                                Text(item.photoUri.toString())
                                            }
                                        }
                                    })
                                } else {
                                    Text(
                                        text = "未登録です\n右下のボタンから登録してください",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                            is MealMeasureListState -> {
                                Text(
                                    text = "今後食事も登録できるようにするよ！",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                )
                            }
                        }
                    }
                }
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