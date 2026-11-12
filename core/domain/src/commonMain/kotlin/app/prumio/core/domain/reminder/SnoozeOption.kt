package app.prumio.core.domain.reminder

enum class SnoozeOption { TenMinutes, OneHour, Tomorrow }

/** BR-REM-008: follow-ups use Tomorrow only. */
fun snoozeOptions(key: AlarmKey): List<SnoozeOption> =
    if (key.value.startsWith("matter:")) listOf(SnoozeOption.Tomorrow) else SnoozeOption.entries
