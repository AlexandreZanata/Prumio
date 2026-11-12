package app.prumio.core.testing

import app.prumio.core.domain.reminder.ReminderReconciliationTrigger

class FakeReminderReconciliationTrigger : ReminderReconciliationTrigger {
    var requests: Int = 0
    override fun request(forceReschedule: Boolean) {
        requests++
    }
}
