package com.app.body_manage.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.data.calendar.Day
import com.app.body_manage.data.calendar.Holiday
import com.app.body_manage.data.calendar.Month
import com.app.body_manage.data.calendar.Week
import com.app.body_manage.data.calendar.Weekday
import com.app.body_manage.data.model.Meal
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.data.repository.MealRepository
import com.app.body_manage.data.repository.TrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

class CalendarListViewModel(
    private val measureRepository: BodyMeasureRepository,
    private val mealRepository: MealRepository,
    private val trainingRepository: TrainingRepository,
) : ViewModel() {
    private val _months = MutableStateFlow(emptyList<Month>())
    val months = _months.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _focusedMonth = MutableStateFlow(getFirstDay())
    val focusedMonth =
        _focusedMonth.stateIn(viewModelScope, SharingStarted.Eagerly, LocalDate.now())

    private fun getFirstDay(): LocalDate {
        val now = LocalDate.now()
        return LocalDate.of(now.year, now.month, 1)
    }

    init {
        viewModelScope.launch {
            _months.update {
                val firstDayOfThisMonth = getFirstDay()
                listOf(
                    createMonth(firstDayOfThisMonth.minusMonths(1)),
                    createMonth(firstDayOfThisMonth),
                    createMonth(firstDayOfThisMonth.plusMonths(1)),
                )
            }
        }
    }

    fun moveToPrev() {
        val aheadMonth = _months.value.first()
        if (aheadMonth.firstDay == _focusedMonth.value.minusMonths(1)) {
            viewModelScope.launch {
                _months.update {
                    listOf(createMonth(aheadMonth.firstDay.minusMonths(1))) + it
                }
            }
        }
        _focusedMonth.update {
            _focusedMonth.value.minusMonths(1)
        }
    }

    fun moveToNext() {
        val lastMonth = _months.value.last()
        if (lastMonth.firstDay == _focusedMonth.value.plusMonths(1)) {
            viewModelScope.launch {
                _months.update {
                    it + listOf(createMonth(lastMonth.firstDay.plusMonths(1)))
                }
            }
        }
        _focusedMonth.update {
            _focusedMonth.value.plusMonths(1)
        }
    }

    private suspend fun createMonth(thisMonth: LocalDate): Month {
        // その月の初日を取得
        val firstWeek = createFirstWeek(LocalDate.of(thisMonth.year, thisMonth.month, 1))
        // 第二週作成
        val secondWeek = createWeekNotFirstAndLast(firstWeek.saturday.value.plusDays(1))
        // 第三週作成
        val thirdWeek = createWeekNotFirstAndLast(secondWeek.saturday.value.plusDays(1))
        // 第四週作成
        val fourthWeek = createWeekNotFirstAndLast(thirdWeek.saturday.value.plusDays(1))
        // 第五週作成
        val fifthWeek = createWeekNotFirstAndLast(fourthWeek.saturday.value.plusDays(1))
        // 第六週作成するかも
        val sixthWeek = createWeekNotFirstAndLast(fifthWeek.saturday.value.plusDays(1))

        return Month(
            firstDay = thisMonth,
            firstWeek = firstWeek,
            secondWeek = secondWeek,
            thirdWeek = thirdWeek,
            fourthWeek = fourthWeek,
            fifthWeek = fifthWeek,
            sixthWeek = sixthWeek,
        )
    }

    private suspend fun createWeekNotFirstAndLast(firstDay: LocalDate): Week {
        return Week(
            sunday = firstDay.toDay(),
            monday = firstDay.plusDays(1).toDay(),
            tuesday = firstDay.plusDays(2).toDay(),
            wednesday = firstDay.plusDays(3).toDay(),
            thursday = firstDay.plusDays(4).toDay(),
            friday = firstDay.plusDays(5).toDay(),
            saturday = firstDay.plusDays(6).toDay(),
        )
    }

    private suspend fun createFirstWeek(firstDay: LocalDate): Week {
        // １日より前に何日分のデータを作成する必要があるか
        val firstWeek = when (checkNotNull(firstDay.dayOfWeek)) {
            DayOfWeek.SUNDAY -> listOf(
                firstDay,
                firstDay.plusDays(1),
                firstDay.plusDays(2),
                firstDay.plusDays(3),
                firstDay.plusDays(4),
                firstDay.plusDays(5),
                firstDay.plusDays(6),
            )

            DayOfWeek.MONDAY -> listOf(
                firstDay.minusDays(1),
                firstDay,
                firstDay.plusDays(1),
                firstDay.plusDays(2),
                firstDay.plusDays(3),
                firstDay.plusDays(4),
                firstDay.plusDays(5),
            )

            DayOfWeek.TUESDAY -> listOf(
                firstDay.minusDays(2),
                firstDay.minusDays(1),
                firstDay,
                firstDay.plusDays(1),
                firstDay.plusDays(2),
                firstDay.plusDays(3),
                firstDay.plusDays(4),
            )

            DayOfWeek.WEDNESDAY -> listOf(
                firstDay.minusDays(3),
                firstDay.minusDays(2),
                firstDay.minusDays(1),
                firstDay,
                firstDay.plusDays(1),
                firstDay.plusDays(2),
                firstDay.plusDays(3),
            )

            DayOfWeek.THURSDAY -> listOf(
                firstDay.minusDays(4),
                firstDay.minusDays(3),
                firstDay.minusDays(2),
                firstDay.minusDays(1),
                firstDay,
                firstDay.plusDays(1),
                firstDay.plusDays(2),
            )

            DayOfWeek.FRIDAY -> listOf(
                firstDay.minusDays(5),
                firstDay.minusDays(4),
                firstDay.minusDays(3),
                firstDay.minusDays(2),
                firstDay.minusDays(1),
                firstDay,
                firstDay.plusDays(1),
            )

            DayOfWeek.SATURDAY -> listOf(
                firstDay.minusDays(6),
                firstDay.minusDays(5),
                firstDay.minusDays(4),
                firstDay.minusDays(3),
                firstDay.minusDays(2),
                firstDay.minusDays(1),
                firstDay,
            )
        }
        return Week(
            sunday = firstWeek[0].toDay(),
            monday = firstWeek[1].toDay(),
            tuesday = firstWeek[2].toDay(),
            wednesday = firstWeek[3].toDay(),
            thursday = firstWeek[4].toDay(),
            friday = firstWeek[5].toDay(),
            saturday = firstWeek[6].toDay(),
        )
    }

    private suspend fun LocalDate.toDay(): Day {
        val meal = this@CalendarListViewModel.mealRepository.getMealsByDate(this)
        val measures = this@CalendarListViewModel.measureRepository.getEntityListByDate(this)
        val hasTraining =
            this@CalendarListViewModel.trainingRepository.getTrainingsByDate(this).isNotEmpty()
        val hasMorning = meal.any { it.timing == Meal.Timing.BREAKFAST }
        val hasLunch = meal.any { it.timing == Meal.Timing.LUNCH }
        val hasDinner = meal.any { it.timing == Meal.Timing.DINNER }
        val hasSnack = meal.any { it.timing == Meal.Timing.SNACK }
        val kcal = meal.sumOf { it.totalKcal }
        return when (dayOfWeek) {
            DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> Holiday(
                value = this,
                hasMorning = hasMorning,
                hasLunch = hasLunch,
                hasDinner = hasDinner,
                hasMiddle = hasSnack,
                training = hasTraining,
                kcal = kcal,
                weight = measures.takeIf { it.isNotEmpty() }?.minOf { it.weight },
                name = "",
            )

            else -> Weekday(
                value = this,
                hasMorning = hasMorning,
                hasLunch = hasLunch,
                hasDinner = hasDinner,
                hasMiddle = hasSnack,
                training = hasTraining,
                kcal = kcal,
                weight = measures.takeIf { it.isNotEmpty() }?.minOf { it.weight },
            )
        }
    }

    fun updateCurrentMonth() {
        viewModelScope.launch {
            val recreatedMonth = createMonth(_focusedMonth.value)
            _months.update { months ->
                val updated = months.map { month ->
                    if (month.firstDay == _focusedMonth.value) {
                        recreatedMonth
                    } else {
                        month
                    }
                }
                updated
            }
        }
    }
}
