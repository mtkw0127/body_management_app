package com.app.body_manage.ui.compare

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.body_manage.common.BottomSheet
import com.app.body_manage.common.BottomSheetData
import com.app.body_manage.data.dao.ComparePhotoHistoryDao
import com.app.body_manage.style.Colors
import com.app.body_manage.util.DateUtil
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

data class TabRowItem(
    val title: String,
    val screen: @Composable () -> Unit
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CompareScreen(
    bottomSheetDataList: List<BottomSheetData>,
    uiState: CompareState,
    saveHistory: () -> Unit,
    loadHistory: () -> Unit,
    beforeSearchLauncher: () -> Unit,
    afterSearchLauncher: () -> Unit,
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        floatingActionButton = {
            if (pagerState.currentPage == 0) {
                FloatingActionButton(onClick = { saveHistory.invoke() }) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null
                    )
                }
            }
        },
        bottomBar = {
            BottomSheet(bottomSheetDataList = bottomSheetDataList)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            when (uiState) {
                is CompareState.CompareItemsHasSet -> {
                    val tabRowItems = listOf(
                        TabRowItem("比較") {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CompareItem("Before", uiState.before, beforeSearchLauncher)
                                CompareItem("After", uiState.after, afterSearchLauncher)
                            }
                        },
                        TabRowItem("履歴") {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                HistoryList(uiState.compareHistory)
                            }
                        }
                    )
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                                color = MaterialTheme.colors.secondary
                            )
                        }
                    ) {
                        tabRowItems.forEachIndexed { index, item ->
                            SideEffect {
                                if (pagerState.currentPage == 1) {
                                    loadHistory.invoke()
                                }
                            }
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(
                                            index
                                        )
                                    }
                                },
                                text = {
                                    Text(text = item.title)
                                }
                            )
                        }
                    }
                    HorizontalPager(
                        count = tabRowItems.size,
                        state = pagerState,
                    ) {
                        tabRowItems[pagerState.currentPage].screen()
                    }
                }
                is CompareState.CompareItemsError -> {

                }
            }
        }
    }
}

@Composable
private fun HistoryList(compareHistory: List<ComparePhotoHistoryDao.PhotoAndBodyMeasure>) {
    LazyColumn {
        items(compareHistory) {
            Column(
                Modifier
                    .fillMaxWidth(0.95F)
                    .padding(vertical = 15.dp)
                    .background(Color.LightGray, RoundedCornerShape(5.dp))
                    .border(1.dp, Color.Transparent, RoundedCornerShape(5.dp)),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    AsyncImage(
                        model = it.beforePhotoUri,
                        contentScale = ContentScale.Inside,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(0.5F)
                            .padding(5.dp)
                    )
                    AsyncImage(
                        model = it.afterPhotoUri,
                        contentScale = ContentScale.Inside,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(0.5F)
                            .padding(5.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                ) {
                    val weight1 = 0.1F
                    val weight2 = 0.3F
                    val weight3 = 0.3F
                    val weight4 = 0.3F
                    Row(
                        modifier = Modifier
                            .height(30.dp)
                            .border(1.dp, Color.Black)
                            .background(Color.White),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(weight1)
                                .border(1.dp, Color.Black)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "日付",
                                textAlign = TextAlign.Justify
                            )
                        }
                        Text(
                            text = "${it.beforeCalendarDate}",
                            modifier = Modifier
                                .weight(weight2),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "100日",
                            modifier = Modifier
                                .weight(weight3),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${it.afterCalendarDate}",
                            modifier = Modifier
                                .weight(weight4),
                            textAlign = TextAlign.Center
                        )
                    }
                    Row(
                        modifier = Modifier
                            .height(30.dp)
                            .border(1.dp, Color.Black)
                            .background(Color.White),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(weight1)
                                .border(1.dp, Color.Black)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "体重",
                                textAlign = TextAlign.Justify
                            )
                        }
                        Text(
                            text = "${it.beforeWeight}kg",
                            modifier = Modifier
                                .weight(weight2),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${it.afterWeight - it.beforeWeight}kg",
                            modifier = Modifier
                                .weight(weight3),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${it.afterWeight}kg",
                            modifier = Modifier
                                .weight(weight4),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompareItem(label: String, item: CompareItemStruct?, onEditClick: () -> Unit) {
    var date = "-"
    var weight = "-"
    if (item != null) {
        date = DateUtil.localDateConvertJapaneseFormatYearMonthDay(item.date)
        weight = "${item.weight}kg"
    }
    Box(
        modifier = Modifier
            .fillMaxWidth(0.95F)
            .height(400.dp)
            .padding(5.dp)
            .background(color = Colors.secondThemeColor, shape = RoundedCornerShape(15.dp))
    ) {
        Row {
            Column(
                modifier = Modifier
                    .width(70.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = label, fontWeight = FontWeight.Bold)
                Divider(modifier = Modifier.fillMaxWidth(0.8F))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "日付")
                    Text(text = date)
                }
                Divider(modifier = Modifier.fillMaxWidth(0.8F))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "体重")
                    Text(text = weight)
                }
                Divider(modifier = Modifier.fillMaxWidth(0.8F))
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "体型登録",
                    modifier = Modifier.clickable {
                        onEditClick.invoke()
                    }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.95F)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .fillMaxHeight(0.95F)
                        .clickable {
                            onEditClick.invoke()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = item?.photoUri,
                        contentScale = ContentScale.Crop,
                        contentDescription = "変更前写真"
                    )
                }
            }
        }
    }
}