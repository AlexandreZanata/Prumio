package app.prumio.core.domain.reminder

import kotlinx.coroutines.sync.Mutex

/** App-wide serialization for planning and delivery; shared by every receiver/use-case instance. */
class ReminderCoordinator {
    internal val reconciliation = Mutex()
    internal val delivery = Mutex()
}
