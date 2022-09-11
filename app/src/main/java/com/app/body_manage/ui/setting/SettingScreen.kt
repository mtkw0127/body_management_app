package com.app.body_manage.ui.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.app.body_manage.common.BottomSheet
import com.app.body_manage.common.BottomSheetData

@Composable
fun SettingScreen(
    checked: MutableState<Boolean>,
    bottomSheetDataList: List<BottomSheetData>,
    notifyAction: (Boolean) -> Unit,
) {
    Scaffold(
        bottomBar = {
            BottomSheet(bottomSheetDataList = (bottomSheetDataList))
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            TextAndLabel(checked, notifyAction)
        }
    }
}

@Composable
private fun TextAndLabel(checked: MutableState<Boolean>, notifyAction: (Boolean) -> Unit) {
    Text("設定画面")
    Switch(checked = checked.value, onCheckedChange = notifyAction)
}