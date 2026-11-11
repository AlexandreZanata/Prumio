package app.prumio.core.ui.format

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/** How a date is named relative to today ("Tomorrow", "Friday", "Mon, 26 Jan"). Pure and testable. */
sealed interface RelativeDay {
    data object Yesterday : RelativeDay

    data object Today : RelativeDay

    data object Tomorrow : RelativeDay

    /** Within the next week: named by weekday. */
    data class ThisWeek(val dayOfWeek: DayOfWeek) : RelativeDay

    data class Other(val date: LocalDate) : RelativeDay
}

fun relativeDayOf(date: LocalDate, today: LocalDate): RelativeDay = when (date) {
    today -> RelativeDay.Today
    today.plus(DatePeriod(days = 1)) -> RelativeDay.Tomorrow
    today.minus(DatePeriod(days = 1)) -> RelativeDay.Yesterday
    in today.plus(DatePeriod(days = 2))..today.plus(DatePeriod(days = 6)) -> RelativeDay.ThisWeek(date.dayOfWeek)
    else -> RelativeDay.Other(date)
}
