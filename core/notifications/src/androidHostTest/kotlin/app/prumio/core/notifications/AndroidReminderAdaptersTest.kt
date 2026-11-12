package app.prumio.core.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import app.prumio.core.domain.reminder.AlarmKey
import app.prumio.core.domain.reminder.AlarmKind
import app.prumio.core.domain.reminder.AlarmPriority
import app.prumio.core.domain.reminder.NotificationContent
import app.prumio.core.domain.reminder.PlannedAlarm
import app.prumio.core.testing.FakeTimeProvider
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlarmManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26, 36])
class AndroidReminderAdaptersTest {
    private val context: Context get() = RuntimeEnvironment.getApplication()
    private val time = FakeTimeProvider()
    private fun alarm(key: String = "task:t1:100:0", kind: AlarmKind = AlarmKind.Task) =
        PlannedAlarm(AlarmKey(key), time.now(), kind, "t1", time.now(), AlarmPriority.TimeCritical)

    @Test fun `BR-REM-013 exact alarms retain PendingIntent identity and cancellation`() = runTest {
        ShadowAlarmManager.setCanScheduleExactAlarms(true)
        val scheduler = AndroidAlarmScheduler(context)
        scheduler.schedule(alarm(), 42)
        scheduler.schedule(alarm(), 42)
        scheduler.schedule(alarm("task:t1:100:10"), 43)
        val manager = shadowOf(context.getSystemService(AlarmManager::class.java))
        assertEquals(2, manager.scheduledAlarms.size)
        assertEquals(
            "prumio://alarm/task:t1:100:0",
            shadowOf(manager.scheduledAlarms.first().operation).savedIntent.dataString,
        )
        scheduler.cancel(alarm().key, 42)
        assertEquals(1, manager.scheduledAlarms.size)
    }

    @Test
    @Config(sdk = [36])
    fun `BR-REM-013 denied exact permission uses an inexact idle alarm`() = runTest {
        ShadowAlarmManager.setCanScheduleExactAlarms(false)
        val scheduler = AndroidAlarmScheduler(context)
        assertTrue(!scheduler.canScheduleExact())
        scheduler.schedule(alarm(), 10)
        val scheduled = shadowOf(context.getSystemService(AlarmManager::class.java)).scheduledAlarms.single()
        assertTrue(scheduled.windowLengthMs != 0L)
    }

    @Test fun `BR-REM-015 BR-REM-016 channels actions deep links and per-item replacement`() = runTest {
        val launch = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(context.packageName)
        shadowOf(context.packageManager).addResolveInfoForIntent(
            launch,
            ResolveInfo().apply {
                activityInfo = ActivityInfo().apply {
                    packageName = context.packageName
                    name = "TestActivity"
                }
            },
        )
        val presenter = AndroidNotificationPresenter(context, time)
        presenter.show(NotificationContent(alarm(), "Call"))
        presenter.show(NotificationContent(alarm("task:t1:100:10"), "Call again"))
        val manager = context.getSystemService(NotificationManager::class.java)
        assertEquals(
            NotificationManager.IMPORTANCE_HIGH,
            manager.getNotificationChannel(AndroidNotificationPresenter.REMINDERS).importance,
        )
        assertEquals(
            NotificationManager.IMPORTANCE_DEFAULT,
            manager.getNotificationChannel(AndroidNotificationPresenter.FOLLOW_UPS).importance,
        )
        assertEquals(
            NotificationManager.IMPORTANCE_LOW,
            manager.getNotificationChannel(AndroidNotificationPresenter.SUMMARIES).importance,
        )
        val notification = manager.activeNotifications.single().notification
        assertEquals("Call again", notification.extras.getCharSequence("android.title"))
        assertEquals(2, notification.actions.size)
        assertEquals("prumio://task/t1", shadowOf(notification.contentIntent).savedIntent.dataString)
        assertEquals(
            AndroidNotificationPresenter.ACTION_COMPLETE,
            shadowOf(notification.actions[0].actionIntent).savedIntent.action,
        )
        assertEquals(
            "prumio://snooze/task:t1:100:10",
            shadowOf(notification.actions[1].actionIntent).savedIntent.dataString,
        )
        presenter.show(
            NotificationContent(alarm("matter:t1:100:0", AlarmKind.Matter), "Awaiting reply", waitingFor = "Landlord"),
        )
        assertEquals(2, manager.activeNotifications.size)
        presenter.cancelFor("task:t1")
        assertEquals(1, manager.activeNotifications.size)
    }
}
