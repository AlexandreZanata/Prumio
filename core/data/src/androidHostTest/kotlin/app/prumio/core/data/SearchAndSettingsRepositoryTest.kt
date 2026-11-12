package app.prumio.core.data

import app.cash.turbine.test
import app.prumio.core.data.appointment.SqlDelightAppointmentRepository
import app.prumio.core.data.inbox.SqlDelightInboxRepository
import app.prumio.core.data.matter.SqlDelightMatterRepository
import app.prumio.core.data.search.SqlDelightSearchRepository
import app.prumio.core.data.settings.SqlDelightSettingsRepository
import app.prumio.core.data.task.SqlDelightTaskRepository
import app.prumio.core.domain.search.searchTerms
import app.prumio.core.testing.TestInstant
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aMatterEntry
import app.prumio.core.testing.aTask
import app.prumio.core.testing.anAppointment
import app.prumio.core.testing.anInboxItem
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchAndSettingsRepositoryTest : RepositoryTestBase() {
    private val search = SqlDelightSearchRepository(database, dispatchers)
    private val settings = SqlDelightSettingsRepository(database, dispatchers)

    @Test
    fun `BR-SEARCH-002 every term must match, ignoring case and accents`() = runTest {
        SqlDelightTaskRepository(
            database,
            dispatchers,
        ).save(aTask(id = "t1", title = "Pagar documentação", notes = "Veículo"))
        SqlDelightTaskRepository(database, dispatchers).save(aTask(id = "t2", title = "Documentação da casa"))
        SqlDelightAppointmentRepository(
            database,
            dispatchers,
        ).save(anAppointment(id = "a1", title = "Detran", location = "Centro"))
        SqlDelightInboxRepository(database, dispatchers).save(anInboxItem(id = "i1", text = "Trocar óleo do veículo"))
        SqlDelightMatterRepository(database, dispatchers).save(
            aMatter(id = "m1", title = "Moto"),
            entries = listOf(aMatterEntry(id = "n1", matterId = "m1", text = "Documentação enviada")),
        )

        val hits = search.search(searchTerms("DOCUMENTACAO veiculo"))

        assertEquals(listOf("t1"), hits.tasks.map { it.id.value })
        assertTrue(hits.appointments.isEmpty())
        assertEquals(listOf("m1"), search.search(searchTerms("documentacao")).matters.map { it.id.value })
        assertEquals(listOf("a1"), search.search(searchTerms("centro detran")).appointments.map { it.id.value })
        assertEquals(listOf("i1"), search.search(searchTerms("oleo")).inboxItems.map { it.id.value })
        assertTrue(search.search(emptyList()).tasks.isEmpty())
    }

    @Test
    fun `settings are stored, replaced and removed`() = runTest {
        settings.observeValues().test {
            assertEquals(emptyMap(), awaitItem())
            settings.setValue("theme", "Dark", TestInstant)
            assertEquals(mapOf("theme" to "Dark"), awaitItem())
            settings.setValue("theme", null, TestInstant)
            assertEquals(emptyMap(), awaitItem())
        }
    }
}
