package app.prumio.core.ui.format

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.intl.Locale
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.date_today
import app.prumio.core.ui.resources.date_tomorrow
import app.prumio.core.ui.resources.date_with_time
import app.prumio.core.ui.resources.date_yesterday
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

// Localized formatting of dates and times (docs/architecture/i18n.md). Platform formatters are used
// because kotlinx-datetime has no localized names; the language tag makes them testable.

/** "Wednesday, 11 Nov" / "quarta-feira, 11 de nov.". */
expect fun formatDayHeader(date: LocalDate, languageTag: String): String

/** "Mon, 26 Jan" / "seg., 26 de jan.". */
expect fun formatShortDate(date: LocalDate, languageTag: String): String

/** Full weekday name: "Friday" / "sexta-feira". */
expect fun formatWeekday(date: LocalDate, languageTag: String): String

/** Abbreviated weekday for compact rows such as the agenda week strip: "Mon" / "seg.". */
expect fun formatShortWeekday(date: LocalDate, languageTag: String): String

/** Month and year: "November 2026" / "novembro de 2026". */
expect fun formatMonthYear(date: LocalDate, languageTag: String): String

/** "15:30" or "3:30 PM" depending on [is24Hour]. */
expect fun formatTime(time: LocalTime, is24Hour: Boolean, languageTag: String): String

/** Whether the device shows times in 24-hour format. */
@Composable
expect fun rememberIs24HourFormat(): Boolean

@Composable
private fun languageTag(): String = Locale.current.toLanguageTag()

/** Composable helper that re-formats when the app language changes. */
@Composable
fun LocalDate.dayHeaderText(): String {
    val tag = languageTag()
    return remember(this, tag) { formatDayHeader(this, tag) }
}

@Composable
fun LocalDate.shortWeekdayText(): String {
    val tag = languageTag()
    return remember(this, tag) { formatShortWeekday(this, tag) }
}

@Composable
fun LocalDate.monthYearText(): String {
    val tag = languageTag()
    return remember(this, tag) { formatMonthYear(this, tag) }
}

/** "Today", "Tomorrow", "Yesterday", a weekday within the next week, else "Mon, 26 Jan". */
@Composable
fun LocalDate.relativeDateText(today: LocalDate): String {
    val tag = languageTag()
    return when (val relative = relativeDayOf(this, today)) {
        RelativeDay.Today -> stringResource(Res.string.date_today)
        RelativeDay.Tomorrow -> stringResource(Res.string.date_tomorrow)
        RelativeDay.Yesterday -> stringResource(Res.string.date_yesterday)
        is RelativeDay.ThisWeek -> remember(this, tag) { formatWeekday(this, tag) }
        is RelativeDay.Other -> remember(this, tag) { formatShortDate(relative.date, tag) }
    }
}

/** Time in the device's 12/24-hour preference. */
@Composable
fun LocalTime.timeText(): String {
    val tag = languageTag()
    val is24Hour = rememberIs24HourFormat()
    return remember(this, tag, is24Hour) { formatTime(this, is24Hour, tag) }
}

/** "Tomorrow, 15:30" or just "Tomorrow" when [time] is null. */
@Composable
fun dateTimeText(date: LocalDate, time: LocalTime?, today: LocalDate): String {
    val day = date.relativeDateText(today)
    return if (time == null) day else stringResource(Res.string.date_with_time, day, time.timeText())
}
