package com.app.body_manage.common

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.R

@Composable
fun BottomSheet(bottomSheetDataList: List<BottomSheetData>) {
    BottomNavigation(
        backgroundColor = colorResource(id = R.color.app_theme),
        modifier = Modifier
            .height(60.dp)
            .padding(0.dp)
    ) {
        bottomSheetDataList.forEach { item ->
            BottomNavigationItem(
                icon = item.icon,
                label = if (item.isSelected) {
                    {
                        Text(
                            text = item.name,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(0.dp)
                        )
                    }
                } else null,
                onClick = item.action,
                selected = item.isSelected
            )
        }
    }
}

data class BottomSheetData(
    val name: String,
    val icon: @Composable () -> Unit,
    val action: () -> Unit,
    val isSelected: Boolean,
)

fun createBottomDataList(
    topAction: () -> Unit,
    compareAction: () -> Unit,
    photoListAction: () -> Unit,
    graphAction: () -> Unit,
    isTop: Boolean = false,
    isCompare: Boolean = false,
    isPhotos: Boolean = false,
    isGraph: Boolean = false,
): List<BottomSheetData> {
    return listOf(
        BottomSheetData(
            "トップ",
            {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 5.dp),
                    tint = Color.Black.copy(alpha = 0.7f)
                )
            },
            topAction,
            isTop,
        ),
        BottomSheetData(
            "比較",
            {
                Icon(
                    imageVector = Icons.Default.CompareArrows,
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 5.dp),
                    tint = Color.Black.copy(alpha = 0.7f)
                )
            },
            compareAction,
            isCompare,
        ),
        BottomSheetData(
            "写真",
            {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 5.dp),
                    tint = Color.Black.copy(alpha = 0.7f)
                )
            },
            photoListAction,
            isPhotos
        ),
        BottomSheetData(
            "グラフ",
            {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_show_chart_24),
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 5.dp),
                    tint = Color.Black.copy(alpha = 0.7f)
                )
            },
            graphAction,
            isGraph
        ),
    )
}
