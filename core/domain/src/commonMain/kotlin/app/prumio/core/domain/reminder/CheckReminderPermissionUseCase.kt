package app.prumio.core.domain.reminder

import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.settings.SettingKeys
import app.prumio.core.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.first

/** BR-REM-014: only in a reminder-creation context, with explanation, never after two denials. */
class CheckReminderPermissionUseCase(private val access: NotificationAccess, private val settings: SettingsRepository) {
    suspend operator fun invoke(hasReminders: Boolean) = storageCall {
        val status = access.status()
        hasReminders && status.canAskPermission && !status.notificationsAllowed &&
            SettingKeys.NotificationPermissionDenials.read(settings.observeValues().first()) < 2
    }
}
