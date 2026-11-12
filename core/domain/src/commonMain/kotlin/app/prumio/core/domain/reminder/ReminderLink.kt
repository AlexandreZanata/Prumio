package app.prumio.core.domain.reminder

data class ReminderLink(val kind: ReminderLinkKind, val id: String? = null, val missing: Boolean = false)
