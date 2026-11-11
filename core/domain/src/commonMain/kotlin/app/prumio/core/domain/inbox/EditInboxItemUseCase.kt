package app.prumio.core.domain.inbox

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.map
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.notFound
import app.prumio.core.domain.error.storageCall
import app.prumio.core.model.InboxItem
import app.prumio.core.model.InboxItemId

/** Changes only the text of an inbox item. Rules: BR-INBOX-006, BR-INBOX-001, BR-GEN-004. */
class EditInboxItemUseCase(private val inbox: InboxRepository, private val time: TimeProvider) {
    suspend operator fun invoke(id: InboxItemId, text: String): Outcome<InboxItem, DomainError> {
        val normalized = inboxText(text).valueOr { return it.asFailure() }
        val item =
            storageCall { inbox.get(id) }.valueOr { return it.asFailure() } ?: return notFound("InboxItem", id.value)
        val edited = item.copy(text = normalized, updatedAt = time.now())
        return storageCall { inbox.save(edited) }.map { edited }
    }
}
