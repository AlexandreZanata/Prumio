package app.prumio.core.domain.reminder

import kotlin.time.Instant

data class ScheduledAlarm(val key: AlarmKey, val triggerAt: Instant, val requestCode: Int)
