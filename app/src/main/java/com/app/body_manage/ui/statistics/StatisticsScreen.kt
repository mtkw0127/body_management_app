package com.app.body_manage.ui.statistics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.R
import com.app.body_manage.data.local.UserPreference
import com.app.body_manage.data.model.BodyMeasure
import com.app.body_manage.extension.age
import com.app.body_manage.extension.toCentiMeter
import com.app.body_manage.extension.toWeight
import com.app.body_manage.extension.withPercent
import com.app.body_manage.style.Colors
import com.app.body_manage.ui.top.HorizontalLine
import com.app.body_manage.ui.top.IconAndText
import com.app.body_manage.ui.top.Panel
import com.app.body_manage.ui.top.TextWithUnderLine

@Composable
fun StatisticsScreen(
    userPreference: UserPreference?,
    bodyMeasure: BodyMeasure?,
    onClickBackPress: () -> Unit,
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
                .padding(horizontal = 10.dp)
                .padding(top = 10.dp)
        ) {
            Panel(content = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextWithUnderLine(R.string.label_current_you)
                    Spacer(modifier = Modifier.size(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {
                        ColumTextWithLabelAndIcon(
                            title = stringResource(id = R.string.weight),
                            value = bodyMeasure?.weight?.toWeight() ?: "-",
                        )
                        ColumTextWithLabelAndIcon(
                            title = stringResource(id = R.string.tall),
                            value = userPreference?.tall?.toCentiMeter() ?: "-",
                        )
                        ColumTextWithLabelAndIcon(
                            title = stringResource(id = R.string.age),
                            value = userPreference?.birth?.age().toString(),
                        )
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {
                        ColumTextWithLabelAndIcon(
                            title = stringResource(id = R.string.label_bmi),
                            value = userPreference?.bim ?: "-",
                        )
                        ColumTextWithLabelAndIcon(
                            title = stringResource(id = R.string.label_kcal) + "※",
                            value = userPreference?.basicConsumeEnergy ?: "-",
                        )
                        ColumTextWithLabelAndIcon(
                            title = stringResource(id = R.string.label_fat),
                            value = userPreference?.calcFat?.withPercent() ?: "-",
                        )
                    }
                }
            }) {
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    stringResource(id = R.string.message_this_is_estimated_value),
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = null)
            }
            Panel(
                content = {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextWithUnderLine(R.string.label_result_diagnosis)
                        Spacer(modifier = Modifier.size(10.dp))
                        val bmi = userPreference?.bim?.toFloat()
                        if (bmi != null) {
                            if (bmi < 18.5) {
                                Text(text = stringResource(id = R.string.label_result_diagnosis_too_low))
                            }
                            if (bmi in 18.5..24.9) {
                                Text(text = stringResource(id = R.string.label_result_diagnosis_good))
                            }
                            if (24.9 < bmi) {
                                Text(text = stringResource(id = R.string.label_result_diagnosis_too_weight))
                            }
                        }

                        HorizontalLine()

                        IconAndText(
                            icon = Icons.Default.AccessibilityNew,
                            text = stringResource(id = R.string.label_healthy_weight),
                            withArrow = false,
                            message = userPreference?.healthyDuration ?: "-",
                            subTitle = stringResource(id = R.string.label_weight_bmi_18_25)
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun RowScope.ColumTextWithLabelAndIcon(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.weight(1F)
    ) {
        Text(text = title)
        Spacer(modifier = Modifier.size(5.dp))
        Text(text = value)
    }
}
