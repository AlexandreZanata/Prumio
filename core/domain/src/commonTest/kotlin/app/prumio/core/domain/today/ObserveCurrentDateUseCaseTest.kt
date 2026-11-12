package app.prumio.core.domain.today

import app.cash.turbine.test
import app.prumio.core.testing.FakeTimeProvider
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveCurrentDateUseCaseTest {
    @Test
    fun `BR-TODAY-007 a new date is emitted after local midnight`() = runTest {
        val time = FakeTimeProvider(LocalDateTime(2026, 1, 15, 23, 59))

        ObserveCurrentDateUseCase(time)().test {
            assertEquals(LocalDate(2026, 1, 15), awaitItem())
            time.setLocal(LocalDateTime(2026, 1, 16, 0, 0, 5))
            assertEquals(LocalDate(2026, 1, 16), awaitItem())
        }
    }

    @Test
    fun `a frozen clock completes the flow instead of spinning`() = runTest {
        val time = FakeTimeProvider(LocalDateTime(2026, 1, 15, 23, 59))

        ObserveCurrentDateUseCase(time)().test {
            assertEquals(LocalDate(2026, 1, 15), awaitItem())
            awaitComplete()
        }
    }
}
