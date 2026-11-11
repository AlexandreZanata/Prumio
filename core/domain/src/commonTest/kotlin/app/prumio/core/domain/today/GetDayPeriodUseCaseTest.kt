package app.prumio.core.domain.today

import app.prumio.core.model.DayPeriod
import app.prumio.core.testing.FakeTimeProvider
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class GetDayPeriodUseCaseTest {
    @Test
    fun `BR-TODAY-001 boundaries map to the right period`() {
        val expectations = mapOf(
            LocalTime(4, 59) to DayPeriod.Evening,
            LocalTime(5, 0) to DayPeriod.Morning,
            LocalTime(11, 59) to DayPeriod.Morning,
            LocalTime(12, 0) to DayPeriod.Afternoon,
            LocalTime(17, 59) to DayPeriod.Afternoon,
            LocalTime(18, 0) to DayPeriod.Evening,
            LocalTime(0, 0) to DayPeriod.Evening,
        )

        expectations.forEach { (time, expected) ->
            assertEquals(expected, dayPeriodAt(time), "at $time")
        }
    }

    @Test
    fun `BR-TODAY-001 uses the local time of the provider`() {
        val timeProvider = FakeTimeProvider(LocalDateTime(2026, 11, 11, 9, 30))

        assertEquals(DayPeriod.Morning, GetDayPeriodUseCase(timeProvider)())
    }
}
