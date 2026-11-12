package app.prumio.app.shell.navigation

import androidx.navigation3.runtime.NavKey
import app.prumio.core.navigation.AgendaKey
import app.prumio.core.navigation.CaptureKey
import app.prumio.core.navigation.InboxKey
import app.prumio.core.navigation.MattersKey
import app.prumio.core.navigation.SettingsKey
import app.prumio.core.navigation.TodayKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PrumioNavigatorTest {
    private val backStack = mutableListOf<NavKey>(TodayKey)
    private val navigator = PrumioNavigator(backStack)

    @Test
    fun `selecting a tab keeps Today as root`() {
        navigator.navigate(AgendaKey)
        navigator.navigate(MattersKey)

        assertEquals(listOf<NavKey>(TodayKey, MattersKey), backStack)
        assertEquals(MattersKey, navigator.currentTopLevelKey)
    }

    @Test
    fun `selecting Today resets the stack`() {
        navigator.navigate(InboxKey)
        navigator.navigate(SettingsKey)

        navigator.navigate(TodayKey)

        assertEquals(listOf<NavKey>(TodayKey), backStack)
    }

    @Test
    fun `screens are pushed once and popped by goBack`() {
        navigator.navigate(SettingsKey)
        navigator.navigate(SettingsKey)
        assertEquals(listOf(TodayKey, SettingsKey), backStack)
        assertFalse(navigator.isShowingTopLevel)

        navigator.goBack()
        navigator.goBack()

        assertEquals(listOf<NavKey>(TodayKey), backStack)
    }

    @Test
    fun `BR-INBOX-002 capture stays one tap away on every tab`() {
        listOf(TodayKey, AgendaKey, MattersKey, InboxKey).forEach { tab ->
            navigator.navigate(tab)
            assertTrue(navigator.isShowingTopLevel)
            navigator.navigate(CaptureKey)
            assertEquals(tab, navigator.currentScreenKey)
            assertTrue(navigator.isShowingTopLevel)
            navigator.goBack()
            assertEquals(tab, navigator.currentScreenKey)
        }
    }
}
