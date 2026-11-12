package app.prumio.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.prumio.core.model.TaskId
import app.prumio.core.notifications.di.completeNotificationTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Explicit notification actions only; dismissing/ignoring has no handler (BR-GEN-006, BR-REM-008). */
class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != AndroidNotificationPresenter.ACTION_COMPLETE) return
        val id = intent.data?.lastPathSegment?.let(::TaskId) ?: return
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                completeNotificationTask(id)
            } finally {
                pending.finish()
            }
        }
    }
}
