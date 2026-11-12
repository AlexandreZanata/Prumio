package app.prumio.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.prumio.core.notifications.di.reconcileReminders
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Restores alarms after OS resets, package/time/zone or exact-access changes. Rule: BR-REM-002. */
class ReconcileReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                reconcileReminders(forceReschedule = true)
            } finally {
                pending.finish()
            }
        }
    }
}
