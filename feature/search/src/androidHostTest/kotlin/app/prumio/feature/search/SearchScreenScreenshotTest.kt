package app.prumio.feature.search

import app.prumio.core.domain.search.SearchResults
import app.prumio.core.screenshottesting.ScreenshotTest
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aTask
import app.prumio.core.testing.anAppointment
import app.prumio.core.testing.anInboxItem
import org.junit.Test

class SearchScreenScreenshotTest : ScreenshotTest() {
    @Test fun hint() = screenshot("hint") { SearchScreen(SearchUiState(), {}) }

    @Test fun noResults() = screenshot("no_results") { SearchScreen(SearchUiState("documents", SearchResults()), {}) }

    @Test fun groups() = screenshot("groups") {
        SearchScreen(
            SearchUiState(
                "documentacao",
                SearchResults(
                    tasks = listOf(aTask(title = "Documentação do carro")),
                    appointments = listOf(anAppointment(title = "Entregar documentação")),
                    matters = listOf(aMatter(title = "Documentação pendente")),
                    inboxItems = listOf(anInboxItem(text = "Documentação recebida")),
                ),
            ),
            {
            },
        )
    }
}
