package com.app.body_manage.ui.measure.list

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.body_manage.R
import com.app.body_manage.common.Calendar
import com.app.body_manage.common.CustomButton
import com.app.body_manage.common.CustomImage
import com.app.body_manage.common.toKcal
import com.app.body_manage.data.dao.BodyMeasurePhotoDao
import com.app.body_manage.data.local.UserPreference
import com.app.body_manage.data.model.BodyMeasure
import com.app.body_manage.data.model.Meal
import com.app.body_manage.data.model.Measure
import com.app.body_manage.data.model.Photo
import com.app.body_manage.data.model.Training
import com.app.body_manage.domain.BMICalculator
import com.app.body_manage.extension.toFat
import com.app.body_manage.extension.toJapaneseTime
import com.app.body_manage.extension.toMMDDEE
import com.app.body_manage.extension.toWeight
import com.app.body_manage.style.Colors.Companion.background
import com.app.body_manage.style.Colors.Companion.theme
import com.app.body_manage.ui.top.HorizontalLine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MeasureListScreen(
    uiState: MeasureListState.BodyMeasureListState,
    userPreference: UserPreference?,
    resetSnackBarMessage: () -> Unit,
    setLocalDate: (LocalDate) -> Unit,
    clickBodyMeasureEdit: (LocalDateTime) -> Unit,
    onClickAddMeasure: () -> Unit,
    onClickAddMeal: () -> Unit,
    updateDate: (Int) -> Unit,
    showPhotoDetail: (Int) -> Unit,
    onChangeCurrentMonth: (YearMonth) -> Unit,
    onClickBack: () -> Unit,
    onClickMeal: (Meal) -> Unit,
    onClickAddTraining: () -> Unit,
    onClickTraining: (Training) -> Unit,
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val state = rememberScaffoldState()

    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val showCalendar = rememberSaveable { mutableStateOf(false) }
    val showPhotoList = rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BottomButtons(
                    userPreference,
                    onClickAddMeasure,
                    onClickAddMeal,
                    onClickAddTraining,
                )
            }
        },
        scaffoldState = state,
        topBar = {
            TopAppBar(
                elevation = 0.dp,
                modifier = Modifier
                    .background(colorResource(id = R.color.app_theme))
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                    ),
                backgroundColor = theme
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.size(10.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable { onClickBack() },
                        tint = Color.Black
                    )
                    Text(
                        text = uiState.date.toMMDDEE(),
                        modifier = Modifier
                            .offset(x = 10.dp)
                            .clickable {
                                scope.launch {
                                    showCalendar.value = true
                                    showPhotoList.value = false
                                    sheetState.show()
                                }
                            },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.size(40.dp))
                    CustomButton(
                        onClick = { updateDate.invoke(-1) },
                        valueResourceId = R.string.prev_day
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    CustomButton(
                        onClick = { updateDate.invoke(1) },
                        valueResourceId = R.string.next_day
                    )
                }
            }
        },
        content = { padding ->
            ModalBottomSheetLayout(
                sheetShape = RoundedCornerShape(15.dp),
                sheetState = sheetState,
                sheetContent = {
                    Column(
                        modifier = Modifier
                            .heightIn(min = 500.dp)
                    ) {
                        if (showCalendar.value) {
                            Calendar(
                                selectedDate = uiState.date,
                                markDayList = uiState.currentMonthRegisteredDayList,
                                onClickDate = {
                                    scope.launch {
                                        setLocalDate.invoke(it)
                                        sheetState.hide()
                                    }
                                },
                                onChangeCurrentMonth = onChangeCurrentMonth,
                            )
                        }
                        if (showPhotoList.value) {
                            Box(modifier = Modifier.padding(top = 10.dp)) {
                                PhotoList(uiState.photoList, clickPhoto = showPhotoDetail)
                            }
                        }
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxHeight()
                        .background(background)
                        .padding(top = 10.dp)
                ) {
                    if (uiState.message.isNotEmpty()) {
                        LaunchedEffect(uiState.message) {
                            coroutineScope.launch {
                                state.snackbarHostState.showSnackbar(
                                    message = uiState.message,
                                    duration = SnackbarDuration.Short
                                )
                                resetSnackBarMessage.invoke()
                            }
                        }
                    }
                    if (uiState.list.isNotEmpty()) {
                        if (
                            uiState.list.filterIsInstance<Meal>().isNotEmpty() &&
                            userPreference?.optionFeature?.meal == true
                        ) {
                            Summary(
                                list = uiState.list,
                                userPreference = userPreference
                            )
                        }
                        MeasureList(
                            userPreference = userPreference,
                            list = uiState.list,
                            clickBodyMeasureEdit = clickBodyMeasureEdit,
                            onClickMeal = onClickMeal,
                            onClickTraining = onClickTraining,
                        )
                    }
                    if (uiState.list.isEmpty()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = stringResource(id = R.string.message_empty_so_add),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun BottomButtons(
    userPreference: UserPreference?,
    onClickAddMeasure: () -> Unit,
    onClickAddMeal: () -> Unit,
    onClickAddTraining: () -> Unit,
) {
    Row(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        CustomButton(
            onClick = onClickAddMeasure,
            valueResourceId = R.string.label_add_measure,
            backgroundColor = theme,
            modifier = Modifier,
        )
        if (userPreference?.optionFeature?.meal == true) {
            CustomButton(
                onClick = onClickAddMeal,
                valueResourceId = R.string.label_add_meal,
                backgroundColor = theme,
                modifier = Modifier,
            )
        }
        if (userPreference?.optionFeature?.training == true) {
            CustomButton(
                onClick = onClickAddTraining,
                valueResourceId = R.string.label_add_training,
                backgroundColor = theme,
                modifier = Modifier,
            )
        }
    }
}

@Composable
private fun Summary(
    list: List<Measure>,
    userPreference: UserPreference
) {
    Column(
        Modifier
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .fillMaxWidth(),
    ) {
        val totalKcal = list.filterIsInstance<Meal>().sumOf { it.totalKcal }
        Text(
            text = stringResource(id = R.string.label_total_kcal_per_day) + "：$totalKcal / ${userPreference.goalKcal} kcal",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.size(10.dp))
        LinearProgressIndicator(
            progress = userPreference.progressKcal(totalKcal),
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = Color(0xFF00CC00),
            backgroundColor = Color.LightGray,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun PhotoList(
    photoList: List<BodyMeasurePhotoDao.PhotoData>,
    clickPhoto: (Int) -> Unit
) {
    Column {
        if (photoList.isEmpty()) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.message_empty_photo),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
            }
        } else {
            Text(
                text = stringResource(id = R.string.label_this_day_photos),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp, bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(0.9F)
                ) {
                    items(photoList) {
                        AsyncImage(
                            model = it.photoUri,
                            contentDescription = null,
                            contentScale = ContentScale.Inside,
                            modifier = Modifier
                                .padding(3.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .clickable {
                                    clickPhoto.invoke(it.photoId)
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MeasureList(
    list: List<Measure>,
    clickBodyMeasureEdit: (LocalDateTime) -> Unit,
    onClickMeal: (Meal) -> Unit,
    onClickTraining: (Training) -> Unit,
    userPreference: UserPreference?,
) {
    LazyColumn(
        modifier = Modifier
            .wrapContentWidth()
            .padding(horizontal = 12.dp),
        content = {
            items(list) { item ->
                if (
                    (item is Meal && userPreference?.optionFeature?.meal == false) ||
                    (item is Training && userPreference?.optionFeature?.training == false)
                ) {
                    return@items
                }
                ResultItem(
                    time = item.time,
                    onClick = {
                        when (item) {
                            is BodyMeasure -> clickBodyMeasureEdit(item.time)
                            is Meal -> onClickMeal(item)
                            is Training -> onClickTraining(item)
                        }
                    }
                ) {
                    when (item) {
                        is BodyMeasure -> {
                            BodyItem(item)
                        }

                        is Meal -> {
                            MealItem(item)
                        }

                        is Training -> {
                            TrainingItem(item)
                        }
                    }
                }
                Spacer(modifier = Modifier.size(15.dp))
            }
        },
    )
}

@Composable
private fun BodyItem(
    measure: BodyMeasure,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column {
            LabelAndText(
                label = stringResource(id = R.string.weight),
                text = measure.weight.toWeight(),
            )
            LabelAndText(
                label = stringResource(id = R.string.label_fat),
                text = measure.fat.toFat(),
            )
            LabelAndText(
                label = stringResource(id = R.string.label_bmi),
                text = BMICalculator().calculate(measure.tall, measure.weight),
            )
            if (measure.photoUri != null) {
                Spacer(modifier = Modifier.size(10.dp))
                CustomImage(
                    photo = object : Photo {
                        override val id: Photo.Id = Photo.Id(0)
                        override val uri: Uri = measure.photoUri
                    },
                    onClickPhotoDetail = {},
                    onClickDeletePhoto = {},
                    deleteTable = false,
                    size = 80.dp,
                )
            }
        }
    }
}

@Composable
private fun TrainingItem(
    training: Training,
) {
    Column {
        Text(text = stringResource(id = R.string.label_training))
        training.menus.forEachIndexed { index, trainingMenu ->
            Row {
                Text(
                    text = stringResource(id = R.string.label_event, trainingMenu.eventIndex + 1)
                )
                Text(
                    text = trainingMenu.name
                )
            }
            if (index == training.menus.lastIndex && training.memo.isNotEmpty()) {
                HorizontalLine(verticalPadding = 7.dp)
            }
        }
        if (training.memo.isNotEmpty()) {
            Text(text = stringResource(id = R.string.label_memo_area))
            Text(text = training.memo)
        }
    }
}

@Composable
private fun MealItem(
    meal: Meal,
) {
    Column {
        Text(text = stringResource(meal.timing.textResourceId))
        LabelAndText(
            label = stringResource(id = R.string.label_total_kcal),
            text = meal.totalKcal.toKcal()
        )
        Row {
            Text(
                text = stringResource(id = R.string.label_meals),
                modifier = Modifier.width(100.dp)
            )
        }
        Row {
            Spacer(modifier = Modifier.size(10.dp))
            Column {
                meal.foods.forEach {
                    Text(text = it.nameWithKcal)
                }
            }
        }
        if (meal.photos.isNotEmpty()) {
            Spacer(modifier = Modifier.size(10.dp))
        }
        Row {
            meal.photos.forEach { photo ->
                CustomImage(
                    photo = photo,
                    size = 80.dp,
                    onClickPhotoDetail = {},
                    onClickDeletePhoto = {},
                    deleteTable = false
                )
            }
        }
    }
}

@Composable
fun ResultItem(
    time: LocalDateTime,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Row(
        Modifier
            .clickable { onClick() }
            .shadow(2.dp, RoundedCornerShape(5.dp))
            .border(0.5.dp, Color.DarkGray, RoundedCornerShape(5.dp))
            .background(Color.White, RoundedCornerShape(5.dp))
            .fillMaxWidth()
            .padding(start = 12.dp)
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = time.toJapaneseTime(),
        )
        Spacer(modifier = Modifier.size(15.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            content()
            Spacer(modifier = Modifier.weight(1F))
            Icon(
                Icons.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(10.dp),
                tint = Color.DarkGray,
            )
            Spacer(modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun LabelAndText(
    label: String,
    text: String,
    labelWidth: Dp = 100.dp
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            modifier = Modifier.width(labelWidth)
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = text)
    }
}
