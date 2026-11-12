package app.prumio.core.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

/** App theme choice (setting `theme`). */
enum class ThemePreference {
    System,
    Light,
    Dark,
}

/**
 * Typed user settings with their defaults (docs/domain/model.md#settings, BR-SET-001).
 * Later phases add their keys here and in `SettingKeys` (:core:domain).
 */
data class Settings(
    val theme: ThemePreference = ThemePreference.System,
    /** null = first day of the week of the device locale. */
    val weekStart: DayOfWeek? = null,
    val defaultReminderTime: LocalTime = LocalTime(9, 0),
    val defaultAppointmentNoticeMinutes: Int = 30,
    val notificationPermissionDenials: Int = 0,
    val notificationBannerDismissed: Boolean = false,
)
