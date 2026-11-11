package app.prumio.feature.today

import app.cash.turbine.test
import app.prumio.core.domain.today.GetDayPeriodUseCase
import app.prumio.core.model.DayPeriod
import app.prumio.core.navigation.SettingsKey
import app.prumio.core.testing.FakeTimeProvider
import app.prumio.core.testing.runViewModelTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class TodayViewModelTest {
    private val timeProvider = FakeTimeProvider(LocalDateTime(2026, 11, 11, 19, 0))

    private fun createViewModel() = TodayViewModel(GetDayPeriodUseCase(timeProvider), timeProvider)

    @Test
    fun `BR-TODAY-001 initial state shows the local date and day period`() = runViewModelTest {
        val viewModel = createViewModel()

        assertEquals(DayPeriod.Evening, viewModel.state.value.dayPeriod)
        assertEquals(LocalDate(2026, 11, 11), viewModel.state.value.date)
    }

    @Test
    fun `refresh picks up the new time`() = runViewModelTest {
        val viewModel = createViewModel()
        timeProvider.setLocal(LocalDateTime(2026, 11, 12, 8, 0))

        viewModel.onAction(TodayAction.Refresh)

        assertEquals(DayPeriod.Morning, viewModel.state.value.dayPeriod)
        assertEquals(LocalDate(2026, 11, 12), viewModel.state.value.date)
    }

    @Test
    fun `opening settings emits a navigation effect`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onAction(TodayAction.OpenSettings)
            assertEquals(TodayEffect.Navigate(SettingsKey), awaitItem())
        }
    }
}
