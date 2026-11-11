package app.prumio.core.domain.inbox

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.map
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.notFound
import app.prumio.core.domain.error.storageCall
import app.prumio.core.model.InboxItem
import app.prumio.core.model.InboxItemId

/** Deletes an inbox item and returns it, so the UI can offer Undo. Rules: BR-INBOX-006, BR-GEN-005. */
class DiscardInboxItemUseCase(private val inbox: InboxRepository) {
    suspend operator fun invoke(id: InboxItemId): Outcome<InboxItem, DomainError> {
        val item =
            storageCall { inbox.get(id) }.valueOr { return it.asFailure() } ?: return notFound("InboxItem", id.value)
        return storageCall { inbox.delete(id) }.map { item }
    }
}
