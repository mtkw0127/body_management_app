package com.app.body_manage.common

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.R

@Composable
fun BottomSheet(bottomSheetDataList: List<BottomSheetData>) {
    BottomNavigation(
        backgroundColor = Color(red = 232, green = 222, blue = 248),
        modifier = Modifier
            .height(60.dp)
            .padding(0.dp)
    ) {
        bottomSheetDataList.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.resourceId),
                        contentDescription = item.name,
                        modifier = Modifier.padding(bottom = 5.dp),
                        tint = Color.Black.copy(alpha = 0.7f)
                    )
                },
                label = {
                    Text(
                        text = item.name,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(0.dp)
                    )
                },
                onClick = item.action,
                selected = false
            )
        }
    }
}

data class BottomSheetData(val name: String, val resourceId: Int, val action: () -> Unit)

fun createBottomDataList(
    calendarAction: () -> Unit,
    compareAction: () -> Unit,
    photoListAction: () -> Unit,
    graphAction: () -> Unit,
): List<BottomSheetData> {
    return listOf(
        BottomSheetData(
            "カレンダー",
            R.drawable.ic_baseline_calendar_month_24,
            calendarAction
        ),
        BottomSheetData(
            "比較",
            R.drawable.ic_baseline_compare_arrows_24,
            compareAction,
        ),
        BottomSheetData(
            "写真",
            R.drawable.ic_baseline_photo_library_24,
            photoListAction
        ),
        BottomSheetData(
            "グラフ",
            R.drawable.ic_baseline_show_chart_24,
            graphAction
        ),
    )
}
