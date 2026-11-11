package app.prumio.core.ui.format

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.intl.Locale
import kotlinx.datetime.LocalDate

/**
 * Localized "weekday, day month" header, e.g. "Wednesday, 11 Nov" / "quarta-feira, 11 de nov.".
 * Platform formatters are used because kotlinx-datetime has no localized month names.
 */
expect fun formatDayHeader(date: LocalDate, languageTag: String): String

/** Composable helper that re-formats when the app language changes. */
@Composable
fun LocalDate.dayHeaderText(): String {
    val languageTag = Locale.current.toLanguageTag()
    return remember(this, languageTag) { formatDayHeader(this, languageTag) }
}
