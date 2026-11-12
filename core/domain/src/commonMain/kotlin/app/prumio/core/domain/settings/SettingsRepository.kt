package app.prumio.core.domain.settings

import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

/**
 * Port: raw key-value storage of settings. Typing and defaults are domain rules
 * ([SettingKeys], BR-SET-001), so the adapter stays a dumb store.
 */
interface SettingsRepository {
    fun observeValues(): Flow<Map<String, String>>

    /** Stores [value] for [key]; null removes the key (back to its default). */
    suspend fun setValue(key: String, value: String?, updatedAt: Instant)
}
