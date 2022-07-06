package com.app.body_manage.ui.measure.list

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.app.body_manage.R
import com.app.body_manage.ui.measure.list.MeasureListState.BodyMeasureListState
import com.app.body_manage.ui.measure.list.MeasureListState.MealMeasureListState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import timber.log.Timber.Forest

@OptIn(ExperimentalPagerApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MeasureListScreen(
    uiState: MeasureListState,
    reload: () -> Unit,
) {
    val pages = listOf("体型", "食事")
    val pagerState = rememberPagerState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "体型管理") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Filled.ArrowBack,
                            contentDescription = null,
                            tint = colorResource(R.color.white)
                        )
                    }
                },
                backgroundColor = colorResource(id = R.color.purple_200)
            )
        },
        content = {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                    )
                }
            ) {
                pages.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            Forest.d(title)
                        },
                    )
                }
            }
            HorizontalPager(count = pages.size, state = pagerState) { page ->
                Box(modifier = Modifier.height(500.dp)) {
                    when (uiState) {
                        is BodyMeasureListState -> {
                            Text(text = "ボディー")
                            reload.invoke()
                            LazyColumn(content = {
                                items(uiState.list) { item ->
                                    Row {
                                        Text(item.ui.toString())
                                        Text(item.photoUri.toString())
                                    }
                                }
                            })
                        }
                        is MealMeasureListState -> {
                            Text(text = "食事")
                            reload.invoke()
                        }
                    }
                }
            }
        }
    )
}