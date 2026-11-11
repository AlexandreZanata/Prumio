package app.prumio.core.ui.format

import android.text.format.DateFormat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

actual fun formatDayHeader(date: LocalDate, languageTag: String): String {
    val locale = Locale.forLanguageTag(languageTag)
    val pattern = DateFormat.getBestDateTimePattern(locale, "EEEEdMMM")
    return DateTimeFormatter.ofPattern(pattern, locale).format(date.toJavaLocalDate())
}
