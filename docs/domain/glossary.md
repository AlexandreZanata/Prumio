# Glossary — the ubiquitous language

One concept, one name, everywhere: in docs, code, tests, UI and commit messages. When a concept is
missing here, add it here first. Never introduce a synonym (an agent searching for `Matter` must
find every place that deals with matters).

| Concept | English UI | Brazilian Portuguese UI | Code name | Definition |
|---|---|---|---|---|
| Inbox item | Inbox item | Item da entrada | `InboxItem` | A captured thought not yet organized. Only text |
| Inbox | Inbox | Entrada | `inbox` (package), `InboxKey` | The list of inbox items |
| Capture | Add anything / Quick capture | Adicionar algo / Captura rápida | `capture` (feature), `CaptureKey` | Saving text to the inbox in seconds |
| Convert / Organize | Organize | Organizar | `ConvertInboxItemUseCase` | Turning an inbox item into a task, appointment or matter |
| Task | Task | Tarefa | `Task`, `TaskId` | A concrete action to do. Answers "what do I need to do?" |
| Due date / due time | Due | Prazo / Data / Horário | `dueDate: LocalDate?`, `dueTime: LocalTime?` | When a task should be done (floating) |
| Complete | Done / Complete | Concluir | `TaskStatus.Completed` | The task was done |
| Dismiss | Dismiss | Descartar | `TaskStatus.Dismissed` | The task no longer matters; kept for history |
| Reopen | Reopen | Reabrir | — | Back to open from completed/dismissed |
| Postpone | Postpone | Adiar | `PostponeTaskUseCase` | Move the due date (changes the task) |
| Snooze | Snooze | Soneca / Lembrar depois | `Snooze` | Delay a notification (does not change the item) |
| Overdue | Overdue | Atrasada | `isOverdue` | Open task whose due date-time has passed |
| Appointment | Appointment | Compromisso | `Appointment`, `AppointmentId` | Something with a date and time where you need to be. Answers "where do I need to be?" |
| Agenda | Agenda | Agenda | `agenda` (feature), `AgendaKey` | Day-by-day view of appointments and dated tasks |
| All-day | All day | Dia inteiro | `allDay` | Appointment without times, spanning whole days |
| Advance notice | Advance notice | Aviso antecipado | `Reminder.offsetMinutes > 0` | Reminder before the anchor ("1 hour before") |
| Matter | Matter | Pendência | `Matter`, `MatterId` | An ongoing life subject that needs resolving. Answers "what do I need to resolve?" |
| Matter status | Open · Waiting · Resolved | Em aberto · Aguardando · Resolvida | `MatterStatus` | Lifecycle of a matter |
| Waiting for | Waiting for | Aguardando quem/o quê | `waitingFor` | Who or what the matter is waiting on |
| Next action | Next action | Próxima ação | `Task` with `matterId` | The open linked task that moves the matter forward |
| Follow-up | Check again on | Verificar novamente em | `followUpDate`, `followUpTime` | When to look at the matter again |
| History | History | Histórico | `MatterEntry`, `MatterEntryKind` | Timeline of what happened to a matter |
| Note | Note | Anotação | `MatterEntryKind.Note` | User-written history entry |
| Needs attention | Needs attention | Merecem atenção | `MatterAttentionPolicy` | BR-MATTER-010 |
| Stalled | Stalled | Parada | — | BR-MATTER-011 |
| Reminder | Reminder | Lembrete | `Reminder`, `ReminderId` | A wish to be notified relative to an item's anchor |
| Anchor | — | — | `anchor` | The instant a reminder is relative to (BR-REM-004) |
| Alarm | — | — | `PlannedAlarm`, `AlarmKey` | One concrete scheduled notification computed by the planner |
| Reconciliation | — | — | `ReconcileRemindersUseCase` | Making scheduled alarms equal to the planned ones |
| Quiet hours | Quiet hours | Horário silencioso | `quietHours*` settings | Period where deferrable notifications wait |
| Morning summary | Morning summary | Resumo matinal | `morningSummary*` settings | Optional daily overview notification |
| Recurrence | Repeat | Repetir | `RecurrenceRule` | Rule that generates occurrences |
| Occurrence | Occurrence | Ocorrência | `Occurrence` | One instance of a recurring item |
| Weekly review | Weekly review | Revisão da semana | `review` (feature), `ReviewKey` | Calm weekly look at what is stuck or coming |
| Today | Today | Hoje | `today` (feature), `TodayKey` | The day-at-a-glance screen |
| Label | Label | Etiqueta | `Label` (phase 6) | Optional user-defined area ("Family", "Car") |
| Backup | Backup | Backup / Cópia de segurança | `backup` | Export/import of all data to a file |
| Day period | — | — | `DayPeriod` | Morning / Afternoon / Evening (greeting) |

## Words to avoid

| Avoid | Use instead | Why |
|---|---|---|
| Event (for calendar items) | Appointment | "Event" collides with UI events, analytics events and effects in code |
| Issue, pending, open loop, project | Matter | One name for the signature concept |
| Todo, item (for tasks) | Task | Precision |
| Archive | Dismiss (tasks) / Resolve (matters) | Prumio has no archive concept |
| Alert | Reminder (data) / Notification (delivery) | Two distinct layers |
| Due (for appointments) | Start / End | Appointments do not have due dates |
