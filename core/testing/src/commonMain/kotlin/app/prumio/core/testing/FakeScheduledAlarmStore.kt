package app.prumio.core.testing

import app.prumio.core.domain.reminder.ScheduledAlarm
import app.prumio.core.domain.reminder.ScheduledAlarmStore

class FakeScheduledAlarmStore : ScheduledAlarmStore {
    var alarms: List<ScheduledAlarm> = emptyList()
    override suspend fun all() = alarms
    override suspend fun replaceAll(alarms: List<ScheduledAlarm>) {
        this.alarms = alarms
    }
}
