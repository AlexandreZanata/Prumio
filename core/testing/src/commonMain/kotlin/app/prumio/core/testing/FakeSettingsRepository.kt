package app.prumio.core.testing

import app.prumio.core.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update
import kotlin.time.Instant

class FakeSettingsRepository(private val store: FakeStore) : SettingsRepository {
    override fun observeValues(): Flow<Map<String, String>> = store.settings

    override suspend fun setValue(key: String, value: String?, updatedAt: Instant) =
        store.settings.update { if (value == null) it - key else it + (key to value) }
}
