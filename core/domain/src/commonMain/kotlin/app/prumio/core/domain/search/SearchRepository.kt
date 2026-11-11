package app.prumio.core.domain.search

import app.prumio.core.model.Appointment
import app.prumio.core.model.InboxItem
import app.prumio.core.model.Matter
import app.prumio.core.model.Task

/** Port: full-text search over stored items (docs/architecture/contracts.md#search-phase-1). */
interface SearchRepository {
    /**
     * Items whose searchable text (see [searchableText]) contains **every** term, each term being
     * already normalized with [normalizeForSearch] (BR-SEARCH-002). Unordered.
     */
    suspend fun search(terms: List<String>): SearchHits
}

data class SearchHits(
    val tasks: List<Task> = emptyList(),
    val appointments: List<Appointment> = emptyList(),
    val matters: List<Matter> = emptyList(),
    val inboxItems: List<InboxItem> = emptyList(),
)
