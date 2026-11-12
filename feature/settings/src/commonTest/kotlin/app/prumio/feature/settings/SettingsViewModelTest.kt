package app.prumio.feature.settings

import app.cash.turbine.test
import app.prumio.core.domain.settings.SettingsRepository
import app.prumio.core.domain.settings.UpdateSettingUseCase
import app.prumio.core.model.ThemePreference
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.runViewModelTest
import kotlinx.coroutines.flow.flowOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Instant

class SettingsViewModelTest {
    private val domain = TestUseCases()

    @Test
    fun `BR-SET-001 default is system and theme changes persist and update live`() = runViewModelTest {
        val vm = SettingsViewModel(domain.observeSettings, domain.updateSetting)
        assertEquals(ThemePreference.System, vm.state.value.theme)
        ThemePreference.entries.forEach { theme ->
            vm.onAction(SettingsAction.ChangeTheme(theme))
            assertEquals(theme, vm.state.value.theme)
        }
        vm.onAction(SettingsAction.ChangeTheme(ThemePreference.System))
        assertEquals(emptyMap(), domain.repos.store.settings.value)
    }

    @Test
    fun `platform actions are effects`() = runViewModelTest {
        val vm = SettingsViewModel(domain.observeSettings, domain.updateSetting)
        vm.effects.test {
            vm.onAction(SettingsAction.OpenLanguage)
            assertEquals(SettingsEffect.OpenLanguage, awaitItem())
            vm.onAction(SettingsAction.OpenSource)
            assertEquals(SettingsEffect.OpenSource, awaitItem())
            vm.onAction(SettingsAction.Close)
            assertEquals(SettingsEffect.Close, awaitItem())
        }
    }

    @Test
    fun `failed preferences show a message`() = runViewModelTest {
        val failing = object : SettingsRepository {
            override fun observeValues() = flowOf(emptyMap<String, String>())
            override suspend fun setValue(key: String, value: String?, updatedAt: Instant): Unit =
                error("Storage unavailable")
        }
        val vm = SettingsViewModel(domain.observeSettings, UpdateSettingUseCase(failing, domain.time))
        vm.effects.test {
            vm.onAction(SettingsAction.ChangeTheme(ThemePreference.Dark))
            assertIs<SettingsEffect.ShowMessage>(awaitItem())
        }
        assertEquals(ThemePreference.System, vm.state.value.theme)
    }
}
