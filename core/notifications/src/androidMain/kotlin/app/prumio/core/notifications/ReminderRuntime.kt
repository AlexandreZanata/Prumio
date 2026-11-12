package app.prumio.core.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import app.prumio.core.domain.reminder.ObserveReminderInputsUseCase
import app.prumio.core.domain.reminder.ReconcileRemindersUseCase
import app.prumio.core.domain.reminder.ReminderReconciliationTrigger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/** App-lifetime debounce and safety net. Domain owns the diff and every alarm decision. Rules: BR-REM-002, BR-SET-002. */
@OptIn(FlowPreview::class)
class ReminderRuntime(
    private val context: Context,
    private val observeInputs: ObserveReminderInputsUseCase,
    private val reconcile: ReconcileRemindersUseCase,
) : ReminderReconciliationTrigger {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val requests = Channel<Unit>(Channel.CONFLATED)
    private val force = AtomicBoolean(false)
    private val started = AtomicBoolean(false)
    fun start() {
        if (!started.compareAndSet(false, true)) return
        scope.launch { requests.receiveAsFlow().debounce(1000).collect { reconcile(force.getAndSet(false)) } }
        scope.launch { observeInputs().collect { request() } }
        WorkManager.getInstance(
            context,
        ).enqueueUniquePeriodicWork(
            "prumio-reminder-reconciliation",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<ReconcileWorker>(24, TimeUnit.HOURS).build(),
        )
        request(forceReschedule = true)
    }
    override fun request(forceReschedule: Boolean) {
        if (forceReschedule) force.set(true)
        requests.trySend(Unit)
    }
}
