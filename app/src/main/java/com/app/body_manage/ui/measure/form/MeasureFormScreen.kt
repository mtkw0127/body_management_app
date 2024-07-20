package com.app.body_manage.ui.measure.form

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Notes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.R
import com.app.body_manage.common.CustomButton
import com.app.body_manage.common.CustomImage
import com.app.body_manage.data.model.Photo
import com.app.body_manage.extension.toCentiMeter
import com.app.body_manage.extension.toFat
import com.app.body_manage.extension.toJapaneseTime
import com.app.body_manage.extension.toMMDDEE
import com.app.body_manage.extension.toWeight
import com.app.body_manage.style.Colors.Companion.background
import com.app.body_manage.style.Colors.Companion.theme

@Composable
fun BodyMeasureFormScreen(
    uiState: FormState,
    onClickBackPress: () -> Unit = {},
    onClickTakePhoto: () -> Unit = {},
    onClickDelete: () -> Unit = {},
    onClickTall: () -> Unit = {},
    onClickSave: () -> Unit = {},
    onClickNextDay: () -> Unit = {},
    onClickPreviousDay: () -> Unit = {},
    onClickPhotoDetail: (Photo) -> Unit = {},
    onClickDeletePhoto: (Photo) -> Unit = {},
    onClickTime: () -> Unit = {},
    onChangeWeightDialog: () -> Unit = {},
    onChangeFatDialog: () -> Unit = {},
    onChangeMemo: (String) -> Unit = {},
) {
    Scaffold(
        modifier = Modifier
            .background(background)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)
            ),
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .background(colorResource(id = R.color.app_theme))
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                    ),
                backgroundColor = theme,
                elevation = 0.dp
            ) {
                if (uiState is FormState.HasData) {
                    val isAdd = uiState is FormState.HasData.Add
                    val isEdit = uiState is FormState.HasData.Edit
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.size(10.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.clickable { onClickBackPress() },
                            tint = Color.Black
                        )
                        Text(
                            text = uiState.measureDate.toMMDDEE(),
                            modifier = Modifier.offset(x = 10.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.size(40.dp))
                        if (isAdd) {
                            CustomButton(
                                onClick = { onClickPreviousDay() },
                                valueResourceId = R.string.prev_day
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            CustomButton(
                                onClick = { onClickNextDay() },
                                valueResourceId = R.string.next_day
                            )
                        }
                        if (isEdit) {
                            Spacer(modifier = Modifier.weight(1F))
                            CustomButton(
                                onClick = onClickDelete,
                                valueResourceId = R.string.delete,
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                        }
                    }
                }
            }
        },
    ) {
        when (uiState) {
            is FormState.HasData -> {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .padding(top = 10.dp)
                            .weight(1F)
                    ) {
                        item {
                            CustomTextField(
                                labelTextResourceId = R.string.hint_time,
                                value = uiState.model.time.toJapaneseTime(),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null
                                    )
                                },
                                onClick = { onClickTime() }
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                        }
                        item {
                            CustomTextField(
                                labelTextResourceId = R.string.tall,
                                value = (uiState.model.tall ?: 0F).toCentiMeter(),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Height,
                                        contentDescription = null
                                    )
                                },
                                onClick = { onClickTall() }
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                        }
                        item {
                            CustomTextField(
                                labelTextResourceId = R.string.hint_weight,
                                value = uiState.model.weight.toWeight(),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AccessibilityNew,
                                        contentDescription = null
                                    )
                                },
                                onClick = { onChangeWeightDialog() }
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                        }
                        item {
                            CustomTextField(
                                labelTextResourceId = R.string.hint_fat,
                                value = uiState.model.fat.toFat(),
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_baseline_opacity_24),
                                        contentDescription = null
                                    )
                                },
                                onClick = { onChangeFatDialog() }
                            )
                            Spacer(modifier = Modifier.size(15.dp))
                        }
                        item {
                            CustomMultiTextField(
                                labelTextResourceId = R.string.hint_memo,
                                value = uiState.model.memo,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Notes,
                                        contentDescription = null
                                    )
                                },
                                onChangeValue = onChangeMemo
                            )
                            Spacer(modifier = Modifier.size(15.dp))
                        }
                        if (uiState.photos.isNotEmpty()) {
                            item {
                                LazyRow {
                                    items(uiState.photos) { photo ->
                                        CustomImage(
                                            photo = photo,
                                            onClickPhotoDetail = onClickPhotoDetail,
                                            onClickDeletePhoto = onClickDeletePhoto,
                                        )
                                        Spacer(modifier = Modifier.size(5.dp))
                                    }
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(background)
                            .shadow(1.dp, clip = true),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Spacer(modifier = Modifier.weight(1F))
                            CustomButton(onClickSave, R.string.save, backgroundColor = theme)
                            Spacer(modifier = Modifier.size(20.dp))
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.clickable { onClickTakePhoto() }
                            )
                        }
                    }
                }
            }

            else -> {
            }
        }
    }
}

@Composable
private fun CustomTextField(
    @StringRes labelTextResourceId: Int,
    value: String,
    leadingIcon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    TextField(
        label = {
            Text(text = stringResource(id = labelTextResourceId), color = Color.Black)
        },
        value = value,
        onValueChange = {},
        leadingIcon = {
            leadingIcon()
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            disabledTextColor = Color.Black,
            disabledLabelColor = Color.Black,
            disabledIndicatorColor = Color.Black,
            disabledLeadingIconColor = Color.Black
        ),
        readOnly = true,
        enabled = false,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    )
}

@Composable
private fun CustomMultiTextField(
    @StringRes labelTextResourceId: Int,
    value: String,
    leadingIcon: @Composable () -> Unit,
    onChangeValue: (String) -> Unit,
) {
    TextField(
        label = {
            Text(text = stringResource(id = labelTextResourceId), color = Color.Black)
        },
        value = value,
        onValueChange = onChangeValue,
        leadingIcon = {
            leadingIcon()
        },
        maxLines = Integer.MAX_VALUE,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            textColor = Color.Black,
            focusedLabelColor = Color.Black,
            unfocusedIndicatorColor = Color.Black,
            focusedIndicatorColor = Color.Gray,
            leadingIconColor = Color.Black
        ),
        placeholder = {
            Text(text = stringResource(R.string.hint_memo_placeholder))
        },
        enabled = true,
        modifier = Modifier
            .fillMaxWidth()
    )
}
