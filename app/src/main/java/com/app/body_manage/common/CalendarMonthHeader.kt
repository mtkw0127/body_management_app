package com.app.body_manage.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import io.github.boguszpawlowski.composecalendar.header.MonthState
import java.time.YearMonth

@Composable
fun CalendarMonthHeader(
    monthState: MonthState,
    modifier: Modifier = Modifier,
    onChangeCurrentMonth: (YearMonth) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.testTag("Decrement"),
            onClick = {
                monthState.currentMonth = monthState.currentMonth.minusMonths(1)
                onChangeCurrentMonth.invoke(monthState.currentMonth)
            }
        ) {
            Image(
                imageVector = Icons.Default.KeyboardArrowLeft,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                contentDescription = "Previous",
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = monthState.currentMonth.year.toString() + "年",
            style = MaterialTheme.typography.h5
        )
        Text(
            modifier = Modifier.testTag("MonthLabel"),
            text = monthState.currentMonth.month.value.toString() + "月",
            style = MaterialTheme.typography.h5,
        )
        IconButton(
            modifier = Modifier.testTag("Increment"),
            onClick = {
                monthState.currentMonth = monthState.currentMonth.plusMonths(1)
                onChangeCurrentMonth.invoke(monthState.currentMonth)
            }
        ) {
            Image(
                imageVector = Icons.Default.KeyboardArrowRight,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                contentDescription = "Next",
            )
        }
    }
}
