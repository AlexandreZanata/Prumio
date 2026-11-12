package app.prumio.core.domain.settings

import app.prumio.core.model.Settings
import app.prumio.core.model.ThemePreference
import kotlinx.datetime.DayOfWeek
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingKeysTest {
    @Test
    fun `BR-SET-001 missing settings take their defaults`() {
        assertEquals(Settings(), SettingKeys.read(emptyMap()))
    }

    @Test
    fun `BR-SET-001 invalid stored values fall back to the default`() {
        val settings = SettingKeys.read(mapOf("theme" to "Purple", "week_start" to "SOMEDAY"))

        assertEquals(ThemePreference.System, settings.theme)
        assertEquals(null, settings.weekStart)
    }

    @Test
    fun `stored values round-trip through their keys`() {
        val values = mapOf(
            SettingKeys.Theme.name to SettingKeys.Theme.encode(ThemePreference.Dark)!!,
            SettingKeys.WeekStart.name to SettingKeys.WeekStart.encode(DayOfWeek.MONDAY)!!,
        )

        assertEquals(Settings(ThemePreference.Dark, DayOfWeek.MONDAY), SettingKeys.read(values))
        assertEquals(null, SettingKeys.WeekStart.encode(null))
    }
}
