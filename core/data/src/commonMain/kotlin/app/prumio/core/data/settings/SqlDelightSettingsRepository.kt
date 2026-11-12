package app.prumio.core.data.settings

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.prumio.core.common.coroutines.DispatcherProvider
import app.prumio.core.data.storage.toColumn
import app.prumio.core.database.PrumioDatabase
import app.prumio.core.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Instant

internal class SqlDelightSettingsRepository(
    private val database: PrumioDatabase,
    private val dispatchers: DispatcherProvider,
) : SettingsRepository {
    private val queries = database.settingQueries

    override fun observeValues(): Flow<Map<String, String>> =
        queries.selectAll().asFlow().mapToList(dispatchers.io).map { rows -> rows.associate { it.key to it.value_ } }

    override suspend fun setValue(key: String, value: String?, updatedAt: Instant) = withContext(dispatchers.io) {
        if (value == null) queries.deleteByKey(key) else queries.upsert(key, value, updatedAt.toColumn())
        Unit
    }
}
