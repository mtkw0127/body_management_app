package com.app.body_manage.ui.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.R
import com.app.body_manage.common.BottomSheet
import com.app.body_manage.common.BottomSheetData

@Composable
fun SettingScreen(
    state: State<SettingUiState>,
    bottomSheetDataList: List<BottomSheetData>,
    notifyAction: (Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.screen_name_settings),
                    )
                },
                backgroundColor = colorResource(id = R.color.app_theme)
            )
        },
        bottomBar = {
            BottomSheet(bottomSheetDataList = (bottomSheetDataList))
        }
    ) {
        when (state.value) {
            is SettingUiState.Settings -> {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .padding(start = 12.dp, top = 12.dp)
                ) {
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
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.label_notify_at_seven),
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 0.dp, end = 10.dp, top = 0.dp, bottom = 0.dp)
        )
        Switch(
            checked = checked,
            onCheckedChange = notifyAction,
            modifier = Modifier.offset(y = 3.dp)
        )
    }
}
