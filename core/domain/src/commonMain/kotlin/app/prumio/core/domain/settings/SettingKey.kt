package app.prumio.core.domain.settings

import app.prumio.core.model.Settings
import app.prumio.core.model.ThemePreference
import kotlinx.datetime.DayOfWeek

/**
 * A typed setting stored as text. [decode] returns null for unknown or invalid values, which then
 * fall back to [default] (BR-SET-001). Names are permanent once released.
 */
class SettingKey<T>(val name: String, val default: T, val encode: (T) -> String?, val decode: (String) -> T?) {
    fun read(values: Map<String, String>): T = values[name]?.let(decode) ?: default
}

/** Registry of every setting. Recipe: docs/ai/recipes/add-setting.md. */
object SettingKeys {
    val Theme = SettingKey(
        name = "theme",
        default = ThemePreference.System,
        encode = { it.name },
        decode = { stored -> ThemePreference.entries.firstOrNull { it.name == stored } },
    )

    val WeekStart = SettingKey<DayOfWeek?>(
        name = "week_start",
        default = null,
        encode = { it?.name },
        decode = { stored -> DayOfWeek.entries.firstOrNull { it.name == stored } },
    )

    /** Typed snapshot of all settings. */
    fun read(values: Map<String, String>): Settings = Settings(
        theme = Theme.read(values),
        weekStart = WeekStart.read(values),
    )
}
