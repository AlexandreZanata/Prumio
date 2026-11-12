# Navigation

Navigation 3 (multiplatform build by JetBrains): the back stack is a plain list of keys that the
app owns. Keys live in `:core:navigation`, entries in features, the host and the back stack logic
in `:app:shell`.

## Keys

| Key | Kind | Provided by | Phase |
|---|---|---|---|
| `TodayKey` | top-level | `:feature:today` | 0 ✅ |
| `AgendaKey` | top-level | `:feature:agenda` | 0 ✅ |
| `MattersKey` | top-level | `:feature:matters` | 0 ✅ |
| `InboxKey` | top-level | `:feature:inbox` | 0 ✅ |
| `CaptureKey` | overlay (dialog) | `:feature:capture` | 0 ✅ |
| `SearchKey` | screen | `:feature:search` | 0 ✅ |
| `SettingsKey` | screen | `:feature:settings` | 0 ✅ |
| `TaskEditorKey(taskId: String?, matterId: String?)` | screen | `:feature:tasks` | 3 |
| `TaskDetailKey(taskId: String)` | screen | `:feature:tasks` | 3 |
| `AllTasksKey(filter: String?)` | screen | `:feature:tasks` | 3 |
| `AppointmentEditorKey(appointmentId: String?, date: String?)` | screen | `:feature:agenda` | 3 |
| `AppointmentDetailKey(appointmentId: String)` | screen | `:feature:agenda` | 3 |
| `MatterDetailKey(matterId: String, promptNextAction: Boolean = false)` | screen | `:feature:matters` | 3 |
| `MatterEditorKey(matterId: String?)` | screen | `:feature:matters` | 3 |
| `ReminderReliabilityKey`, `BackupKey` | screen | `:feature:settings` | 4–5 |
| `ReviewKey` | screen | `:feature:review` | 6 |

Rules:
- Keys are `@Serializable`, carry only primitives (ids as `String`, dates as ISO `String`) and are
  registered in `navKeySerializersModule` 🔒.
- A key with arguments is a `data class`; without, a `data object`.
- `TopLevelNavKey` marks bottom-bar destinations; `OverlayNavKey` marks dialogs (rendered by
  `DialogSceneStrategy`, entry metadata `DialogSceneStrategy.dialog()`).

## Back stack rules

Implemented only in `PrumioNavigator` (`:app:shell`) and covered by `PrumioNavigatorTest`:

1. The stack always starts with `TodayKey` and is never empty.
2. Navigating to a top-level key resets the stack to `[Today]` or `[Today, key]` (switching tabs
   does not keep per-tab history; back from any tab goes to Today).
3. Any other key is pushed, unless it equals the current top (no duplicates from double taps).
4. Back pops one entry; on `[Today]` the system handles back (app goes to background).
5. The bottom bar and "Add anything" are shown when the top-most non-overlay key is top-level.

## How a feature navigates

1. The Screen emits an action (`OpenTask(id)`).
2. The ViewModel emits `Navigate(TaskDetailKey(id.value))` as an effect (testable).
3. The Route calls `navigator.navigate(key)`.

Features never import another feature. Results between screens (e.g. "task created") are handled
through data: the destination writes to the database and the origin's `Flow` updates.

## Deep links (phase 4)

Notification taps carry a URI handled by `MainActivity`, which builds the back stack:

| URI | Back stack |
|---|---|
| `prumio://today` | `[Today]` |
| `prumio://task/<id>` | `[Today, TaskDetailKey(id)]` |
| `prumio://appointment/<id>` | `[Today, AppointmentDetailKey(id)]` |
| `prumio://matter/<id>` | `[Today, MatterDetailKey(id)]` |
| `prumio://capture` (shortcuts, widget, tile) | `[Today, CaptureKey]` |

Unknown or deleted ids open the detail screen, which shows "This item no longer exists" and closes.

## ViewModel scoping

Each entry gets its own `ViewModelStore` (`rememberViewModelStoreNavEntryDecorator`), so a
ViewModel lives exactly as long as its screen is on the back stack.

Recipe: [ai/recipes/add-navigation-key.md](../ai/recipes/add-navigation-key.md).

About is a section inside Settings, without an AboutKey. Inbox conversion creates the target
first and navigates to its editor by id; cancelling the editor preserves the converted item.

After completing/dismissing the last action of an open matter from another screen, the domain
result opens its detail with `promptNextAction = true`. The existing optional question offers
Add next action, Mark as waiting and Resolve; adding an action or Undo closes the question.

## Implemented notification links (phase 4)

MainActivity handles ACTION_VIEW on first launch and onNewIntent. ShellViewModel resolves
`prumio://task/<id>`, `appointment/<id>`, `matter/<id>`, `today`, `capture`, and `snooze/<alarmKey>`
through ResolveReminderLinkUseCase. PrumioNavigator replaces the stack with Today and the target.
Missing items open Today with the shared not-found message. Consumed intent data is cleared.
`ReminderReliabilityKey` is a full Settings screen; `SnoozeKey` is a Settings-owned dialog overlay.
Both are registered for saved back stacks. Snooze choices come from the domain, including
Tomorrow-only matter follow-ups.
