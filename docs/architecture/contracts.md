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

Status: ports below are implemented in the phase indicated. Signatures may gain parameters during
implementation; update this page in the same change.

## Inbox (phase 1)

```kotlin
interface InboxRepository {
    fun observeAll(): Flow<List<InboxItem>>                      // newest first (BR-INBOX-003)
    fun observeCount(): Flow<Int>                                // badge (BR-INBOX-005)
    suspend fun get(id: InboxItemId): InboxItem?
    suspend fun insert(item: InboxItem)
    suspend fun update(item: InboxItem)
    suspend fun delete(id: InboxItemId)
    /** Atomically inserts [target] and deletes the item (BR-INBOX-004). */
    suspend fun convert(id: InboxItemId, target: ConversionTarget)
}

sealed interface ConversionTarget {
    data class ToTask(val task: Task) : ConversionTarget
    data class ToAppointment(val appointment: Appointment) : ConversionTarget
    data class ToMatter(val matter: Matter, val created: MatterEntry) : ConversionTarget
}
```

## Tasks (phase 1; completions phase 5)

```kotlin
interface TaskRepository {
    fun observe(id: TaskId): Flow<Task?>
    fun observeOpenDueOnOrBefore(date: LocalDate): Flow<List<Task>>   // Today: due today + overdue candidates
    fun observeOpenUndated(): Flow<List<Task>>                       // Anytime
    fun observeOpenDueBetween(from: LocalDate, to: LocalDate): Flow<List<Task>>   // Agenda, Upcoming
    fun observeCompletedSince(instant: Instant): Flow<List<Task>>
    fun observeByMatter(matterId: MatterId): Flow<List<Task>>
    suspend fun get(id: TaskId): Task?
    suspend fun upsert(task: Task)
    /** Saves [task] and appends [entry] to its matter's history in one transaction (BR-MATTER-003). */
    suspend fun upsertWithMatterEntry(task: Task, entry: MatterEntry?)
    suspend fun delete(id: TaskId)
    suspend fun recordCompletion(completion: TaskCompletion)          // phase 5 (BR-REC-007)
    suspend fun removeCompletion(id: String)                          // phase 5
}
```

## Appointments (phase 1; exceptions phase 5)

```kotlin
interface AppointmentRepository {
    fun observe(id: AppointmentId): Flow<Appointment?>
    /** Appointments whose stored local dates overlap [from]..[to] widened by one day (time-semantics.md). */
    fun observeOverlapping(from: LocalDate, to: LocalDate): Flow<List<Appointment>>
    fun observeRecurring(): Flow<List<Appointment>>                   // series, expanded by the domain
    suspend fun get(id: AppointmentId): Appointment?
    suspend fun upsert(appointment: Appointment)
    suspend fun delete(id: AppointmentId)
}
```

## Matters (phase 1)

```kotlin
interface MatterRepository {
    fun observe(id: MatterId): Flow<Matter?>
    fun observeUnresolved(): Flow<List<Matter>>
    fun observeResolvedSince(instant: Instant): Flow<List<Matter>>
    fun observeHistory(id: MatterId): Flow<List<MatterEntry>>          // newest first
    suspend fun get(id: MatterId): Matter?
    /** Saves the matter and appends [entries] atomically (state changes, follow-ups: BR-MATTER-003). */
    suspend fun save(matter: Matter, entries: List<MatterEntry>)
    /** Resolve with the chosen fate of open actions, atomically (BR-MATTER-007). */
    suspend fun resolve(matter: Matter, entries: List<MatterEntry>, tasksToUpdate: List<Task>)
    suspend fun updateNote(entry: MatterEntry)
    suspend fun deleteNote(id: MatterEntryId)
    suspend fun delete(id: MatterId)                                  // cascades history; unlinks tasks
}
```

## Search (phase 1)

```kotlin
interface SearchRepository {
    /** Items whose normalized searchable text contains every normalized term (BR-SEARCH-002). */
    suspend fun search(terms: List<String>, limitPerType: Int = 50): SearchResults
}
```

Normalization (`SearchTextNormalizer`, pure, in domain): lowercase + remove diacritics + collapse
whitespace. Stored in `search_text` columns maintained by the data layer on every write.

## Settings (phase 1)

```kotlin
interface SettingsRepository {
    fun observe(): Flow<Settings>                                     // typed, with defaults (BR-SET-001)
    suspend fun get(): Settings
    suspend fun <T> set(key: SettingKey<T>, value: T)
}
```

`SettingKey<T>` is a typed registry in `:core:domain/settings` (name, default, codec). Adding a
setting = adding one `SettingKey` ([recipe](../ai/recipes/add-setting.md)); no migration needed.

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
