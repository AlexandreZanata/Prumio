package app.prumio.core.testing

import app.prumio.core.domain.reminder.NotificationAccess
import app.prumio.core.domain.reminder.NotificationAccessState

class FakeNotificationAccess : NotificationAccess {
    var current = NotificationAccessState(true, false, true, false)
    override fun status() = current
}
