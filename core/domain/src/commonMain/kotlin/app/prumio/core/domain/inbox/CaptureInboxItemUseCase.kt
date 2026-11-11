package app.prumio.core.domain.inbox

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.id.IdGenerator
import app.prumio.core.common.map
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.text.TextLimits
import app.prumio.core.domain.text.requiredText
import app.prumio.core.model.InboxItem
import app.prumio.core.model.InboxItemId

/** Saves a thought to the inbox with nothing but its text. Rules: BR-INBOX-001, BR-GEN-001, BR-GEN-003, BR-GEN-004. */
class CaptureInboxItemUseCase(
    private val inbox: InboxRepository,
    private val ids: IdGenerator,
    private val time: TimeProvider,
) {
    suspend operator fun invoke(text: String): Outcome<InboxItem, DomainError> {
        val normalized = inboxText(text).valueOr { return it.asFailure() }
        val now = time.now()
        val item = InboxItem(id = InboxItemId(ids.newId()), text = normalized, createdAt = now, updatedAt = now)
        return storageCall { inbox.save(item) }.map { item }
    }
}

/** Inbox text rule shared by capture and edit (BR-INBOX-001). */
internal fun inboxText(text: String) =
    requiredText(text, field = "text", max = TextLimits.INBOX_TEXT, ruleId = "BR-INBOX-001")
