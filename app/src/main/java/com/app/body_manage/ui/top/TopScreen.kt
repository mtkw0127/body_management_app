package com.app.body_manage.ui.top

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.common.BottomSheet
import com.app.body_manage.common.BottomSheetData
import com.app.body_manage.style.Colors.Companion.accentColor
import com.app.body_manage.style.Colors.Companion.background

@Composable
fun TopScreen(
    bottomSheetDataList: List<BottomSheetData>,
    onClickCalendar: () -> Unit = {},
    onClickAdd: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            BottomSheet(bottomSheetDataList)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onClickAdd() },
                backgroundColor = accentColor
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .background(background)
                .padding(it)
                .padding(10.dp)
                .fillMaxHeight()
        ) {
            item {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "63.2",
                        fontSize = 32.sp,
                        color = Color.Black,
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = "kg",
                        fontSize = 18.sp,
                        color = Color.Gray,
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
            item {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "目標を設定しましょう。")
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
            item {
                PanelRow {
                    ColumTextWithLabelAndIcon(
                        title = "BMI",
                        value = "22",
                    )
                    VerticalLine()
                    ColumTextWithLabelAndIcon(
                        title = "kcal/日",
                        value = "1900",
                    )
                    VerticalLine()
                    ColumTextWithLabelAndIcon(
                        title = "体脂肪率",
                        value = "22%",
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
            item {
                PanelColumn {
                    IconAndText(
                        icon = Icons.Default.CalendarMonth,
                        onClick = { onClickCalendar() },
                        text = "カレンダーで見る"
                    )
                    HorizontalLine()
                    IconAndText(
                        icon = Icons.Default.Calculate,
                        onClick = { onClickCalendar() },
                        text = "統計"
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
            item {
                PanelColumn {
                    IconAndText(
                        icon = Icons.Default.Flag,
                        onClick = { onClickCalendar() },
                        text = "開始点"
                    )
                    HorizontalLine()
                    IconAndText(
                        icon = Icons.Default.Check,
                        onClick = { onClickCalendar() },
                        text = "理想体重"
                    )
                }
            }
        }
    }
}

@Composable
private fun VerticalLine() {
    Box(
        modifier = Modifier
            .width(width = 1.dp)
            .height(height = 50.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(1.dp))
    )
}

@Composable
private fun ColumTextWithLabelAndIcon(
    title: String,
    value: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = title)
        Spacer(modifier = Modifier.size(5.dp))
        Text(text = value)
    }
}

@Composable
private fun HorizontalLine() {
    Box(
        modifier = Modifier
            .padding(vertical = 20.dp)
            .fillMaxWidth()
            .height(height = 1.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(1.dp))
    )
}

@Composable
private fun IconAndText(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = text,
        )
        Spacer(modifier = Modifier.weight(1F))
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = null,
            modifier = Modifier.size(10.dp)
        )
        Spacer(modifier = Modifier.size(10.dp))
    }
}

@Composable
private fun PanelColumn(
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .shadow(2.dp)
            .background(
                Color.White,
                RoundedCornerShape(5.dp)
            )
            .fillMaxWidth()
            .padding(20.dp),
    ) {
        content()
    }
}

@Composable
private fun PanelRow(
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .shadow(2.dp)
            .background(
                Color.White,
                RoundedCornerShape(5.dp)
            )
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}