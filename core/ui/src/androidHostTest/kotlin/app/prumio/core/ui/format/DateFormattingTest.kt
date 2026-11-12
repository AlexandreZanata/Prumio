package app.prumio.core.ui.format

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DateFormattingTest {
    private val date = LocalDate(2026, 1, 26) // Monday

    @Test
    fun `dates follow the language`() {
        assertEquals("Mon, Jan 26", formatShortDate(date, "en-US"))
        assertEquals("seg., 26 de jan.", formatShortDate(date, "pt-BR"))
        assertEquals("Monday", formatWeekday(date, "en-US"))
        assertEquals("segunda-feira", formatWeekday(date, "pt-BR"))
        assertEquals("Mon", formatShortWeekday(date, "en-US"))
        assertEquals("January 2026", formatMonthYear(date, "en-US"))
        assertEquals("janeiro de 2026", formatMonthYear(date, "pt-BR"))
    }

    @Test
    fun `times follow the 12 or 24 hour preference`() {
        val time = LocalTime(15, 30)
        assertEquals("15:30", formatTime(time, is24Hour = true, languageTag = "en-US"))
        // ICU versions differ in the space before AM/PM (regular, no-break or narrow no-break).
        val twelveHour = formatTime(time, is24Hour = false, languageTag = "en-US")
        assertEquals("3:30 PM", twelveHour.replace('\u202F', ' ').replace('\u00A0', ' '))
    }
}
