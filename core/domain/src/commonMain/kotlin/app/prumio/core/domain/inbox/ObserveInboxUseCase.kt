package app.prumio.core.domain.inbox

import app.prumio.core.model.InboxItem
import kotlinx.coroutines.flow.Flow

/** Inbox items, newest first. Rule: BR-INBOX-003. */
class ObserveInboxUseCase(private val inbox: InboxRepository) {
    operator fun invoke(): Flow<List<InboxItem>> = inbox.observeAll()
}
