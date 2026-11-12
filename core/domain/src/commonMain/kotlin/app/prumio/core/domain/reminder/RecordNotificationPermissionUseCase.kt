package app.prumio.core.domain.reminder

import app.prumio.core.common.time.TimeProvider
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.settings.SettingKeys
import app.prumio.core.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.first

/** Device-specific refusal count, capped at two (BR-REM-014). */
class RecordNotificationPermissionUseCase(private val settings: SettingsRepository, private val time: TimeProvider) {
    suspend operator fun invoke(granted: Boolean) = storageCall {
        if (!granted) {
            val count = SettingKeys.NotificationPermissionDenials.read(settings.observeValues().first())
            settings.setValue(
                SettingKeys.NotificationPermissionDenials.name,
                (count + 1).coerceAtMost(2).toString(),
                time.now(),
            )
            settings.setValue(SettingKeys.NotificationBannerDismissed.name, null, time.now())
        }
    }
}
