package app.prumio.core.domain.settings

import app.prumio.core.model.Settings
import app.prumio.core.model.ThemePreference
import app.prumio.core.testing.FakeRepositories
import app.prumio.core.testing.FakeTimeProvider
import app.prumio.core.testing.assertSuccess
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsUseCasesTest {
    private val repos = FakeRepositories()
    private val observe = ObserveSettingsUseCase(repos.settings)
    private val update = UpdateSettingUseCase(repos.settings, FakeTimeProvider())

    @Test
    fun `BR-SET-001 settings start at their defaults and keep what the user chose`() = runTest {
        assertEquals(Settings(), observe().first())

        update(SettingKeys.Theme, ThemePreference.Dark).assertSuccess()
        update(SettingKeys.WeekStart, DayOfWeek.MONDAY).assertSuccess()

        assertEquals(Settings(ThemePreference.Dark, DayOfWeek.MONDAY), observe().first())
    }

    @Test
    fun `BR-SET-001 choosing the default value removes the stored one`() = runTest {
        update(SettingKeys.Theme, ThemePreference.Light).assertSuccess()
        update(SettingKeys.Theme, ThemePreference.System).assertSuccess()

        assertEquals(emptyMap(), repos.store.settings.value)
    }
}
