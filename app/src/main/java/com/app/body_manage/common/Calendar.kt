package com.app.body_manage.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.boguszpawlowski.composecalendar.StaticCalendar
import java.time.LocalDate

@Composable
fun Calendar(
    selectedDate: LocalDate,
    modifier: Modifier = Modifier,
    onClickDate: (LocalDate) -> Unit
) {
    Surface {
        StaticCalendar(
            modifier = modifier
                .padding(bottom = 10.dp),
            monthHeader = {
                CalendarMonthHeader(monthState = it)
            },
            dayContent = {
                CalendarDay(state = it, selectedDate = selectedDate) { date ->
                    onClickDate.invoke(date)
                }
            }
        )
    }
}