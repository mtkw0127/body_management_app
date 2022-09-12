package com.app.body_manage.ui.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import com.app.body_manage.common.BottomSheet
import com.app.body_manage.common.BottomSheetData

@Composable
fun SettingScreen(
    state: State<SettingUiState>,
    bottomSheetDataList: List<BottomSheetData>,
    notifyAction: (Boolean) -> Unit,
) {
    Scaffold(
        bottomBar = {
            BottomSheet(bottomSheetDataList = (bottomSheetDataList))
        }
    ) {
        when (state.value) {
            is SettingUiState.Settings -> {
                Column(modifier = Modifier.padding(it)) {
                    val setting = state.value as SettingUiState.Settings
                    TextAndLabel(setting.alarm, notifyAction)
                }
            }
            is SettingUiState.ErrorSettings -> {
                Column {
                    Text(text = "設定情報の取得に失敗しました。")
                }
            }
        }
    }
}

@Composable
private fun TextAndLabel(checked: Boolean, notifyAction: (Boolean) -> Unit) {
    Text("設定画面")
    Switch(checked = checked, onCheckedChange = notifyAction)
}