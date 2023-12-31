package com.app.body_manage.ui.calendar

import androidx.lifecycle.ViewModel
import com.app.body_manage.util.DateUtil
import java.time.LocalDate

class CalendarListViewModel : ViewModel() {

    val today: LocalDate = LocalDate.now()
    val yearMonth: String
        get() {
            return DateUtil.localDateConvertJapaneseFormatYearMonth(today)
        }
}
