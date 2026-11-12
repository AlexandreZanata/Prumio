# `:core:navigation`

> The navigation contract shared by all features: every destination key and the `Navigator` interface.

| | |
|---|---|
| **Layer** | Contract |
| **Plugin** | `prumio.kmp.library` + kotlinx-serialization |
| **Package** | `app.prumio.core.navigation` |
| **May depend on** | nothing (Navigation 3 runtime and kotlinx-serialization only) |
| **Used by** | `:feature:*`, `:app:shell` |

## Public API

| Symbol | File | Contract |
|---|---|---|
| `TopLevelNavKey`, `OverlayNavKey` | `PrumioNavKeys.kt` | Marker types: bottom bar tabs / dialogs |
| `TodayKey`, `AgendaKey`, `MattersKey`, `InboxKey` | `PrumioNavKeys.kt` | Top-level destinations |
| `CaptureKey` | `PrumioNavKeys.kt` | Quick capture dialog |
| `SearchKey`, `SettingsKey` | `PrumioNavKeys.kt` | Full screens |
| Task, appointment and matter editor/detail keys; `AllTasksKey(filter?)` | `PrumioNavKeys.kt` | Phase 3 screens with primitive id/date/filter arguments |
| `navKeySerializersModule` | `NavKeySerialization.kt` | Registers every key for back stack persistence |
| `Navigator` | `Navigator.kt` | `navigate(key)`, `goBack()`; implemented by `:app:shell` |

Planned keys and back stack rules: [docs/architecture/navigation.md](../../docs/architecture/navigation.md).

## Rules

- Keys are `@Serializable` and carry only primitives (ids as `String`), never domain objects.
- Every key is registered in `navKeySerializersModule` (architecture test enforces it).
- Exactly one feature provides the entry for each key.

## How to extend

New destination → [docs/ai/recipes/add-navigation-key.md](../../docs/ai/recipes/add-navigation-key.md).
