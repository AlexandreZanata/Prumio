package app.prumio.core.ui.message

import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.text.ValidationReasons
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.error_generic
import app.prumio.core.ui.resources.error_not_found
import app.prumio.core.ui.resources.error_storage
import app.prumio.core.ui.resources.validation_blank
import app.prumio.core.ui.resources.validation_too_long
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UiMessageTest {
    @Test
    fun `storage and missing items have their own message`() {
        assertEquals(Res.string.error_storage, DomainError.Storage(IllegalStateException()).toUiMessage().text)
        assertEquals(Res.string.error_not_found, DomainError.NotFound("Task", "task-1").toUiMessage().text)
    }

    @Test
    fun `validation errors use the field message`() {
        val error = DomainError.Validation("BR-TASK-001", "title", ValidationReasons.BLANK)
        assertEquals(Res.string.validation_blank, error.toUiMessage().text)
        assertEquals(
            Res.string.validation_too_long,
            DomainError.Validation("BR-TASK-001", "title", ValidationReasons.TOO_LONG).fieldMessage(),
        )
    }

    @Test
    fun `unknown reasons and invalid transitions fall back to the generic message`() {
        assertEquals(
            Res.string.error_generic,
            DomainError.Validation("BR-TASK-001", "title", "new_reason").fieldMessage(),
        )
        val transition = DomainError.InvalidTransition("BR-MATTER-002", from = "Resolved", to = "Waiting")
        assertEquals(Res.string.error_generic, transition.toUiMessage().text)
    }

    @Test
    fun `error messages offer no action`() {
        assertNull(DomainError.NotFound("Matter", "matter-1").toUiMessage().actionLabel)
    }
}
