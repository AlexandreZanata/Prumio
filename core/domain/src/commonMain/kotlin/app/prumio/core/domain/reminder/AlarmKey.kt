package app.prumio.core.domain.reminder

/** Stable identity: kind, owner, anchor epoch minute, offset. Rule: BR-REM-009. */
data class AlarmKey(val value: String)
