package app.prumio.core.domain.reminder

import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.domain.settings.SettingKeys
import app.prumio.core.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.first

/** BR-REM-013 and BR-REM-014: honest status, with a dismissible denial banner only when reminders exist. */
class GetReminderReliabilityUseCase(
    private val access: NotificationAccess,
    private val settings: SettingsRepository,
    private val reminders: ReminderRepository,
    private val matters: MatterRepository,
) {
    suspend operator fun invoke() = storageCall {
        val status = access.status()
        val values = settings.observeValues().first()
        val hasReminders =
            reminders.observeAll().first().isNotEmpty() ||
                matters.observeUnresolved().first().any { it.followUpDate != null }
        ReminderReliability(
            status,
            !status.notificationsAllowed && hasReminders && !SettingKeys.NotificationBannerDismissed.read(values),
        )
    }
}
