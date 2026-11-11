package app.prumio.core.testing

import app.prumio.core.model.Appointment
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.InboxItem
import app.prumio.core.model.InboxItemId
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterEntry
import app.prumio.core.model.MatterEntryId
import app.prumio.core.model.MatterId
import app.prumio.core.model.Task
import app.prumio.core.model.TaskId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory "database" shared by the fake repositories, so cross-aggregate effects (matter deletion
 * unlinking tasks, conversions) behave like the SQLite adapters. Use [FakeRepositories] in tests.
 */
class FakeStore {
    val inboxItems = MutableStateFlow<Map<InboxItemId, InboxItem>>(emptyMap())
    val tasks = MutableStateFlow<Map<TaskId, Task>>(emptyMap())
    val appointments = MutableStateFlow<Map<AppointmentId, Appointment>>(emptyMap())
    val matters = MutableStateFlow<Map<MatterId, Matter>>(emptyMap())
    val matterEntries = MutableStateFlow<Map<MatterEntryId, MatterEntry>>(emptyMap())
    val settings = MutableStateFlow<Map<String, String>>(emptyMap())

    fun putTask(task: Task) = tasks.update { it + (task.id to task) }

    fun putMatter(matter: Matter) = matters.update { it + (matter.id to matter) }

    fun putEntries(entries: List<MatterEntry>) = matterEntries.update { current ->
        current +
            entries.associateBy { it.id }
    }
}

/** One coherent set of fakes over a single [FakeStore]. */
class FakeRepositories(val store: FakeStore = FakeStore()) {
    val inbox = FakeInboxRepository(store)
    val tasks = FakeTaskRepository(store)
    val appointments = FakeAppointmentRepository(store)
    val matters = FakeMatterRepository(store)
    val search = FakeSearchRepository(store)
    val settings = FakeSettingsRepository(store)
}
