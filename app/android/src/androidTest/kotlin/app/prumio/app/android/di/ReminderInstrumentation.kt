package app.prumio.app.android.di

import android.app.Activity
import android.app.Instrumentation
import android.app.NotificationManager
import android.os.Bundle
import android.os.SystemClock
import app.prumio.core.common.Outcome
import app.prumio.core.domain.reminder.NotificationPresenter
import app.prumio.core.domain.reminder.ReconcileRemindersUseCase
import app.prumio.core.domain.settings.SettingKeys
import app.prumio.core.domain.settings.UpdateSettingUseCase
import app.prumio.core.domain.task.CreateTaskUseCase
import app.prumio.core.domain.task.DeleteTaskUseCase
import app.prumio.core.domain.task.ObserveTaskUseCase
import app.prumio.core.domain.task.TaskDraft
import app.prumio.core.model.TaskId
import app.prumio.core.model.TaskStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.context.GlobalContext
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

/** Physical-device schedule → real receiver → notification → Complete. Never clears app data. */
class ReminderInstrumentation : Instrumentation() {
    private var restorePermissionQa = false
    override fun onCreate(arguments: Bundle?) {
        restorePermissionQa = arguments?.getString("restorePermissionQa") == "true"
        super.onCreate(arguments)
        start()
    }
    override fun onStart() {
        // onStart runs on a new thread while Android is still creating the target Application.
        waitForIdleSync()
        val report = Bundle()
        var taskId: TaskId? = null
        var resultCode = Activity.RESULT_CANCELED
        val koin = GlobalContext.get()
        try {
            check(targetContext.packageName.endsWith(".debug")) { "Debug package required" }
            runBlocking {
                val local = (Clock.System.now() + 8.seconds).toLocalDateTime(TimeZone.currentSystemDefault())
                val created =
                    koin.get<CreateTaskUseCase>()(
                        TaskDraft("Prumio reminder instrumentation", dueDate = local.date, dueTime = local.time),
                    )
                val id = (created as Outcome.Success).value.id
                taskId = id
                check(koin.get<ReconcileRemindersUseCase>()(forceReschedule = true) is Outcome.Success)
                val manager = targetContext.getSystemService(NotificationManager::class.java)
                val deadline = SystemClock.elapsedRealtime() + 60_000
                var notification = manager.activeNotifications.firstOrNull { it.tag == "task:${id.value}" }
                while (notification == null && SystemClock.elapsedRealtime() < deadline) {
                    SystemClock.sleep(250)
                    notification = manager.activeNotifications.firstOrNull { it.tag == "task:${id.value}" }
                }
                checkNotNull(notification) { "Alarm did not produce a notification within 60 seconds" }
                check(notification.notification.actions.size == 2)
                val monitor = addMonitor("app.prumio.app.android.MainActivity", null, false)
                notification.notification.contentIntent.send()
                val opened = waitForMonitorWithTimeout(monitor, 5_000)
                check(opened != null) { "Notification content did not open MainActivity" }
                removeMonitor(monitor)
                notification.notification.actions[0].actionIntent.send()
                val completedDeadline = SystemClock.elapsedRealtime() + 10_000
                while (koin.get<ObserveTaskUseCase>()(id).first()?.status != TaskStatus.Completed &&
                    SystemClock.elapsedRealtime() < completedDeadline
                ) {
                    SystemClock.sleep(100)
                }
                check(koin.get<ObserveTaskUseCase>()(id).first()?.status == TaskStatus.Completed)
                check(manager.activeNotifications.none { it.tag == "task:${id.value}" })
            }
            report.putString("result", "PASS: real alarm, receiver, notification and Complete action")
            resultCode = Activity.RESULT_OK
        } catch (error: Throwable) {
            report.putString("result", "FAIL: ${error::class.simpleName}: ${error.message}")
        } finally {
            runBlocking {
                taskId?.let {
                    koin.get<DeleteTaskUseCase>()(it)
                    koin.get<NotificationPresenter>().cancelFor("task:${it.value}")
                }
                koin.get<ReconcileRemindersUseCase>()(forceReschedule = true)
                if (restorePermissionQa) {
                    val update = koin.get<UpdateSettingUseCase>()
                    update(SettingKeys.NotificationPermissionDenials, 0)
                    update(SettingKeys.NotificationBannerDismissed, false)
                    val manager = targetContext.getSystemService(NotificationManager::class.java)
                    val fixtures = setOf("P4-QA-permission-one", "P4-QA-permission-two", "P4-QA-permission-three")
                    manager.activeNotifications.filter {
                        it.notification.extras.getCharSequence("android.title")?.toString() in fixtures
                    }.forEach { manager.cancel(it.tag, it.id) }
                }
            }
        }
        finish(resultCode, report)
    }
}
