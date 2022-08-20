package com.app.body_manage.ui.measure.list

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.app.body_manage.R
import com.app.body_manage.ui.measure.list.MeasureListState.BodyMeasureListState
import com.app.body_manage.ui.measure.list.MeasureListState.MealMeasureListState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MeasureListScreen(
    uiState: MeasureListState,
    switchPage: (MeasureType) -> Unit,
) {
    val context = LocalContext.current
    val pages = MeasureType.values()
    val pagerState = rememberPagerState()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect {
            switchPage.invoke(pages[it])
        }
    }

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
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
            ) {
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                        )
                    }
                ) {
                    pages.forEachIndexed { index, type ->
                        Tab(
                            text = { Text(text = type.title) },
                            selected = pagerState.currentPage == index,
                            onClick = {
                                Toast.makeText(context, type.title, Toast.LENGTH_LONG)
                                    .show()
                            },
                        )
                    }
                }
                HorizontalPager(
                    count = pages.size,
                    state = pagerState,
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    when (uiState) {
                        is BodyMeasureListState -> {
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
                            LazyColumn(content = {
                                items(uiState.list) { item ->
                                    Row {
                                        Text(item.ui.toString())
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }
    )
}