package app.prumio.core.testing

import app.prumio.core.domain.reminder.ReminderRepository
import app.prumio.core.model.Reminder
import app.prumio.core.model.ReminderOwner
import kotlinx.coroutines.flow.combine

class FakeReminderRepository(private val store: FakeStore) : ReminderRepository {
    override fun observeAll() = combine(store.tasks, store.appointments) { tasks, appointments ->
        tasks.values.flatMap { it.reminders } +
            appointments.values.flatMap { it.reminders }
    }
    override suspend fun replaceFor(owner: ReminderOwner, reminders: List<Reminder>) {
        when (owner) {
            is ReminderOwner.Task -> store.tasks.value[owner.taskId]?.let {
                store.putTask(it.copy(reminders = reminders))
            }

            is ReminderOwner.Appointment -> store.appointments.value[owner.appointmentId]?.let {
                store.putAppointment(it.copy(reminders = reminders))
            }
        }
    }
}
