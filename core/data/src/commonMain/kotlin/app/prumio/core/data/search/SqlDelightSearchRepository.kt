package app.prumio.core.data.search

import app.prumio.core.common.coroutines.DispatcherProvider
import app.prumio.core.data.storage.toModel
import app.prumio.core.database.PrumioDatabase
import app.prumio.core.domain.search.SearchHits
import app.prumio.core.domain.search.SearchRepository
import kotlinx.coroutines.withContext

/** Every term must match (BR-SEARCH-002): one LIKE query per term and type, ids intersected. */
internal class SqlDelightSearchRepository(
    private val database: PrumioDatabase,
    private val dispatchers: DispatcherProvider,
) : SearchRepository {
    override suspend fun search(terms: List<String>): SearchHits = withContext(dispatchers.io) {
        if (terms.isEmpty()) return@withContext SearchHits()
        fun ids(select: (String) -> List<String>): List<String> =
            terms.map { select(it).toSet() }.reduce { acc, set -> acc intersect set }.toList()
        database.transactionWithResult {
            SearchHits(
                tasks = database.taskQueries.selectByIds(
                    ids {
                        database.taskQueries.selectIdsMatching(it).executeAsList()
                    },
                )
                    .executeAsList().map { it.toModel() },
                appointments = database.appointmentQueries
                    .selectByIds(ids { database.appointmentQueries.selectIdsMatching(it).executeAsList() })
                    .executeAsList().map { it.toModel() },
                matters = database.matterQueries.selectByIds(
                    ids {
                        database.matterQueries.selectIdsMatching(it).executeAsList()
                    },
                )
                    .executeAsList().map { it.toModel() },
                inboxItems = database.inbox_itemQueries
                    .selectByIds(ids { database.inbox_itemQueries.selectIdsMatching(it).executeAsList() })
                    .executeAsList().map { it.toModel() },
            )
        }
    }
}
