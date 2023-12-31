package com.app.body_manage.ui.compare

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.body_manage.R
import com.app.body_manage.common.BottomSheet
import com.app.body_manage.common.BottomSheetData
import com.app.body_manage.data.dao.ComparePhotoHistoryDao
import com.app.body_manage.style.Colors.Companion.accentColor
import com.app.body_manage.style.Colors.Companion.secondPrimary
import com.app.body_manage.style.Colors.Companion.theme
import com.app.body_manage.util.DateUtil
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        floatingActionButton = {
            if (pagerState.currentPage == 0 &&
                uiState is CompareState.CompareItemsHasSet &&
                uiState.before != null &&
                uiState.after != null
            ) {
                FloatingActionButton(
                    onClick = {
                        saveHistory.invoke()
                        // Move to History
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page = 1,
                                pageOffset = 0F
                            )
                        }
                    },
                    backgroundColor = accentColor
                ) {
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
            modifier = Modifier.padding(it)
        ) {
            when (uiState) {
                is CompareState.CompareItemsHasSet -> {
                    val tabRowItems = listOf(
                        TabRowItem(stringResource(id = R.string.compare)) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CompareItem(
                                    stringResource(R.string.before),
                                    uiState.before,
                                    beforeSearchLauncher
                                )
                                CompareItem(
                                    stringResource(R.string.after),
                                    uiState.after,
                                    afterSearchLauncher
                                )
                            }
                        },
                        TabRowItem(stringResource(id = R.string.history)) {
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
                                        stringResource(id = R.string.message_not_yet_saved_history),
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
                                color = secondPrimary
                            )
                        },
                        backgroundColor = theme
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
                                    Text(
                                        text = item.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
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
                    .fillMaxWidth(0.98F)
                    .padding(vertical = 10.dp)
                    .background(
                        colorResource(id = R.color.compare_history_item_background),
                        RoundedCornerShape(5.dp)
                    )
                    .border(1.dp, Color.Transparent, RoundedCornerShape(5.dp)),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    CompareImage(
                        modifier = Modifier.weight(0.5F),
                        label = stringResource(id = R.string.before),
                        photoId = it.beforePhotoId,
                        photoUri = it.beforePhotoUri,
                        onClickPhoto = onClickPhoto,
                    )
                    CompareImage(
                        modifier = Modifier.weight(0.5F),
                        label = stringResource(id = R.string.after),
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

data class TabRowItem(
    val title: String,
    val screen: @Composable () -> Unit
)

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
                label = stringResource(id = R.string.date),
                before = beforeCalendarDate.toString(),
                diff = "${getDiffDays()}日",
                after = afterCalendarDate.toString(),
            )
            val gained = afterWeight >= beforeWeight
            val diff = (afterWeight * 10 - beforeWeight * 10) / 10
            TableRow(
                unit = "kg",
                label = stringResource(id = R.string.weight),
                before = beforeWeight.toString(),
                after = afterWeight.toString(),
                diff = if (gained) {
                    "+$diff"
                } else {
                    "-$diff"
                },
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
                    colorResource(R.color.compare_history_diff_background),
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
        date = DateUtil.localDateConvertMonthDay(item.date)
        weight = "${item.weight}kg"
    }
    Box(
        modifier = Modifier
            .fillMaxWidth(0.98F)
            .height(400.dp)
            .padding(5.dp)
            .background(
                color = colorResource(id = R.color.compare_item_background),
                shape = RoundedCornerShape(5.dp)
            )
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
                    Text(text = stringResource(id = R.string.date))
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        text = date,
                        textAlign = TextAlign.Center,
                    )
                }
                Divider(modifier = Modifier.fillMaxWidth(0.8F))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = stringResource(id = R.string.weight))
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(text = weight)
                }
                Divider(modifier = Modifier.fillMaxWidth(0.8F))
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = null,
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
                    if (item != null) {
                        AsyncImage(
                            model = item.photoUri,
                            contentScale = ContentScale.Crop,
                            contentDescription = null
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AddCircleOutline,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(
                                text = stringResource(id = R.string.message_please_set_photo_for_compare),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}
