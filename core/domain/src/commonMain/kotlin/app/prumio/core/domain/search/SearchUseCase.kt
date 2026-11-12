package app.prumio.core.domain.search

import app.prumio.core.common.Outcome
import app.prumio.core.common.asSuccess
import app.prumio.core.common.map
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.storageCall
import app.prumio.core.model.Appointment
import app.prumio.core.model.InboxItem
import app.prumio.core.model.Matter
import app.prumio.core.model.Task

/** Grouped search results; [queryTooShort] when nothing was searched (BR-SEARCH-004). */
data class SearchResults(
    val tasks: List<Task> = emptyList(),
    val appointments: List<Appointment> = emptyList(),
    val matters: List<Matter> = emptyList(),
    val inboxItems: List<InboxItem> = emptyList(),
    val queryTooShort: Boolean = false,
) {
    val isEmpty: Boolean get() = tasks.isEmpty() && appointments.isEmpty() && matters.isEmpty() && inboxItems.isEmpty()

    companion object {
        const val MIN_QUERY_LENGTH = 2
    }
}

/** Offline search. Rules: BR-SEARCH-001, BR-SEARCH-002, BR-SEARCH-003, BR-SEARCH-004. */
class SearchUseCase(private val search: SearchRepository) {
    suspend operator fun invoke(query: String): Outcome<SearchResults, DomainError> {
        if (query.trim().length < SearchResults.MIN_QUERY_LENGTH) return SearchResults(queryTooShort = true).asSuccess()
        return storageCall { search.search(searchTerms(query)) }.map { hits ->
            SearchResults(
                tasks = hits.tasks.sortedWith(openFirst({ it.isOpen }, { it.updatedAt })),
                appointments = hits.appointments.sortedByDescending { it.updatedAt },
                matters = hits.matters.sortedWith(openFirst({ !it.isResolved }, { it.updatedAt })),
                inboxItems = hits.inboxItems.sortedByDescending { it.updatedAt },
            )
        }
    }

    /** BR-SEARCH-003: open before closed, most recently updated first. */
    private fun <T> openFirst(isOpen: (T) -> Boolean, updatedAt: (T) -> Comparable<*>): Comparator<T> =
        compareBy<T> { !isOpen(it) }.thenByDescending { updatedAt(it) }
}
