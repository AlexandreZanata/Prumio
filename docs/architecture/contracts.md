# Contracts (domain ports)

The interfaces through which the domain talks to the outside world. They are declared in
`:core:domain` (package per aggregate), implemented in `:core:data` or `:core:notifications`, and
faked in `:core:testing`. This page is the specification; the code must match it.

Conventions for every port:
- Reads are `Flow` (live, re-emitting on change) or `suspend` one-shot (`get…`) when a snapshot is enough.
- Writes are `suspend`, atomic (one transaction), and return `Outcome<Unit, DomainError>` when a
  storage failure must be reported; otherwise they throw only programming errors.
- Ports never validate business rules (use cases do) and never decide (policies do).
- Fakes must behave like the real adapter, including emitting on every `Flow` after a write.

Status: sections marked *implemented* exist in code; the others are specifications for their phase. Signatures may gain parameters during
implementation; update this page in the same change.

## Inbox (implemented, phase 1)

`app.prumio.core.domain.inbox` — adapter `SqlDelightInboxRepository`, fake `FakeInboxRepository`.

```kotlin
interface InboxRepository {
    fun observeAll(): Flow<List<InboxItem>>                       // newest first (BR-INBOX-003)
    fun observeCount(): Flow<Int>                                 // badge (BR-INBOX-005)
    suspend fun get(id: InboxItemId): InboxItem?
    suspend fun save(item: InboxItem)                             // insert or replace
    suspend fun delete(id: InboxItemId)
    suspend fun convert(id: InboxItemId, target: ConversionTarget) // target + delete, one transaction (BR-INBOX-004)
}

sealed interface ConversionTarget {
    data class ToTask(val task: Task) : ConversionTarget
    data class ToAppointment(val appointment: Appointment) : ConversionTarget
    data class ToMatter(val matter: Matter, val created: MatterEntry) : ConversionTarget
}
```

## Tasks (implemented, phase 1; completions phase 5)

`app.prumio.core.domain.task` — adapter `SqlDelightTaskRepository`, fake `FakeTaskRepository`.
Dated lists are ordered by due date, due time, creation. Writes that also change a matter's history
go through `MatterRepository.save(matter, entries, tasks)` so they stay atomic (the domain's
`TaskWriter` does this).

```kotlin
interface TaskRepository {
    fun observe(id: TaskId): Flow<Task?>
    fun observeOpen(): Flow<List<Task>>
    fun observeOpenDueOnOrBefore(date: LocalDate): Flow<List<Task>>   // Today: overdue + due today
    fun observeOpenDueBetween(from: LocalDate, to: LocalDate): Flow<List<Task>>
    fun observeOpenUndated(): Flow<List<Task>>                         // newest first
    fun observeCompletedSince(instant: Instant): Flow<List<Task>>      // most recent first
    fun observeDismissed(): Flow<List<Task>>                           // most recent first
    fun observeByMatter(matterId: MatterId): Flow<List<Task>>
    suspend fun get(id: TaskId): Task?
    suspend fun getByMatter(matterId: MatterId): List<Task>
    suspend fun save(task: Task)                                       // insert or replace
    suspend fun delete(id: TaskId)
    // phase 5: recordCompletion / removeCompletion (BR-REC-007)
}
```

## Appointments (implemented, phase 1; exceptions phase 5)

`app.prumio.core.domain.appointment` — adapter `SqlDelightAppointmentRepository`, fake `FakeAppointmentRepository`.

```kotlin
interface AppointmentRepository {
    fun observe(id: AppointmentId): Flow<Appointment?>
    /** Stored local dates overlap from..to; callers widen by one day and filter in the domain. */
    fun observeOverlapping(from: LocalDate, to: LocalDate): Flow<List<Appointment>>
    suspend fun get(id: AppointmentId): Appointment?
    suspend fun save(appointment: Appointment)
    suspend fun delete(id: AppointmentId)
}
```

## Matters (implemented, phase 1)

`app.prumio.core.domain.matter` — adapter `SqlDelightMatterRepository`, fake `FakeMatterRepository`.

```kotlin
interface MatterRepository {
    fun observe(id: MatterId): Flow<Matter?>
    fun observeUnresolved(): Flow<List<Matter>>                        // most recent activity first
    fun observeResolvedSince(instant: Instant): Flow<List<Matter>>     // most recently resolved first
    fun observeHistory(id: MatterId): Flow<List<MatterEntry>>          // newest first
    suspend fun get(id: MatterId): Matter?
    suspend fun getEntry(id: MatterEntryId): MatterEntry?
    /** Upserts the matter, its entries and tasks in ONE transaction (BR-MATTER-003). */
    suspend fun save(matter: Matter, entries: List<MatterEntry> = emptyList(), tasks: List<Task> = emptyList())
    suspend fun deleteEntry(matter: Matter, entryId: MatterEntryId)    // delete + save matter, one transaction
    suspend fun delete(id: MatterId)                                   // cascades history; unlinks tasks/appointments
}
```

## Search (implemented, phase 1)

`app.prumio.core.domain.search` — adapter `SqlDelightSearchRepository`, fake `FakeSearchRepository`.

```kotlin
interface SearchRepository {
    /** Items whose searchable text contains EVERY normalized term (BR-SEARCH-002); unordered. */
    suspend fun search(terms: List<String>): SearchHits
}
data class SearchHits(val tasks: List<Task>, val appointments: List<Appointment>, val matters: List<Matter>, val inboxItems: List<InboxItem>)
```

Normalization is domain code shared by adapters and fakes (`SearchText.kt`): `normalizeForSearch`
(lowercase, Latin diacritics removed, whitespace collapsed), `searchTerms(query)`, and
`searchableText()` per type (what is searchable: BR-SEARCH-001). The data layer stores it in
`search_text` columns on every write; matter notes are searchable through `matter_entry.search_text`.

