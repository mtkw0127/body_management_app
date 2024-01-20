package com.app.body_manage.data.calendar

import java.time.LocalDate

data class Month(
    val firstDay: LocalDate,
    val firstWeek: Week,
    val secondWeek: Week,
    val thirdWeek: Week,
    val fourthWeek: Week,
    val fifthWeek: Week,
    val sixthWeek: Week,
)
