package app.prumio.core.testing

import app.prumio.core.domain.reminder.AlarmKey
import app.prumio.core.domain.reminder.AlarmScheduler
import app.prumio.core.domain.reminder.PlannedAlarm

class FakeAlarmScheduler : AlarmScheduler {
    var exact: Boolean = true
    val scheduled = mutableListOf<Pair<PlannedAlarm, Int>>()
    val cancelled = mutableListOf<AlarmKey>()
    override fun canScheduleExact() = exact
    override suspend fun schedule(alarm: PlannedAlarm, requestCode: Int) {
        scheduled += alarm to requestCode
    }
    override suspend fun cancel(key: AlarmKey, requestCode: Int) {
        cancelled += key
    }
}
