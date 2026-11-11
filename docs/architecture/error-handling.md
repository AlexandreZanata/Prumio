# Error handling

## Two kinds of failure

| Kind | Examples | Mechanism |
|---|---|---|
| **Expected** — a valid situation the user can cause or meet | blank title, item deleted meanwhile, invalid backup file, disk full | Return `Outcome.Failure(DomainError)` |
| **Programming error** — a broken invariant | impossible state, missing DI binding, corrupted enum in DB | `require`/`check`/`error` → crash in debug, caught by tests |

Never use exceptions for control flow; never swallow exceptions silently.

## Types

```kotlin
// :core:common
sealed interface Outcome<out T, out E> { Success(value) ; Failure(error) }

// :core:domain/error
sealed interface DomainError {
    data class Validation(val ruleId: String, val field: String, val reason: String) : DomainError
    data class NotFound(val entity: String, val id: String) : DomainError
    data class InvalidTransition(val ruleId: String, val from: String, val to: String) : DomainError
    data class Storage(val cause: Throwable) : DomainError
}
```

- Use cases that write or can fail return `Outcome<T, DomainError>`.
- Observations (`Flow`) do not carry errors for expected cases: an absent item emits `null`.
- `ruleId` ties every validation failure to docs/domain/business-rules.md.

## From error to user

ViewModels translate a `DomainError` into UI:

| Error | UI |
|---|---|
| `Validation` on a form field | Field error text under the field (state), save stays enabled |
| `NotFound` | Close the screen and show "This item no longer exists" (effect) |
| `InvalidTransition` | Should not happen from the UI (actions are hidden); show a generic message and log in debug |
| `Storage` | Snackbar "Couldn't save. Your data is safe; please try again." (effect) |

Messages are string resources (`:core:ui` shared strings for generic ones). The mapping lives in
one place per type: `DomainError.toUiMessage()` in `:core:ui` (task P2.08).

## Crashes and logs

- No remote crash reporting (BR-PRIV-002). Debug builds log to Logcat; release builds do not log
  user content.
- A future "Export diagnostic info" (phase 7) may produce a local text file the user can share
  manually; it never includes item contents.
