package app.prumio.core.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import app.prumio.core.domain.reminder.NotificationAccess
import app.prumio.core.domain.reminder.NotificationAccessState

/** Platform facts for domain permission policy (BR-REM-013, BR-REM-014). */
class AndroidNotificationAccess(private val context: Context) : NotificationAccess {
    override fun status(): NotificationAccessState {
        val canAsk =
            Build.VERSION.SDK_INT >= 33 &&
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        val exact =
            Build.VERSION.SDK_INT < 31 || context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()
        return NotificationAccessState(
            context.getSystemService(NotificationManager::class.java).areNotificationsEnabled(),
            canAsk,
            exact,
            !context.getSystemService(PowerManager::class.java).isIgnoringBatteryOptimizations(context.packageName),
        )
    }
}
