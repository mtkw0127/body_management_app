package com.app.body_manage.ui.selectTrainingMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.style.Colors
import com.app.body_manage.ui.common.TrainingMenuFilter
import com.app.body_manage.ui.common.TrainingMenuItem

@Composable
fun SelectTrainingMenuScreen(
    selectedPart: TrainingMenu.Part? = null, // nullは全てと判定する
    selectedType: TrainingMenu.Type? = null, // nullは全てと判定する
    trainingMenuList: List<TrainingMenu>,
    onClickMenu: (TrainingMenu) -> Unit,
    onClickBackPress: () -> Unit,
    onClickPart: (TrainingMenu.Part?) -> Unit = {},
    onClickType: (TrainingMenu.Type?) -> Unit = {},
) {
    Scaffold(
        topBar = {
            // TODO: TopAppBarを共通化
            TopAppBar(
                modifier = Modifier.background(Colors.theme),
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top),
                backgroundColor = Colors.theme,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.size(10.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable { onClickBackPress() },
                        tint = Color.Black
                    )
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 10.dp)
                .fillMaxSize()
        ) {
            item {
                TrainingMenuFilter(
                    selectedPart = selectedPart,
                    selectedType = selectedType,
                    onClickPart = onClickPart,
                    onClickType = onClickType,
                )
            }
            items(trainingMenuList) { trainingMenu ->
                TrainingMenuItem(
                    trainingMenu = trainingMenu,
                    onClick = onClickMenu
                )
                Spacer(modifier = Modifier.size(10.dp))
            }
        }
    }
}
