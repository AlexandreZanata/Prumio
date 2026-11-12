package app.prumio.core.domain.matter

import app.prumio.core.model.MatterId
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.assertSuccess
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RestoreMatterNoteUseCaseTest {
    @Test
    fun `BR-GEN-005 Undo restores the same note including its backdate`() = runTest {
        val domain = TestUseCases()
        domain.repos.store.putMatter(aMatter("m1"))
        val note = domain.addMatterNote(MatterId("m1"), "Called the office", LocalDate(2026, 1, 10)).assertSuccess()
        domain.deleteMatterNote(note.id).assertSuccess()
        assertNull(domain.repos.matters.getEntry(note.id))

        RestoreMatterNoteUseCase(domain.repos.matters, domain.ids, domain.time)(note).assertSuccess()

        assertEquals(note, domain.repos.matters.getEntry(note.id))
    }
}
