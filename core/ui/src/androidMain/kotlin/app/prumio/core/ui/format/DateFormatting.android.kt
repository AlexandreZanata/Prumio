package app.prumio.core.ui.format

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun pattern(skeleton: String, languageTag: String): DateTimeFormatter {
    val locale = Locale.forLanguageTag(languageTag)
    return DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(locale, skeleton), locale)
}

actual fun formatDayHeader(date: LocalDate, languageTag: String): String =
    pattern("EEEEdMMM", languageTag).format(date.toJavaLocalDate())

actual fun formatShortDate(date: LocalDate, languageTag: String): String =
    pattern("EEEdMMM", languageTag).format(date.toJavaLocalDate())

actual fun formatWeekday(date: LocalDate, languageTag: String): String =
    pattern("EEEE", languageTag).format(date.toJavaLocalDate())

actual fun formatShortWeekday(date: LocalDate, languageTag: String): String =
    pattern("EEE", languageTag).format(date.toJavaLocalDate())

actual fun formatMonthYear(date: LocalDate, languageTag: String): String =
    pattern("MMMMy", languageTag).format(date.toJavaLocalDate())

actual fun formatTime(time: LocalTime, is24Hour: Boolean, languageTag: String): String =
    pattern(if (is24Hour) "Hm" else "hm", languageTag).format(time.toJavaLocalTime())

@Composable
actual fun rememberIs24HourFormat(): Boolean = DateFormat.is24HourFormat(LocalContext.current)
