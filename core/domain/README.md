# `:core:domain`

> Every business rule of Prumio, in pure Kotlin: use cases, policies, engines and the ports
> (interfaces) the outer layers implement.

| | |
|---|---|
| **Layer** | Domain |
| **Plugin** | `prumio.kmp.library` |
| **Package** | `app.prumio.core.domain` |
| **May depend on** | `:core:common`, `:core:model` |
| **Used by** | `:core:data`, `:core:notifications`, `:core:testing`, `:feature:*`, `:app:shell` |

## Responsibility

- Use cases: one class per business operation (`CompleteTaskUseCase`), `operator fun invoke`.
- Ports: repository and platform interfaces (`TaskRepository`, `AlarmScheduler`), specified in
  [docs/architecture/contracts.md](../../docs/architecture/contracts.md).
- Pure engines: recurrence (docs/domain/recurrence.md), reminder planning
  (docs/domain/reminders-and-notifications.md), search normalization.
- `DomainError`: the expected-failure taxonomy (docs/architecture/error-handling.md).

## Package layout

One package per aggregate or capability:

```
app.prumio.core.domain
├── error/        DomainError, storageCall (storage failures → DomainError.Storage)
├── text/         TextLimits, ValidationReasons, requiredText/optionalText (BR-GEN-002)
├── inbox/        InboxRepository, ConversionTarget, capture/edit/discard/restore/convert/observe
├── task/         TaskRepository, TaskDraft, TaskWriter, TaskOverduePolicy, task use cases
├── appointment/  AppointmentRepository, AppointmentDraft/Schedule, AppointmentTimePolicy, use cases
├── matter/       MatterRepository, MatterDraft, FollowUp, MatterHistory, NextActionPolicy,
│                 MatterAttentionPolicy, status/notes/follow-up/overview/detail use cases
├── agenda/       ObserveAgendaUseCase (ordered AgendaDay/AgendaItem projection)
├── today/        GetDayPeriodUseCase, ObserveCurrentDateUseCase, ObserveTodayUseCase (TodaySnapshot)
├── search/       SearchRepository, SearchText (normalization), SearchUseCase, highlightRanges
├── settings/     SettingsRepository, SettingKey/SettingKeys, observe/update use cases
├── reminder/     (phase 4) planner, reconciler, scheduling ports
├── recurrence/   (phase 5) RRULE subset and occurrence engine
├── backup/       (phase 5)
└── di/           coreDomainModule (every use case, alphabetical)
```

Every port, use case and policy is listed in [docs/architecture/contracts.md](../../docs/architecture/contracts.md).

## Rules

- No Android, Compose, SQLDelight or `java.*` (architecture tests enforce it). Koin only in `di/`.
- Every rule enforced here cites its id in KDoc: `/** Rule: BR-TASK-003 */`.
- Use cases receive dependencies through the constructor and never hold state.
- Return `Outcome<T, DomainError>` for operations that can fail for expected reasons; expose `Flow`
  for observations.
- Time comes from `TimeProvider`, ids from `IdGenerator` — never from globals.

## How to extend

- New use case → [docs/ai/recipes/add-use-case.md](../../docs/ai/recipes/add-use-case.md)
- New repository port → [docs/ai/recipes/add-repository.md](../../docs/ai/recipes/add-repository.md)
- New business rule → [docs/ai/recipes/add-business-rule.md](../../docs/ai/recipes/add-business-rule.md)

## Tests

`./gradlew :core:domain:testAndroidHostTest`. Tests in `src/commonTest`, using fakes from
`:core:testing`. Test names start with the rule id they prove. Target coverage: ≥ 90 % lines.
