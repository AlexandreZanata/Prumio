package app.prumio.feature.search

import app.cash.turbine.test
import app.prumio.core.domain.search.SearchHits
import app.prumio.core.domain.search.SearchRepository
import app.prumio.core.domain.search.SearchUseCase
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.MatterId
import app.prumio.core.model.TaskId
import app.prumio.core.navigation.AppointmentDetailKey
import app.prumio.core.navigation.InboxKey
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.TaskDetailKey
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aTask
import app.prumio.core.testing.anAppointment
import app.prumio.core.testing.anInboxItem
import app.prumio.core.testing.runViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    private val domain = TestUseCases()

    @Test
    fun `BR-SEARCH-001 returning from a changed detail refreshes the same query`() = runViewModelTest {
        domain.repos.store.putTask(aTask("t1", "Documents"))
        val vm = SearchViewModel(domain.search)
        vm.onAction(SearchAction.ChangeQuery("documents"))
        advanceTimeBy(200)
        runCurrent()
        assertEquals(1, vm.state.value.results?.tasks?.size)
        domain.deleteTask(TaskId("t1"))
        vm.onAction(SearchAction.Refresh)
        advanceTimeBy(200)
        runCurrent()
        assertTrue(vm.state.value.results?.isEmpty == true)
    }

    @Test
    fun `BR-SEARCH-004 query is debounced for 200 ms and short queries show a hint`() = runViewModelTest {
        val vm = SearchViewModel(domain.search)
        vm.onAction(SearchAction.ChangeQuery("c"))
        advanceTimeBy(199)
        assertNull(vm.state.value.results)
        advanceTimeBy(1)
        runCurrent()
        assertTrue(vm.state.value.results?.queryTooShort == true)
    }

    @Test
    fun `BR-SEARCH-001 searches all four groups and cancels intermediate queries`() = runViewModelTest {
        domain.repos.store.putTask(aTask(title = "Documentação"))
        domain.repos.store.putAppointment(anAppointment(title = "Documentação"))
        domain.repos.store.putMatter(aMatter(title = "Documentação"))
        domain.repos.store.putInboxItem(anInboxItem(text = "Documentação"))
        val vm = SearchViewModel(domain.search)
        vm.onAction(SearchAction.ChangeQuery("nothing"))
        advanceTimeBy(100)
        vm.onAction(SearchAction.ChangeQuery("documentacao"))
        advanceTimeBy(200)
        runCurrent()
        val results = assertNotNull(vm.state.value.results)
        assertEquals(1, results.tasks.size)
        assertEquals(1, results.appointments.size)
        assertEquals(1, results.matters.size)
        assertEquals(1, results.inboxItems.size)
        vm.onAction(SearchAction.ChangeQuery("absent"))
        advanceTimeBy(200)
        runCurrent()
        assertTrue(vm.state.value.results?.isEmpty == true)
    }

    @Test
    fun `search results open details and inbox results open Inbox`() = runViewModelTest {
        val vm = SearchViewModel(domain.search)
        vm.effects.test {
            vm.onAction(SearchAction.OpenTask(TaskId("t1")))
            assertEquals(SearchEffect.Navigate(TaskDetailKey("t1")), awaitItem())
            vm.onAction(SearchAction.OpenAppointment(AppointmentId("a1")))
            assertEquals(SearchEffect.Navigate(AppointmentDetailKey("a1")), awaitItem())
            vm.onAction(SearchAction.OpenMatter(MatterId("m1")))
            assertEquals(SearchEffect.Navigate(MatterDetailKey("m1")), awaitItem())
            vm.onAction(SearchAction.OpenInbox)
            assertEquals(SearchEffect.Navigate(InboxKey), awaitItem())
            vm.onAction(SearchAction.Close)
            assertEquals(SearchEffect.Close, awaitItem())
        }
    }

    @Test
    fun `search storage failures show a message`() = runViewModelTest {
        val failing = object : SearchRepository {
            override suspend fun search(terms: List<String>): SearchHits = error("Storage unavailable")
        }
        val vm = SearchViewModel(SearchUseCase(failing))
        vm.effects.test {
            vm.onAction(SearchAction.ChangeQuery("query"))
            advanceTimeBy(200)
            runCurrent()
            assertIs<SearchEffect.ShowMessage>(awaitItem())
        }
    }
}
