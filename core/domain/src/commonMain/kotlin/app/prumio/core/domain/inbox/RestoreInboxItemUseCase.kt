package app.prumio.core.domain.inbox

import app.prumio.core.common.Outcome
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.storageCall
import app.prumio.core.model.InboxItem

/** Puts a discarded item back exactly as it was (Undo). Rule: BR-GEN-005. */
class RestoreInboxItemUseCase(private val inbox: InboxRepository) {
    suspend operator fun invoke(item: InboxItem): Outcome<Unit, DomainError> = storageCall { inbox.save(item) }
}
