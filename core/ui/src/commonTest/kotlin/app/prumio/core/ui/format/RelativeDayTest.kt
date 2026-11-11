package app.prumio.core.ui.format

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class RelativeDayTest {
    private val today = LocalDate(2026, 1, 15) // Thursday

    @Test
    fun `adjacent days have names`() {
        assertEquals(RelativeDay.Today, relativeDayOf(today, today))
        assertEquals(RelativeDay.Tomorrow, relativeDayOf(LocalDate(2026, 1, 16), today))
        assertEquals(RelativeDay.Yesterday, relativeDayOf(LocalDate(2026, 1, 14), today))
    }

    @Test
    fun `the rest of the coming week is named by weekday`() {
        assertEquals(RelativeDay.ThisWeek(DayOfWeek.SATURDAY), relativeDayOf(LocalDate(2026, 1, 17), today))
        assertEquals(RelativeDay.ThisWeek(DayOfWeek.WEDNESDAY), relativeDayOf(LocalDate(2026, 1, 21), today))
    }

    @Test
    fun `a week ahead and the past beyond yesterday use the date`() {
        assertEquals(RelativeDay.Other(LocalDate(2026, 1, 22)), relativeDayOf(LocalDate(2026, 1, 22), today))
        assertEquals(RelativeDay.Other(LocalDate(2026, 1, 13)), relativeDayOf(LocalDate(2026, 1, 13), today))
    }

    @Test
    fun `month and year boundaries are handled`() {
        assertEquals(RelativeDay.Tomorrow, relativeDayOf(LocalDate(2027, 1, 1), LocalDate(2026, 12, 31)))
    }
}
