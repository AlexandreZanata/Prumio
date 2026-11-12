package app.prumio.app.shell

import app.prumio.core.domain.settings.SettingKeys
import app.prumio.core.model.ThemePreference
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.anInboxItem
import app.prumio.core.testing.runViewModelTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ShellViewModelTest {
    @Test
    fun `BR-INBOX-005 badge follows the Inbox count across screens`() = runViewModelTest {
        val domain = TestUseCases()
        val vm = ShellViewModel(domain.observeInboxCount, domain.observeSettings)
        assertEquals(0, vm.state.value.inboxCount)
        domain.repos.store.putInboxItem(anInboxItem())
        assertEquals(1, vm.state.value.inboxCount)
        domain.discardInboxItem(anInboxItem().id)
        assertEquals(0, vm.state.value.inboxCount)
    }

    @Test
    fun `BR-SET-001 shell applies stored theme and system default`() = runViewModelTest {
        val domain = TestUseCases()
        val vm = ShellViewModel(domain.observeInboxCount, domain.observeSettings)
        assertEquals(ThemePreference.System, vm.state.value.theme)
        domain.updateSetting(SettingKeys.Theme, ThemePreference.Dark)
        assertEquals(ThemePreference.Dark, vm.state.value.theme)
    }
}
