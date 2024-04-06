package com.app.body_manage.ui.trainingMenu

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.body_manage.R
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.data.model.createSampleOwnWeightTrainingMenu
import com.app.body_manage.data.model.createSampleTrainingMenu
import com.app.body_manage.style.Colors

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
                    TrainingMenu(
                        trainingMenu = menu,
//                        onClickHistory = onClickHistory,
                        onClickEdit = onClickEdit,
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TrainingMenu(
    trainingMenu: TrainingMenu,
    onClickEdit: (TrainingMenu) -> Unit,
//    onClickHistory: (TrainingMenu) -> Unit,
) {
    val cornerShape = RoundedCornerShape(10.dp)
    Column(
        modifier = Modifier
            .shadow(1.dp, cornerShape)
            .fillMaxWidth()
            .border(0.5.dp, Color.DarkGray, cornerShape)
            .background(Color.White, cornerShape)
            .padding(10.dp)
            .clickable { onClickEdit(trainingMenu) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                LabelAndContentRow(
                    label = R.string.label_menu_type_name
                ) {
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(text = trainingMenu.name)
                }
                Spacer(modifier = Modifier.size(10.dp))
                LabelAndContentRow(
                    label = R.string.label_training_target_part
                ) {
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(text = stringResource(trainingMenu.part.nameStringResourceId))
                }
            }
        }
    }
}

@Composable
private fun LabelAndContentRow(
    @StringRes label: Int,
    content: @Composable () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(id = label),
            modifier = Modifier
                .background(Color.Black, RoundedCornerShape(5.dp))
                .padding(5.dp),
            color = Color.White,
        )
        Spacer(modifier = Modifier.size(10.dp))
        content()
    }
}

@Composable
@Preview
private fun TrainingMenuListScreenPreview() {
    TrainingMenuListScreen(
        trainingMenus =
        List(3) {
            createSampleTrainingMenu()
        } + List(3) {
            createSampleOwnWeightTrainingMenu()
        }
    )
}
