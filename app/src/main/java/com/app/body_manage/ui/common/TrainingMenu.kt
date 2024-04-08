package com.app.body_manage.ui.common

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.body_manage.R
import com.app.body_manage.data.model.TrainingMenu

@Composable
fun TrainingMenuItem(
    trainingMenu: TrainingMenu,
    onClick: (TrainingMenu) -> Unit,
) {
    val cornerShape = RoundedCornerShape(10.dp)
    Column(
        modifier = Modifier
            .shadow(1.dp, cornerShape)
            .fillMaxWidth()
            .border(0.5.dp, Color.DarkGray, cornerShape)
            .background(Color.White, cornerShape)
            .padding(10.dp)
            .clickable { onClick(trainingMenu) }
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
