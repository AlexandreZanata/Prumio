package app.prumio.core.domain.error

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class DomainErrorTest {
    @Test
    fun `rule-based errors keep the rule id that caused them`() {
        val validation = DomainError.Validation(ruleId = "BR-TODAY-001", field = "title", reason = "blank")
        val transition = DomainError.InvalidTransition(ruleId = "BR-TODAY-001", from = "Open", to = "Open")

        assertEquals("BR-TODAY-001", validation.ruleId)
        assertEquals("BR-TODAY-001", transition.ruleId)
    }

    @Test
    fun `rule-based errors reject anything that is not a rule id`() {
        assertFailsWith<IllegalArgumentException> {
            DomainError.Validation(ruleId = "title", field = "title", reason = "x")
        }
        assertFailsWith<IllegalArgumentException> {
            DomainError.InvalidTransition(ruleId = "BR-1", from = "a", to = "b")
        }
    }

    @Test
    fun `other errors carry what the UI needs to explain them`() {
        val notFound: DomainError = DomainError.NotFound(entity = "Task", id = "42")
        val storage: DomainError = DomainError.Storage(cause = IllegalStateException("disk full"))

        assertIs<DomainError.NotFound>(notFound)
        assertEquals("disk full", (storage as DomainError.Storage).cause.message)
    }
}
