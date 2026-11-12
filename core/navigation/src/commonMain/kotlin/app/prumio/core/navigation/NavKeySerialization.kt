package app.prumio.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * Registers every [NavKey] for back stack persistence (required on iOS, checked by architecture tests).
 * Adding a key? Add one `subclass(...)` line here.
 */
val navKeySerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(ReminderReliabilityKey::class)
        subclass(SnoozeKey::class)
        subclass(TodayKey::class)
        subclass(AgendaKey::class)
        subclass(MattersKey::class)
        subclass(InboxKey::class)
        subclass(CaptureKey::class)
        subclass(SearchKey::class)
        subclass(SettingsKey::class)
        subclass(TaskEditorKey::class)
        subclass(TaskDetailKey::class)
        subclass(AllTasksKey::class)
        subclass(AppointmentEditorKey::class)
        subclass(AppointmentDetailKey::class)
        subclass(MatterEditorKey::class)
        subclass(MatterDetailKey::class)
    }
}
