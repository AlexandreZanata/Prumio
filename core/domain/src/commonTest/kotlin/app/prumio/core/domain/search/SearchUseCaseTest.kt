package app.prumio.core.domain.search

import app.prumio.core.domain.error.DomainError
import app.prumio.core.model.MatterStatus
import app.prumio.core.model.TaskStatus
import app.prumio.core.testing.FakeRepositories
import app.prumio.core.testing.TestInstant
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aMatterEntry
import app.prumio.core.testing.aTask
import app.prumio.core.testing.anAppointment
import app.prumio.core.testing.anInboxItem
import app.prumio.core.testing.assertFailure
import app.prumio.core.testing.assertSuccess
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours

class SearchUseCaseTest {
    private val repos = FakeRepositories()
    private val search = SearchUseCase(repos.search)

    @Test
    fun `BR-SEARCH-001 BR-SEARCH-002 every type is searched without accents or case`() = runTest {
        repos.tasks.save(aTask(id = "t1", title = "Documentação do carro"))
        repos.appointments.save(anAppointment(id = "a1", title = "Detran", location = "Documentação"))
        repos.matters.save(
            aMatter(id = "m1", title = "Moto"),
            listOf(aMatterEntry(matterId = "m1", text = "Documentação pronta")),
        )
        repos.inbox.save(anInboxItem(id = "i1", text = "Ver DOCUMENTACAO"))

        val results = search("documentacao").assertSuccess()

        assertEquals(listOf("t1"), results.tasks.map { it.id.value })
        assertEquals(listOf("a1"), results.appointments.map { it.id.value })
        assertEquals(listOf("m1"), results.matters.map { it.id.value })
        assertEquals(listOf("i1"), results.inboxItems.map { it.id.value })
        assertTrue(search("documentacao zebra").assertSuccess().isEmpty)
    }

    @Test
    fun `BR-SEARCH-003 open items come first, then the most recently updated`() = runTest {
        repos.tasks.save(
            aTask(
                id = "done",
                title = "Pay rent",
                status = TaskStatus.Completed,
                createdAt =
                    TestInstant + 2.hours,
            ),
        )
        repos.tasks.save(aTask(id = "old", title = "Pay rent", createdAt = TestInstant))
        repos.tasks.save(aTask(id = "new", title = "Pay rent", createdAt = TestInstant + 1.hours))
        repos.matters.save(aMatter(id = "closed", title = "Rent", status = MatterStatus.Resolved))
        repos.matters.save(aMatter(id = "active", title = "Rent"))

        val results = search("rent").assertSuccess()

        assertEquals(listOf("new", "old", "done"), results.tasks.map { it.id.value })
        assertEquals(listOf("active", "closed"), results.matters.map { it.id.value })
    }

    @Test
    fun `BR-SEARCH-004 queries shorter than two characters do not search`() = runTest {
        repos.tasks.save(aTask(title = "a"))

        assertTrue(search(" a ").assertSuccess().queryTooShort)
        assertTrue(search("").assertSuccess().isEmpty)
    }

    @Test
    fun `storage failures become a Storage error`() = runTest {
        val failing = SearchUseCase(
            object : SearchRepository {
                override suspend fun search(terms: List<String>): SearchHits = error("disk unavailable")
            },
        )

        assertIs<DomainError.Storage>(failing("anything").assertFailure())
    }
}
