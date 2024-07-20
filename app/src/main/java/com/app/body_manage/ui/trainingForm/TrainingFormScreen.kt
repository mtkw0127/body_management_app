package com.app.body_manage.ui.trainingForm

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.R
import com.app.body_manage.common.CustomButton
import com.app.body_manage.data.model.Training
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.data.model.createSampleOwnWeightTrainingMenu
import com.app.body_manage.data.model.createSampleTrainingMenu
import com.app.body_manage.extension.toJapaneseTime
import com.app.body_manage.extension.toMMDDEE
import com.app.body_manage.style.Colors.Companion.background
import com.app.body_manage.style.Colors.Companion.theme
import com.app.body_manage.ui.top.PanelColumn
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun TrainingFormScreen(
    training: Training?,
    @StringRes registerTextResourceId: Int,
    updateMemo: (String) -> Unit = {},
    onClickTrainingDelete: (() -> Unit)?,
    onClickDeleteMenu: (eventIndex: Long) -> Unit = {},
    onClickBackPress: () -> Unit = {},
    onClickRegister: () -> Unit = {},
    onClickFab: () -> Unit = {},
    onClickRep: (menuIndex: Int, setIndex: Int) -> Unit = { _, _ -> },
    onClickWeight: (menuIndex: Int, setIndex: Int) -> Unit = { _, _ -> },
    onClickDelete: (menuIndex: Int, setIndex: Int) -> Unit = { _, _ -> },
    onClickStartTime: () -> Unit = {},
    onClickEndTime: () -> Unit = {},
    onClickCardioMinutes: (menuIndex: Int, setIndex: Int) -> Unit = { _, _ -> },
    onClickCardioDistance: (menuIndex: Int, setIndex: Int) -> Unit = { _, _ -> },
) {
    Scaffold(
        modifier = Modifier.background(theme).windowInsetsPadding(
            WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)
        ),
        topBar = {
            TopAppBar(
                elevation = 0.dp,
                backgroundColor = theme,
                modifier = Modifier
                    .background(colorResource(id = R.color.app_theme))
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                    ),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.size(10.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable { onClickBackPress() },
                        tint = Color.Black
                    )
                    if (training == null) return@TopAppBar
                    Text(
                        text = training.date.toMMDDEE(),
                        modifier = Modifier.offset(x = 10.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    onClickTrainingDelete?.let {
                        CustomButton(
                            onClick = onClickTrainingDelete,
                            valueResourceId = R.string.delete,
                        )
                    }
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .background(theme)
                    .height(50.dp)
                    .fillMaxWidth()
                    .clickable {
                        onClickRegister()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(text = stringResource(id = registerTextResourceId))
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onClickFab()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        },
    ) { padding ->
        if (training == null) return@Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(padding)
                .padding(horizontal = 5.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.size(5.dp))

            PanelColumn {
                Row {
                    Text(text = stringResource(id = R.string.label_start_training_time))
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = training.startTime.toJapaneseTime(),
                        modifier = Modifier
                            .drawBehind {
                                drawLine(
                                    color = Color.Black,
                                    strokeWidth = 1.dp.toPx(),
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                )
                            }
                            .clickable { onClickStartTime() }
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(text = stringResource(id = R.string.label_end_training_time))
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = training.endTime.toJapaneseTime(),
                        modifier = Modifier
                            .drawBehind {
                                drawLine(
                                    color = Color.Black,
                                    strokeWidth = 1.dp.toPx(),
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                )
                            }
                            .clickable { onClickEndTime() }
                    )
                }
            }

            Spacer(modifier = Modifier.size(5.dp))

            PanelColumn {
                Text(text = stringResource(id = R.string.label_memo_area_training))
                MemoTextField(
                    text = training.memo,
                    updateMemo = updateMemo,
                )
            }

            Spacer(modifier = Modifier.size(5.dp))

            // #1
            val setWeight = 0.1F

            // 筋トレ用
            val repWeight = 0.4F
            val weightWeight = 0.4F

            // 有酸素用
            val minuteWeight = 0.4F
            val distanceWeight = 0.4F

            // 削除ボタン
            val deleteWeight = 0.1F

            training.menus.forEachIndexed { menuIndex, menu ->
                TrainingPanel {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(id = R.string.label_event, menu.eventIndex + 1),
                            modifier = Modifier
                                .background(Color.Black, RoundedCornerShape(5.dp))
                                .padding(5.dp),
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(text = menu.name)
                        Spacer(modifier = Modifier.weight(1F))
                        CustomButton(
                            onClick = { onClickDeleteMenu(menu.eventIndex) },
                            valueResourceId = R.string.delete,
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        // ヘッダー
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "",
                                modifier = Modifier.weight(setWeight),
                            )
                            if (menu.type == TrainingMenu.Type.MACHINE || menu.type == TrainingMenu.Type.FREE) {
                                // レップ数用
                                Text(
                                    text = stringResource(id = R.string.label_rep_num),
                                    modifier = Modifier.weight(repWeight),
                                    textAlign = TextAlign.Center,
                                )
                            }
                            if (menu.type == TrainingMenu.Type.Cardio) {
                                Text(
                                    text = stringResource(id = R.string.label_minutes),
                                    modifier = Modifier.weight(minuteWeight),
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    text = stringResource(id = R.string.label_distance),
                                    modifier = Modifier.weight(distanceWeight),
                                    textAlign = TextAlign.Center,
                                )
                            }
                            if (menu.type == TrainingMenu.Type.MACHINE || menu.type == TrainingMenu.Type.FREE) {
                                Text(
                                    text = stringResource(id = R.string.label_weight),
                                    modifier = Modifier.weight(weightWeight),
                                    textAlign = TextAlign.Center,
                                )
                            }
                            // 削除アイコン用
                            Spacer(modifier = Modifier.weight(deleteWeight))
                        }
                        // 内容
                        menu.sets.forEachIndexed { setIndex, set ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(
                                        id = R.string.label_set,
                                        setIndex + 1
                                    ),
                                    modifier = Modifier.weight(setWeight),
                                )
                                if (set is TrainingMenu.CardioSet) {
                                    CardioSet(
                                        set = set,
                                        minuteWeight = minuteWeight,
                                        distanceWeight = distanceWeight,
                                        onClickCardioMinutes = {
                                            onClickCardioMinutes(menuIndex, setIndex)
                                        },
                                        onClickCardioDistance = {
                                            onClickCardioDistance(menuIndex, setIndex)
                                        },
                                    )
                                }
                                if (set is TrainingMenu.Set) {
                                    MuscleSet(
                                        set = set,
                                        menuIndex = menuIndex,
                                        setIndex = setIndex,
                                        weightWeight = weightWeight,
                                        repWeight = repWeight,
                                        onClickRep = onClickRep,
                                        onClickWeight = onClickWeight,
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .weight(deleteWeight)
                                        .clickable { onClickDelete(menuIndex, setIndex) },
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
        }
    }
}

@Composable
private fun RowScope.CardioSet(
    set: TrainingMenu.CardioSet,
    distanceWeight: Float,
    minuteWeight: Float,
    onClickCardioMinutes: () -> Unit,
    onClickCardioDistance: () -> Unit,
) {
    CountTextField(
        modifier = Modifier.weight(minuteWeight),
        count = set.minutes,
        unitStringResource = R.string.label_minute_unit,
        onClick = {
            onClickCardioMinutes()
        },
    )
    CountTextField(
        modifier = Modifier.weight(distanceWeight),
        count = set.distance,
        unitStringResource = R.string.label_distance_unit,
        onClick = {
            onClickCardioDistance()
        },
    )
}

@Composable
private fun RowScope.MuscleSet(
    set: TrainingMenu.Set,
    menuIndex: Int, // 何番目の種目
    setIndex: Int, // 何番目のセット
    onClickRep: (menuIndex: Int, setIndex: Int) -> Unit,
    onClickWeight: (menuIndex: Int, setIndex: Int) -> Unit,
    weightWeight: Float,
    repWeight: Float,
) {
    CountTextField(
        modifier = Modifier.weight(repWeight),
        count = set.number,
        unitStringResource = R.string.label_count,
        onClick = {
            onClickRep(menuIndex, setIndex)
        },
    )
    // 重りを扱う種目の場合は重量を入力する
    if (set is TrainingMenu.WeightSet) {
        CountTextField(
            modifier = Modifier.weight(weightWeight),
            count = set.weight,
            unitStringResource = R.string.label_weight_unit,
            onClick = {
                onClickWeight(menuIndex, setIndex)
            },
        )
    } else {
        // 幅調整のために空白を入れる
        Spacer(modifier = Modifier.weight(weightWeight))
    }
}

@Composable
private fun TrainingPanel(
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .shadow(1.dp, RoundedCornerShape(15.dp))
            .background(
                Color.White,
                shape = RoundedCornerShape(15.dp)
            )
            .padding(15.dp)
    ) {
        content()
    }
}

@Composable
private fun MemoTextField(
    text: String,
    updateMemo: (String) -> Unit,
) {
    Column {
        Spacer(modifier = Modifier.size(5.dp))
        BasicTextField(
            value = text,
            onValueChange = {
                updateMemo(it)
            },
            maxLines = 10,
            modifier = Modifier
                .heightIn(min = 80.dp)
                .fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.LightGray, RoundedCornerShape(1.dp))
                        .padding(5.dp)
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.label_memo_area),
                            color = Color.LightGray
                        )
                    } else {
                        innerTextField()
                    }
                }
            }
        )
    }
}

