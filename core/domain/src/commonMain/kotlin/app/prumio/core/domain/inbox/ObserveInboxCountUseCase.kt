package app.prumio.core.domain.inbox

import kotlinx.coroutines.flow.Flow

/** Number of inbox items, for the tab badge and Today's nudge (BR-INBOX-005, BR-TODAY-006). */
class ObserveInboxCountUseCase(private val inbox: InboxRepository) {
    operator fun invoke(): Flow<Int> = inbox.observeCount()
}
