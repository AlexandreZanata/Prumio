package app.prumio.core.domain.reminder

import app.prumio.core.common.onSuccess
import app.prumio.core.domain.task.CompleteTaskUseCase
import app.prumio.core.model.TaskId

/** Uses the normal task transition; ignoring/dismissing a notification never calls this. Rules: BR-REM-008, BR-TASK-003, BR-GEN-006. */
class CompleteTaskFromNotificationUseCase(
    private val completeTask: CompleteTaskUseCase,
    private val presenter: NotificationPresenter,
    private val trigger: ReminderReconciliationTrigger,
) {
    suspend operator fun invoke(id: TaskId) = completeTask(id).onSuccess {
        presenter.cancelFor("task:${id.value}")
        trigger.request()
    }
}
