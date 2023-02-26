package com.app.body_manage.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import io.github.boguszpawlowski.composecalendar.StaticCalendar
import io.github.boguszpawlowski.composecalendar.rememberCalendarState
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun Calendar(
    selectedDate: LocalDate,
    modifier: Modifier = Modifier,
    onClickDate: (LocalDate) -> Unit,
    onChangeCurrentMonth: (YearMonth) -> Unit = {},
    markDayList: List<LocalDate> = listOf(),
    onClickBackButton: () -> Unit = {  }
) {
    val calendarState = rememberCalendarState()
    LaunchedEffect(calendarState.monthState.currentMonth) {
        onChangeCurrentMonth.invoke(calendarState.monthState.currentMonth)
    }
    StaticCalendar(
        monthHeader = {
            CalendarMonthHeader(
                monthState = it,
                onChangeCurrentMonth = onChangeCurrentMonth,
                onClickBackButton = onClickBackButton
            )
        },
        dayContent = {
            CalendarDay(markDayList, state = it, selectedDate = selectedDate) { date ->
                onClickDate.invoke(date)
            }
        },
        calendarState = calendarState
    )
}
