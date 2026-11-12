package app.prumio.core.model

sealed interface ReminderOwner {
    val id: String
    data class Task(val taskId: TaskId) : ReminderOwner {
        override val id: String get() = taskId.value
    }
    data class Appointment(val appointmentId: AppointmentId) : ReminderOwner {
        override val id: String get() = appointmentId.value
    }
}
