package app.prumio.core.domain.settings

import app.prumio.core.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Typed settings with defaults, live. Rule: BR-SET-001. */
class ObserveSettingsUseCase(private val settings: SettingsRepository) {
    operator fun invoke(): Flow<Settings> = settings.observeValues().map(SettingKeys::read)
}
