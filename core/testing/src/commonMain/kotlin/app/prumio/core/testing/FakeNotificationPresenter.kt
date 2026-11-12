package app.prumio.core.testing

import app.prumio.core.domain.reminder.NotificationContent
import app.prumio.core.domain.reminder.NotificationPresenter

class FakeNotificationPresenter : NotificationPresenter {
    val shown = mutableListOf<NotificationContent>()
    val cancelled = mutableListOf<String>()
    override suspend fun show(content: NotificationContent) {
        shown += content
    }
    override suspend fun cancelFor(itemKey: String) {
        cancelled += itemKey
    }
}