@Composable
private fun CountTextField(
    modifier: Modifier = Modifier,
    count: Number,
    unitStringResource: Int,
    onClick: () -> Unit,
) {
    val unit = stringResource(id = unitStringResource)
    val offset = 5F
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "$count$unit",
            modifier = modifier
                .drawBehind {
                    drawLine(
                        color = Color.Black,
                        strokeWidth = 1.dp.toPx(),
                        start = Offset(0f - offset, size.height),
                        end = Offset(size.width + offset, size.height),
                    )
                }
                .clickable { onClick() },
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
@Preview
private fun TrainingFormPreview() {
    TrainingFormScreen(
        training = Training(
            id = Training.NEW_ID,
            date = LocalDate.now(),
            time = LocalDateTime.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now(),
            menus = listOf(
                createSampleTrainingMenu(0),
                createSampleTrainingMenu(1),
                createSampleTrainingMenu(2),
                createSampleTrainingMenu(3),
                createSampleTrainingMenu(4),
                createSampleOwnWeightTrainingMenu(5),
                createSampleOwnWeightTrainingMenu(6),
            ),
            memo = "たくさん頑張った".repeat(5),
            createdAt = LocalDate.now(),
        ),
        registerTextResourceId = R.string.label_register_training,
        onClickBackPress = {},
        onClickTrainingDelete = null,
    )
}
