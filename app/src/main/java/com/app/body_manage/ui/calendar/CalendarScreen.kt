package com.app.body_manage.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.R
import com.app.body_manage.data.calendar.Day
import com.app.body_manage.data.calendar.Month
import com.app.body_manage.data.calendar.Week
import com.app.body_manage.style.Colors
import com.app.body_manage.util.DateUtil
import kotlinx.coroutines.delay
import java.time.LocalDate
import kotlin.math.absoluteValue

@Composable
fun CalendarScreen(
    months: List<Month>,
    focusedMonth: LocalDate,
    moveToPrev: () -> Unit,
    moveToNext: () -> Unit,
    onClickBackPress: () -> Unit = {},
    onClickDate: (Day) -> Unit = {},
) {
    val state = rememberLazyListState()
    var changingFocus by remember { mutableStateOf(false) }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = focusedMonth) {
        if (initialized) {
            val index = months.indexOfFirst { it.firstDay == focusedMonth }
            if (index >= 0) {
                state.animateScrollToItem(index)
            }
        }
    }

    LaunchedEffect(changingFocus) {
        if (changingFocus) {
            delay(200)
            changingFocus = false
        }
    }

    LaunchedEffect(months.isNotEmpty()) {
        if (initialized.not() && months.isNotEmpty()) {
            delay(200)
            state.animateScrollToItem(1)
            initialized = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = Colors.theme) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.size(14.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable { onClickBackPress() },
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    val text = if (LocalDate.now().year == focusedMonth.year) {
                        DateUtil.localDateConvertJapaneseFormatMonth(focusedMonth)
                    } else {
                        DateUtil.localDateConvertJapaneseFormatYearMonth(focusedMonth)
                    }
                    Text(
                        text = text,
                        fontSize = 18.sp,
                        color = Color.Black,
                    )
                }
            }
        }
    ) {
        LazyRow(
            state = state,
            modifier = Modifier.padding(it),
            userScrollEnabled = false,
        ) {
            items(months) { month ->
                Column(
                    modifier = Modifier.pointerInput(Unit) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            if (dragAmount.absoluteValue > 20 && changingFocus.not()) {
                                changingFocus = true
                                if (dragAmount > 0) {
                                    moveToPrev()
                                } else {
                                    moveToNext()
                                }
                            }
                        }
                    }
                ) {
                    DayView()
                    DateView(month, onClickDate)
                }
            }
        }
    }
}

@Composable
private fun DayView() {
    Row(
        modifier = Modifier
            .width(
                Dp(LocalConfiguration.current.screenWidthDp.toFloat())
            )
            .height(40.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DayCell(day = stringResource(id = R.string.sunday), modifier = Modifier.weight(1F))
        DayCell(day = stringResource(id = R.string.monday), modifier = Modifier.weight(1F))
        DayCell(day = stringResource(id = R.string.tuesday), modifier = Modifier.weight(1F))
        DayCell(day = stringResource(id = R.string.wednesday), modifier = Modifier.weight(1F))
        DayCell(day = stringResource(id = R.string.thursday), modifier = Modifier.weight(1F))
        DayCell(day = stringResource(id = R.string.friday), modifier = Modifier.weight(1F))
        DayCell(day = stringResource(id = R.string.saturday), modifier = Modifier.weight(1F))
    }
}

@Composable
fun DayCell(
    day: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .wrapContentHeight()
            .drawBehind {
                drawLine(
                    color = Color.LightGray,
                    start = Offset(size.width, size.height),
                    end = Offset(size.width, size.height + 100),
                    strokeWidth = 2.0f
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = day,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun DateView(
    month: Month,
    onClickDate: (Day) -> Unit
) {
    Column(
        modifier = Modifier
            .width(
                Dp(LocalConfiguration.current.screenWidthDp.toFloat())
            )
            .height(
                Dp(LocalConfiguration.current.screenHeightDp.toFloat())
            )
    ) {
        Week(month.firstWeek, onClickDate, Modifier.weight(1F))
        Week(month.secondWeek, onClickDate, Modifier.weight(1F))
        Week(month.thirdWeek, onClickDate, Modifier.weight(1F))
        Week(month.fourthWeek, onClickDate, Modifier.weight(1F))
        Week(month.fifthWeek, onClickDate, Modifier.weight(1F))
        Week(month.sixthWeek, onClickDate, Modifier.weight(1F))
    }
}

@Composable
private fun Week(
    week: Week,
    onClickDate: (Day) -> Unit,
    modifier: Modifier,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        DateCell(
            day = week.sunday,
            onClickDate,
            Modifier.weight(1F)
        )
        DateCell(
            day = week.monday,
            onClickDate,
            Modifier.weight(1F)
        )
        DateCell(
            day = week.tuesday,
            onClickDate,
            Modifier.weight(1F)
        )
        DateCell(
            day = week.wednesday,
            onClickDate,
            Modifier.weight(1F)
        )
        DateCell(
            day = week.thursday,
            onClickDate,
            Modifier.weight(1F)
        )
        DateCell(
            day = week.friday,
            onClickDate,
            Modifier.weight(1F)
        )
        DateCell(
            day = week.saturday,
            onClickDate,
            Modifier.weight(1F)
        )
    }
}

@Composable
private fun DateCell(
    day: Day,
    onClickDate: (Day) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .border(0.2.dp, Color.LightGray)
            .clickable {
                onClickDate(day)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = day.value.dayOfMonth.toString(),
            modifier = Modifier
                .padding(vertical = 5.dp)
                .size(20.dp)
                .drawBehind {
                    if (day.value == LocalDate.now()) {
                        drawOval(
                            color = Colors.secondPrimary,
                        )
                    }
                },
            textAlign = TextAlign.Center,
            color = if (day.value == LocalDate.now()) {
                Color.White
            } else {
                Color.Black
            }
        )
        if (day.hasSomething()) {
            if (day.weight != null) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .background(
                            Color.Green,
                            RoundedCornerShape(5.dp)
                        )
                        .padding(horizontal = 2.dp)
                ) {
                    Text(
                        text = day.weight.toString(),
                        fontSize = 12.sp,
                    )
                    Text(
                        text = " " + stringResource(id = R.string.unit_kg),
                        fontSize = 9.sp
                    )
                }
            } else {
                Text(text = "")
            }
            Spacer(modifier = Modifier.size(5.dp))
            if (day.kcal != 0) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .background(
                            Color.Blue,
                            RoundedCornerShape(5.dp)
                        )
                        .padding(horizontal = 2.dp)
                ) {
                    Text(
                        text = day.kcal.toString(),
                        fontSize = 12.sp,
                        color = Color.White,
                    )
                    Text(
                        text = " " + stringResource(id = R.string.unit_kcal),
                        fontSize = 9.sp,
                        color = Color.White,
                    )
                }
            } else {
                Text(text = "")
            }
        }
    }
}
