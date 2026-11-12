package app.prumio.core.domain.settings

import app.prumio.core.common.Outcome
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.storageCall

/** Stores one setting; the default value removes it so a future default change applies. Rule: BR-SET-001. */
class UpdateSettingUseCase(private val settings: SettingsRepository, private val time: TimeProvider) {
    suspend operator fun <T> invoke(key: SettingKey<T>, value: T): Outcome<Unit, DomainError> {
        val stored = if (value == key.default) null else key.encode(value)
        return storageCall { settings.setValue(key.name, stored, time.now()) }
    }
}
