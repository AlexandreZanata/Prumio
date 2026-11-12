package app.prumio.core.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.prumio.core.common.Outcome
import app.prumio.core.notifications.di.reconcileReminders

/** Daily, unconstrained, offline reconciliation (BR-REM-002). */
class ReconcileWorker(context: Context, parameters: WorkerParameters) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result = when (reconcileReminders(forceReschedule = true)) {
        is Outcome.Success -> Result.success()
        is Outcome.Failure -> Result.retry()
    }
}
