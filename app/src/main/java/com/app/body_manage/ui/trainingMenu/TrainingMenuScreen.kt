package com.app.body_manage.ui.trainingMenu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.data.model.createSampleOwnWeightTrainingMenu
import com.app.body_manage.data.model.createSampleTrainingMenu
import com.app.body_manage.style.Colors
import com.app.body_manage.ui.common.TrainingMenuItem

@Composable
fun TrainingMenuListScreen(
    trainingMenus: List<TrainingMenu>,
    onClickBackPress: () -> Unit = {},
//    onClickHistory: (TrainingMenu) -> Unit = {},
    onClickEdit: (TrainingMenu) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = Colors.theme) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.size(10.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable { onClickBackPress() },
                        tint = Color.Black
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(10.dp)
        ) {
            LazyColumn {
                items(trainingMenus) { menu ->
                    TrainingMenuItem(
                        trainingMenu = menu,
                        onClick = onClickEdit
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        }
    }
}

@Composable
@Preview
private fun TrainingMenuListScreenPreview() {
    TrainingMenuListScreen(
        trainingMenus =
        List(3) { eventIndex ->
            createSampleTrainingMenu(eventIndex.toLong())
        } + List(3) { eventIndex ->
            createSampleOwnWeightTrainingMenu(eventIndex.toLong() + 2)
        }
    )
}
