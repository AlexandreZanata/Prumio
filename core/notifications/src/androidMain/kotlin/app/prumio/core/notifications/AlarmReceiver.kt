package app.prumio.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.prumio.core.domain.reminder.AlarmKey
import app.prumio.core.notifications.di.deliverAlarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Delegates delivery; asynchronous receiver lifetime is always finished (BR-REM-002). */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val key = intent.data?.pathSegments?.singleOrNull()?.let(::AlarmKey) ?: return
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                deliverAlarm(key)
            } finally {
                pending.finish()
            }
        }
    }
}
