package com.app.body_manage.ui.measure.list

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.app.body_manage.R
import com.app.body_manage.ui.measure.list.MeasureListState.BodyMeasureListState
import com.app.body_manage.ui.measure.list.MeasureListState.MealMeasureListState

@Composable
fun MeasureListScreen(
    uiState: MeasureListState
) {
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
            when (uiState) {
                is BodyMeasureListState -> {

                }
                is MealMeasureListState -> {

                }
            }
        }
    )
}