package com.app.body_manage.ui.trainingMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.body_manage.R
import com.app.body_manage.common.toCount
import com.app.body_manage.common.toKg
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.data.model.createSampleOwnWeightTrainingMenu
import com.app.body_manage.data.model.createSampleTrainingMenu

@Composable
fun TrainingMenuListScreen(
    trainingMenus: List<TrainingMenu>,
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(10.dp)
        ) {
            LazyColumn {
                items(trainingMenus) { menu ->
                    TrainingMenu(trainingMenu = menu)
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        }
    }
}

@Composable
private fun TrainingMenu(
    trainingMenu: TrainingMenu,
) {
    val cornerShape = RoundedCornerShape(10.dp)
    Column(
        modifier = Modifier
            .shadow(1.dp, cornerShape)
            .fillMaxWidth()
            .border(0.5.dp, Color.DarkGray, cornerShape)
            .background(Color.White, cornerShape)
            .padding(10.dp)
    ) {
        Row {
            Text(text = stringResource(id = R.string.label_menu_type_name))
            Text(text = trainingMenu.name)
        }
        Row {
            Text(text = stringResource(id = R.string.label_training_target_part))
            Text(text = stringResource(trainingMenu.part.nameStringResourceId))
        }
        trainingMenu.sets.forEach { set ->
            Row {
                // Nセット目
                Text(text = stringResource(id = R.string.label_set, set.index))
                // N[kg]
                if (set is TrainingMenu.WeightSet) {
                    Text(text = set.targetWeight.toKg())
                }
                // N[回]
                Text(text = set.targetNumber.toCount())
            }
        }
    }
}

@Composable
@Preview
private fun TrainingMenuPreview() {
    TrainingMenu(
        trainingMenu = createSampleTrainingMenu()
    )
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