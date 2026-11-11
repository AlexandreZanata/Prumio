package app.prumio.core.testing

import app.prumio.core.domain.search.SearchHits
import app.prumio.core.domain.search.SearchRepository
import app.prumio.core.domain.search.searchableText

class FakeSearchRepository(private val store: FakeStore) : SearchRepository {
    override suspend fun search(terms: List<String>): SearchHits {
        fun matches(text: String) = terms.all { text.contains(it) }
        val notesByMatter = store.matterEntries.value.values.groupBy({ it.matterId }, { it.searchableText() })
        return SearchHits(
            tasks = store.tasks.value.values.filter { matches(it.searchableText()) },
            appointments = store.appointments.value.values.filter { matches(it.searchableText()) },
            matters = store.matters.value.values.filter { matter ->
                // SQL semantics: each term must match the matter text or one of its notes.
                val texts = listOf(matter.searchableText()) + notesByMatter[matter.id].orEmpty()
                terms.all { term -> texts.any { it.contains(term) } }
            },
            inboxItems = store.inboxItems.value.values.filter { matches(it.searchableText()) },
        )
    }
}
