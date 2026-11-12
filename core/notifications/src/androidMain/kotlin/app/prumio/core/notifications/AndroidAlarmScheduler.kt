package app.prumio.core.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import app.prumio.core.domain.reminder.AlarmKey
import app.prumio.core.domain.reminder.AlarmScheduler
import app.prumio.core.domain.reminder.PlannedAlarm

/** Executes the projection, with exact/inexact fallback only (BR-REM-013). */
class AndroidAlarmScheduler(private val context: Context) : AlarmScheduler {
    private val manager = context.getSystemService(AlarmManager::class.java)
    override fun canScheduleExact(): Boolean = Build.VERSION.SDK_INT < 31 || manager.canScheduleExactAlarms()
    override suspend fun schedule(alarm: PlannedAlarm, requestCode: Int) {
        val operation = pending(alarm.key, requestCode)
        val at = alarm.triggerAt.toEpochMilliseconds()
        if (canScheduleExact()) {
            try {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, operation)
                return
            } catch (
                _: SecurityException,
            ) { /* Permission may change after the check. */ }
        }
        manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, operation)
    }
    override suspend fun cancel(key: AlarmKey, requestCode: Int) {
        manager.cancel(pending(key, requestCode))
    }
    private fun pending(key: AlarmKey, code: Int): PendingIntent = PendingIntent.getBroadcast(
        context,
        code,
        Intent(context, AlarmReceiver::class.java).setData(Uri.parse("prumio://alarm/${key.value}")),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}
