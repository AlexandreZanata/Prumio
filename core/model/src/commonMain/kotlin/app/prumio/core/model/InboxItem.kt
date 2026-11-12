package app.prumio.core.model

import kotlin.time.Instant

/** A captured thought not yet organized (docs/domain/model.md#inboxitem). */
data class InboxItem(val id: InboxItemId, val text: String, val createdAt: Instant, val updatedAt: Instant)
