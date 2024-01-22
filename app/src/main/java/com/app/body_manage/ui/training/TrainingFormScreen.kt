package com.app.body_manage.ui.training

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.data.model.Training
import com.app.body_manage.data.model.createSampleTrainingMenu
import com.app.body_manage.extension.toJapaneseTime
import com.app.body_manage.extension.toMMDDEE
import com.app.body_manage.style.Colors.Companion.background
import com.app.body_manage.style.Colors.Companion.theme
import com.app.body_manage.ui.top.PanelColumn
import java.time.LocalDateTime

@Composable
fun TrainingFormScreen(
    training: Training,
    onClickBackPress: () -> Unit = {},
    onClickRegister: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = theme) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.size(10.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable { onClickBackPress() },
                        tint = Color.Black
                    )
                    Text(
                        text = training.dateTime.toLocalDate().toMMDDEE(),
                        modifier = Modifier.offset(x = 10.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .background(theme)
                    .height(50.dp)
                    .fillMaxWidth()
                    .clickable {
                        onClickRegister()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "登録する")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.size(5.dp))

            PanelColumn {
                Text(text = training.dateTime.toJapaneseTime())
            }

            Spacer(modifier = Modifier.size(5.dp))

            PanelColumn {
                Text(text = "メモ")
                Spacer(modifier = Modifier.size(10.dp))
                TextField(value = training.memo, onValueChange = {})
            }

            Spacer(modifier = Modifier.size(5.dp))

            PanelColumn {
                Text(text = "トレーニング内容")
                Spacer(modifier = Modifier.size(10.dp))
                training.menus.forEach { menu ->
                    Column {
                        Text(
                            text = stringResource(id = menu.type.nameStringRes) + ":" + stringResource(
                                menu.part.nameStringResourceId
                            )
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text(text = menu.name)
                        Spacer(modifier = Modifier.size(5.dp))
                        menu.sets.forEach { set ->
                            Text(text = "${set.index}セット目")
                            Text(text = set.numberString)
                            Text(text = set.weightString)
                            Spacer(modifier = Modifier.size(5.dp))
                        }
                        TextField(value = menu.memo, onValueChange = {})
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        }
    }
}

@Composable
@Preview
private fun TrainingFormPreview() {
    TrainingFormScreen(
        training = Training(
            id = Training.NEW_ID,
            dateTime = LocalDateTime.now(),
            menus = listOf(
                createSampleTrainingMenu(),
                createSampleTrainingMenu(),
                createSampleTrainingMenu(),
            ),
            memo = "メモ".repeat(5)
        ),
        onClickBackPress = {},
    )
}
