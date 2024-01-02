package com.app.body_manage.ui.measure.form

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.body_manage.R
import com.app.body_manage.data.model.PhotoModel
import com.app.body_manage.extension.toFat
import com.app.body_manage.extension.toJapaneseTime
import com.app.body_manage.extension.toWeight
import com.app.body_manage.style.Colors.Companion.theme
import com.app.body_manage.util.DateUtil

@Composable
fun BodyMeasureFormScreen(
    uiState: FormState,
    onClickBackPress: () -> Unit = {},
    onClickTakePhoto: () -> Unit = {},
    onClickDelete: () -> Unit = {},
    onClickSave: () -> Unit = {},
    onClickNextDay: () -> Unit = {},
    onClickPreviousDay: () -> Unit = {},
    onClickPhotoDetail: (PhotoModel) -> Unit = {},
    onClickDeletePhoto: (PhotoModel) -> Unit = {},
    onClickTime: () -> Unit = {},
    onChangeWeightDialog: () -> Unit = {},
    onChangeFatDialog: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (uiState is FormState.HasData) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val isAdd = uiState is FormState.HasData.Add
                            val isEdit = uiState is FormState.HasData.Edit
                            if (isAdd) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBackIosNew,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.clickable { onClickPreviousDay() }
                                )
                                Spacer(modifier = Modifier.size(10.dp))
                            }
                            Text(
                                text = DateUtil.localDateConvertJapaneseFormatYearMonthDay(
                                    uiState.measureDate
                                )
                            )
                            if (isAdd) {
                                Spacer(modifier = Modifier.size(10.dp))
                                Icon(
                                    imageVector = Icons.Filled.ArrowForwardIos,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.clickable { onClickNextDay() }
                                )
                            }
                            if (isEdit) {
                                Spacer(modifier = Modifier.weight(1F))
                                Icon(
                                    imageVector = Icons.Filled.DeleteForever,
                                    tint = Color.Black,
                                    contentDescription = null,
                                    modifier = Modifier.clickable { onClickDelete() }
                                )
                                Spacer(modifier = Modifier.size(10.dp))
                            }
                        }
                    }
                },
                backgroundColor = colorResource(id = R.color.app_theme)
            )
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
                                value = uiState.model.capturedLocalDateTime.toJapaneseTime(),
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
                        if (uiState.photos.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .border(1.dp, Color.Gray, RoundedCornerShape(5.dp))
                                        .background(Color.Transparent, RoundedCornerShape(5.dp))
                                        .fillMaxWidth()
                                        .height(300.dp)
                                        .clickable {
                                            onClickTakePhoto()
                                        },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Filled.AddCircleOutline,
                                            contentDescription = null,
                                            tint = Color.Gray,
                                        )
                                        Spacer(modifier = Modifier.size(10.dp))
                                        Text(
                                            text = stringResource(id = R.string.message_tap_and_add_photo),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Gray,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                }
                            }
                        } else {
                            items(uiState.photos) { photo ->
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    AsyncImage(
                                        model = photo.uri,
                                        contentDescription = null,
                                        modifier = Modifier.clickable {
                                            onClickPhotoDetail(photo)
                                        }
                                    )
                                    IconButton(
                                        onClick = { onClickDeletePhoto(photo) },
                                        modifier = Modifier.offset(x = 5.dp, y = 5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Divider(
                        modifier = Modifier
                            .background(Color.Black)
                            .fillMaxWidth()
                            .height(1.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            CustomButton(onClickBackPress, R.string.back)
                            Spacer(modifier = Modifier.weight(1F))
                            CustomButton(onClickSave, R.string.save, theme)
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
private fun CustomButton(
    onClick: () -> Unit,
    @StringRes valueResourceId: Int,
    backgroundColor: Color = Color.White,
) {
    Box(
        modifier = Modifier
            .width(60.dp)
            .height(35.dp)
            .background(backgroundColor, RoundedCornerShape(10.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(id = valueResourceId),
            textAlign = TextAlign.Center,
        )
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