## Settings (implemented, phase 1)

`app.prumio.core.domain.settings` — adapter `SqlDelightSettingsRepository`, fake `FakeSettingsRepository`.

```kotlin
interface SettingsRepository {                      // raw key-value store; typing is a domain rule
    fun observeValues(): Flow<Map<String, String>>
    suspend fun setValue(key: String, value: String?, updatedAt: Instant)   // null removes the key
}

class SettingKey<T>(val name: String, val default: T, val encode: (T) -> String?, val decode: (String) -> T?)
object SettingKeys { Theme; WeekStart; fun read(values): Settings }       // registry with defaults (BR-SET-001)
```

Adding a setting = one `SettingKey` + one `Settings` field ([recipe](../ai/recipes/add-setting.md)); no migration.

## Use cases (implemented, phase 1)

All registered in `coreDomainModule`. Writes return `Outcome<T, DomainError>`; observations return `Flow`.

| Area | Use cases |
|---|---|
| Inbox | `CaptureInboxItemUseCase`, `EditInboxItemUseCase`, `DiscardInboxItemUseCase`, `RestoreInboxItemUseCase`, `ConvertInboxItemUseCase`, `ObserveInboxUseCase`, `ObserveInboxCountUseCase` |
| Tasks | `CreateTaskUseCase`, `UpdateTaskUseCase`, `CompleteTaskUseCase`, `DismissTaskUseCase` (both return `ClosedTask` with `matterNeedsNextAction`), `ReopenTaskUseCase`, `PostponeTaskUseCase`, `LinkTaskToMatterUseCase`, `DeleteTaskUseCase`, `RestoreTaskUseCase`, `ObserveTaskUseCase`, `ObserveTaskListUseCase` |
| Appointments | `CreateAppointmentUseCase`, `UpdateAppointmentUseCase`, `DeleteAppointmentUseCase`, `RestoreAppointmentUseCase`, `ObserveAppointmentUseCase`, `ObserveAppointmentsUseCase` (per day) |
| Matters | `CreateMatterUseCase`, `UpdateMatterUseCase`, `WaitOnMatterUseCase`, `ResumeMatterUseCase`, `ResolveMatterUseCase`, `ReopenMatterUseCase`, `SetMatterFollowUpUseCase`, `AddMatterNoteUseCase`, `EditMatterNoteUseCase`, `DeleteMatterNoteUseCase`, `DeleteMatterUseCase`, `RestoreMatterUseCase`, `ObserveMattersUseCase`, `ObserveMatterDetailUseCase` |
| Today | `GetDayPeriodUseCase`, `ObserveTodayUseCase` (`TodaySnapshot`) |
| Search / Settings | `SearchUseCase`, `ObserveSettingsUseCase`, `UpdateSettingUseCase` |

Pure policies (no I/O): `TaskOverduePolicy` (BR-TASK-004), `AppointmentTimePolicy` (BR-APPT-005),
`NextActionPolicy` (BR-MATTER-005), `MatterAttentionPolicy` (BR-MATTER-010), draft validation
(`TaskDraft`, `AppointmentDraft`, `MatterDraft`, `TextRules`).

## Reminders (phase 4)

```kotlin
interface ReminderRepository {
    fun observeAll(): Flow<List<Reminder>>
    suspend fun replaceFor(owner: ReminderOwner, offsets: Set<Int>)   // one transaction
}

interface AlarmScheduler {                                            // :core:notifications
    fun canScheduleExact(): Boolean
    suspend fun schedule(alarm: PlannedAlarm, requestCode: Int)
    suspend fun cancel(key: AlarmKey, requestCode: Int)
}

interface NotificationPresenter {                                     // :core:notifications
    suspend fun show(content: NotificationContent)
    suspend fun cancelFor(itemKey: String)
    fun areNotificationsEnabled(): Boolean
}

interface ScheduledAlarmStore {                                       // :core:data
    suspend fun all(): List<ScheduledAlarm>
    suspend fun replaceAll(alarms: List<ScheduledAlarm>)
}

interface SnoozeStore {
    suspend fun all(): Map<AlarmKey, Instant>
    suspend fun put(key: AlarmKey, until: Instant)
    suspend fun remove(key: AlarmKey)
}

interface DeliveredAlarmStore {
    suspend fun recent(since: Instant): Set<AlarmKey>
    suspend fun markDelivered(key: AlarmKey, at: Instant)
    suspend fun pruneBefore(instant: Instant)
}

/** Requests a reconciliation from anywhere (receivers, workers, settings changes). Debounced. */
interface ReminderReconciliationTrigger {
    fun request()
}
```

## Backup (phase 5)

```kotlin
interface BackupRepository {
    suspend fun snapshot(): BackupData                                // all user data, one read transaction
    suspend fun replaceAll(data: BackupData)                          // one write transaction (BR-BACKUP-003)
}

interface BackupFileGateway {                                         // platform: Storage Access Framework
    suspend fun write(destination: FileLocation, bytes: ByteArray): Outcome<Unit, DomainError>
    suspend fun read(source: FileLocation): Outcome<ByteArray, DomainError>
}
```

`BackupCodec` (domain, pure): `encode(BackupData): String`, `decode(String): Outcome<BackupData, BackupError>`.

## Platform services

```kotlin
interface TimeProvider { fun now(): Instant; fun timeZone(): TimeZone }       // :core:common (exists)
fun interface IdGenerator { fun newId(): String }                              // :core:common (exists)
interface DispatcherProvider { val io: CoroutineDispatcher; val default: CoroutineDispatcher } // exists
```
