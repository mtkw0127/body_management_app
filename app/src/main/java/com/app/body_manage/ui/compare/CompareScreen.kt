package com.app.body_manage.ui.compare

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onClickDelete: (ComparePhotoHistoryDao.PhotoAndBodyMeasure) -> Unit,
    onClickPhoto: (Int) -> Unit,
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
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CompareItem("Before", uiState.before, beforeSearchLauncher)
                                CompareItem("After", uiState.after, afterSearchLauncher)
                            }
                        },
                        TabRowItem("履歴") {
                            if (uiState.compareHistory.isNotEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Top,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    HistoryList(
                                        uiState.compareHistory,
                                        onClickDelete,
                                        onClickPhoto,
                                    )
                                }
                            } else {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "体型比較がまだ保存されていません。",
                                        color = Color.Gray,
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center,
                                    )
                                }
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
private fun HistoryList(
    compareHistory: List<ComparePhotoHistoryDao.PhotoAndBodyMeasure>,
    onClickDelete: (ComparePhotoHistoryDao.PhotoAndBodyMeasure) -> Unit,
    onClickPhoto: (Int) -> Unit
) {
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
                    CompareImage(
                        modifier = Modifier.weight(0.5F),
                        label = "Before",
                        photoId = it.beforePhotoId,
                        photoUri = it.beforePhotoUri,
                        onClickPhoto = onClickPhoto,
                    )
                    CompareImage(
                        modifier = Modifier.weight(0.5F),
                        label = "After",
                        photoId = it.afterPhotoId,
                        photoUri = it.afterPhotoUri,
                        onClickPhoto = onClickPhoto,
                    )
                }
                TableData(compareHistory = it)
                Box(
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 10.dp, end = 5.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .background(
                                color = Color.Gray,
                                shape = CircleShape
                            )
                            .padding(5.dp)
                            .size(26.dp)
                            .clickable {
                                onClickDelete.invoke(it)
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun CompareImage(
    modifier: Modifier,
    label: String,
    photoUri: String,
    onClickPhoto: (Int) -> Unit,
    photoId: Int,
) {
    Box(
        modifier = modifier
            .padding(5.dp)
    ) {
        AsyncImage(
            model = photoUri,
            contentScale = ContentScale.Inside,
            contentDescription = null,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable {
                    onClickPhoto(photoId)
                }
        )
        Text(
            label,
            modifier = Modifier
                .background(Color.Black)
                .padding(5.dp),
            color = Color.White,
        )
    }
}

/** 日付・体重のデータ*/
@Composable
private fun TableData(compareHistory: ComparePhotoHistoryDao.PhotoAndBodyMeasure) {
    Column(
        modifier = Modifier
            .padding(5.dp)
    ) {
        with(compareHistory) {
            TableRow(
                label = "日付",
                before = beforeCalendarDate.toString(),
                diff = "${getDiffDays()}日",
                after = afterCalendarDate.toString(),
            )
            TableRow(
                unit = "kg",
                label = "体重",
                before = beforeWeight.toString(),
                after = afterWeight.toString(),
                diff = (afterWeight - beforeWeight).toString()
            )
        }
    }
}

@Composable
private fun TableRow(
    label: String,
    before: String,
    diff: String,
    after: String,
    unit: String? = null,
) {
    val weight1 = 0.2F
    val weight2 = 0.3F
    val weight3 = 0.2F
    val weight4 = 0.3F
    val beforeStr = if (unit != null) before + unit else before
    val afterStr = if (unit != null) after + unit else after
    val diffStr = if (unit != null) diff + unit else diff
    Row(
        modifier = Modifier
            .height(40.dp)
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
                label,
                textAlign = TextAlign.Justify,
                fontSize = 16.sp,
            )
        }
        Text(
            text = beforeStr,
            modifier = Modifier
                .weight(weight2),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
        )
        Box(
            modifier = Modifier
                .background(
                    Color.Red,
                    shape = RoundedCornerShape(topEnd = 25.dp, bottomEnd = 25.dp)
                )
                .padding(3.dp)
                .weight(weight3),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = diffStr,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = Color.White,
            )
        }
        Text(
            text = afterStr,
            modifier = Modifier
                .weight(weight4),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
        )
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