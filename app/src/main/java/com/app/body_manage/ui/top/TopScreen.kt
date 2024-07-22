package com.app.body_manage.ui.top

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.app.body_manage.BuildConfig
import com.app.body_manage.R
import com.app.body_manage.common.BottomSheet
import com.app.body_manage.common.BottomSheetData
import com.app.body_manage.common.CustomButton
import com.app.body_manage.data.local.UserPreference
import com.app.body_manage.data.model.BodyMeasure
import com.app.body_manage.data.model.Meal
import com.app.body_manage.extension.age
import com.app.body_manage.extension.toCentiMeter
import com.app.body_manage.extension.toMMDDEE
import com.app.body_manage.extension.toWeight
import com.app.body_manage.extension.withPercent
import com.app.body_manage.style.Colors.Companion.background
import com.app.body_manage.style.Colors.Companion.theme
import com.app.body_manage.ui.common.ColumTextWithLabelAndIcon
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize.FULL_BANNER
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun TopScreen(
    userPreference: UserPreference?,
    lastMeasure: BodyMeasure?,
    initialMeasure: BodyMeasure?,
    todayMeasure: TodayMeasure,
    bottomSheetDataList: List<BottomSheetData>,
    onClickSeeTrainingMenu: () -> Unit = {},
    onClickCompare: () -> Unit = {},
    onClickPhotos: () -> Unit = {},
    onClickToday: () -> Unit = {},
    onClickAddMeasure: () -> Unit = {},
    onClickSetGoal: () -> Unit = {},
    onClickSetting: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onClickAddMeasure) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(colorResource(id = R.color.app_theme))
                    .navigationBarsPadding()
            ) {
                AndroidView(factory = { context ->
                    val adView = AdView(context).apply {
                        adUnitId = if (BuildConfig.DEBUG) {
                            "ca-app-pub-3940256099942544/9214589741"
                        } else {
                            "ca-app-pub-2002859886618281/9421408761"
                        }
                        setAdSize(FULL_BANNER)
                    }
                    scope.launch {
                        AdRequest.Builder().build().let { adView.loadAd(it) }
                    }
                    adView
                })
                BottomSheet(bottomSheetDataList)
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 10.dp)
                .padding(top = 10.dp)
                .background(background)
                .safeDrawingPadding()
                .fillMaxHeight()
        ) {
            item {
                Row(verticalAlignment = Alignment.Bottom) {
                    if (lastMeasure?.weight != null) {
                        Text(
                            text = lastMeasure.weight.toString(),
                            fontSize = 32.sp,
                            color = Color.Black,
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = stringResource(id = R.string.unit_kg),
                            fontSize = 18.sp,
                            color = Color.Gray,
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    lastMeasure?.time?.toLocalDate()?.toMMDDEE()?.let { mmdd ->
                        val label = stringResource(id = R.string.label_registered_date)
                        Text(
                            text = "$label: $mmdd",
                            fontSize = 14.sp,
                            color = Color.Gray,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    (lastMeasure?.tall)?.let { tall ->
                        Text(
                            text = tall.toCentiMeter(),
                            fontSize = 14.sp,
                            color = Color.Gray,
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            onClickSetting()
                        }
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
            if (userPreference?.goalWeight == null && lastMeasure != null) {
                item {
                    RequireGoal(onClickSetGoal)
                    Spacer(modifier = Modifier.size(10.dp))
                }
            } else if (lastMeasure != null && userPreference != null) {
                item {
                    Goal(
                        initialMeasure = initialMeasure,
                        lastMeasure = lastMeasure,
                        userPreference = userPreference,
                        meal = todayMeasure.meals,
                        onClickSetGoal
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
            if (lastMeasure != null && userPreference != null) {
                item {
                    Statistics(
                        bodyMeasure = lastMeasure,
                        userPreference = userPreference,
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
            item {
                PanelColumn(modifier = Modifier) {
                    IconAndText(
                        icon = Icons.Default.Today,
                        modifier = Modifier.padding(vertical = 5.dp),
                        onClick = { onClickToday() },
                        text = stringResource(id = R.string.label_see_by_today),
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
            item {
                PanelColumn {
                    IconAndText(
                        icon = Icons.Default.Compare,
                        modifier = Modifier.padding(vertical = 5.dp),
                        onClick = { onClickCompare() },
                        text = stringResource(id = R.string.label_compare),
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
            item {
                PanelColumn {
                    IconAndText(
                        icon = Icons.Default.Photo,
                        modifier = Modifier.padding(vertical = 5.dp),
                        onClick = { onClickPhotos() },
                        text = stringResource(id = R.string.label_photos),
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
            if (userPreference?.optionFeature?.training == true) {
                item {
                    PanelColumn {
                        IconAndText(
                            icon = Icons.Default.EmojiPeople,
                            modifier = Modifier.padding(vertical = 5.dp),
                            onClick = { onClickSeeTrainingMenu() },
                            text = stringResource(id = R.string.label_see_by_training_menu),
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        }
    }
}

@Composable
private fun Goal(
    initialMeasure: BodyMeasure?,
    lastMeasure: BodyMeasure,
    userPreference: UserPreference,
    meal: List<Meal>,
    onClickSetGoat: () -> Unit,
) {
    val startWeight = userPreference.startWeight ?: initialMeasure?.weight
    val goalWeight = checkNotNull(userPreference.goalWeight)
    PanelColumn(
        horizontalAlignment = Alignment.Start,
    ) {
        Text(text = stringResource(id = R.string.label_object_weight))
        Spacer(modifier = Modifier.size(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, Color.Black),
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .border(0.5.dp, Color.Black),
                        text = stringResource(id = R.string.label_start_weight),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .border(0.5.dp, Color.Black),
                        text = stringResource(id = R.string.current_weight),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .border(0.5.dp, Color.Black),
                        text = stringResource(id = R.string.label_target_weight),
                        textAlign = TextAlign.Center,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, Color.Black),
                ) {
                    val displayStartWeight = if (startWeight != null) {
                        "$startWeight kg"
                    } else {
                        "未設定"
                    }
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .border(0.5.dp, Color.Black),
                        text = displayStartWeight,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .border(0.5.dp, Color.Black),
                        text = "${lastMeasure.weight} kg",
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .border(0.5.dp, Color.Black),
                        text = "$goalWeight kg",
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        startWeight?.let {
            Spacer(modifier = Modifier.size(10.dp))
            Diff(
                label = stringResource(id = R.string.label_from_start),
                standard = lastMeasure.weight,
                current = it,
                isFromStart = true,
            )
            Spacer(modifier = Modifier.size(10.dp))
            Diff(
                label = stringResource(id = R.string.label_until_object),
                standard = goalWeight,
                current = lastMeasure.weight,
                isFromStart = false,
            )
        }

        if (userPreference.optionFeature.meal && userPreference.goalKcal != null) {
            Spacer(modifier = Modifier.size(10.dp))
            HorizontalLine()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.label_target_kcal) + " ${userPreference.goalKcal} kcal"
                )
                Spacer(Modifier.weight(1F))
                Text(text = userPreference.progressKcalText(meal.sumOf { it.totalKcal }))
            }
            Spacer(modifier = Modifier.size(10.dp))
            LinearProgressIndicator(
                progress = userPreference.progressKcal(meal.sumOf { it.totalKcal }),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.size(15.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            CustomButton(
                modifier = Modifier.height(35.dp),
                onClick = { onClickSetGoat() },
                valueResourceId = R.string.label_update_object,
                backgroundColor = theme
            )
        }
    }
}

@Composable
fun Dp.toPx(): Float {
    val metrics = LocalContext.current.resources.displayMetrics
    return this.value * (metrics.densityDpi / 160f)
}

@Composable
private fun Diff(
    label: String,
    standard: Float,
    current: Float,
    isFromStart: Boolean,
) {
    val diff = ((standard - current) * 100).toInt() / 100F
    val color = if (diff.toInt() == 0) {
        Color.Black
    } else if (diff > 0) {
        Color.Red
    } else {
        Color.Blue
    }

    val plusMinus = if (diff.toInt() == 0) {
        "±"
    } else if (diff > 0) {
        "+"
    } else {
        "-"
    }
    val yOffsetDp = 2.dp
    val yOffsetPx = yOffsetDp.toPx()

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start,
    ) {
        Text(
            text = label,
            modifier = Modifier
                .offset(y = yOffsetDp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = plusMinus,
            color = color,
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = "${diff.absoluteValue} kg",
            color = color,
            fontSize = 16.sp,
            modifier = Modifier.drawBehind {
                drawLine(
                    color = color,
                    start = Offset(-30F, size.height + yOffsetPx),
                    end = Offset(size.width + 10F, size.height + yOffsetPx),
                    strokeWidth = 2F
                )
            }
        )
        if (isFromStart) {
            Spacer(modifier = Modifier.size(10.dp))
            val text = if (diff.toInt() == 0) {
                ""
            } else if (diff > 0) {
                "増加"
            } else {
                "減少"
            }
            Text(
                text = text,
                color = color,
                fontSize = 12.sp,
                modifier = Modifier.offset(y = yOffsetDp),
            )
        }
    }
}

@Composable
private fun RequireGoal(
    onClickSetGoat: () -> Unit
) {
    Box(
        modifier = Modifier
            .shadow(2.dp)
            .background(
                Color.White,
                RoundedCornerShape(5.dp)
            )
            .fillMaxWidth()
            .height(120.dp)
            .padding(5.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
        ) {
            TextWithUnderLine(R.string.label_set_object)
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = stringResource(id = R.string.message_set_object),
                fontSize = 12.sp,
            )
        }
        CustomButton(
            modifier = Modifier.height(35.dp),
            onClick = { onClickSetGoat() },
            valueResourceId = R.string.label_set_object,
            backgroundColor = theme
        )
    }
}

@Composable
fun Statistics(
    bodyMeasure: BodyMeasure,
    userPreference: UserPreference,
) {
    Panel(content = {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextWithUnderLine(stringResource(id = R.string.label_current_you, userPreference.name))
            Spacer(modifier = Modifier.size(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                ColumTextWithLabelAndIcon(
                    title = stringResource(id = R.string.weight),
                    value = bodyMeasure.weight.toWeight(),
                )
                ColumTextWithLabelAndIcon(
                    title = stringResource(id = R.string.tall),
                    value = bodyMeasure.tall.toCentiMeter(),
                )
                ColumTextWithLabelAndIcon(
                    title = stringResource(id = R.string.age),
                    value = userPreference.birth.age().toString(),
                )
            }
            Spacer(modifier = Modifier.size(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                ColumTextWithLabelAndIcon(
                    title = stringResource(id = R.string.label_bmi),
                    value = userPreference.bim(bodyMeasure.tall, bodyMeasure.weight),
                )
                ColumTextWithLabelAndIcon(
                    title = stringResource(id = R.string.label_kcal) + "※",
                    value = userPreference.basicConsumeEnergy(bodyMeasure.tall, bodyMeasure.weight),
                )
                ColumTextWithLabelAndIcon(
                    title = stringResource(id = R.string.label_fat),
                    value = userPreference.calcFat(bodyMeasure.tall, bodyMeasure.weight)
                        .withPercent(),
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
}

@Composable
fun TextWithUnderLine(
    @StringRes stringResourceId: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(id = stringResourceId),
        modifier = modifier.drawBehind {
            drawLine(
                Color.Black,
                Offset(-10F, size.height),
                Offset(size.width + 10F, size.height),
                strokeWidth = 1F
            )
        },
        fontSize = 16.sp,
    )
}

@Composable
fun TextWithUnderLine(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .heightIn(min = 30.dp)
            .drawBehind {
                drawLine(
                    Color.Black,
                    Offset(-10F, size.height),
                    Offset(size.width + 10F, size.height),
                    strokeWidth = 1F
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            modifier = modifier,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun HorizontalLine(
    verticalPadding: Dp = 20.dp
) {
    Box(
        modifier = Modifier
            .padding(vertical = verticalPadding)
            .fillMaxWidth()
            .height(height = 1.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(1.dp))
    )
}

@Composable
fun IconAndText(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    withArrow: Boolean = true,
    message: String? = null,
    subTitle: String? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxSize()
            .clickable { onClick() },
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
        )
        Spacer(modifier = Modifier.size(10.dp))
        Column {
            Text(text = text)
            subTitle?.let {
                Text(
                    text = subTitle,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1F))
        if (withArrow) {
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.size(10.dp))
        }
        if (message != null) {
            Text(
                text = message,
            )
        }
    }
}

@Composable
fun PanelColumn(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(5.dp))
            .background(
                Color.White,
                RoundedCornerShape(5.dp)
            )
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = horizontalAlignment
    ) {
        content()
    }
}

@Composable
fun Panel(
    content: @Composable () -> Unit,
    bottom: @Composable () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .shadow(2.dp)
            .background(
                Color.White,
                RoundedCornerShape(5.dp)
            )
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
        bottom()
    }
}
