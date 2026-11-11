# Recipe: add a navigation destination

## Steps

1. Add the key to `core/navigation/src/commonMain/kotlin/app/prumio/core/navigation/PrumioNavKeys.kt`:
   ```kotlin
   @Serializable
   data class TaskDetailKey(val taskId: String) : NavKey      // arguments: primitives only
   ```
   Top-level tab? implement `TopLevelNavKey` (and update the bottom bar, needs owner approval).
   Dialog? implement `OverlayNavKey` and register the entry with `metadata = DialogSceneStrategy.dialog()`.
2. Register it in `NavKeySerialization.kt`: `subclass(TaskDetailKey::class)` 🔒.
3. Provide the entry in exactly one feature's `xxxEntries` function:
   `entry<TaskDetailKey> { key -> TaskDetailRoute(taskId = key.taskId, navigator = navigator) }`.
4. Update the key table in [navigation.md](../../architecture/navigation.md) and, if it has a deep
   link, the deep link table.
5. Navigate to it from ViewModels through effects: `sendEffect(XxxEffect.Navigate(TaskDetailKey(id.value)))`.

## Validation

```bash
./gradlew :core:navigation:testAndroidHostTest :app:shell:testAndroidHostTest :tooling:architecture-tests:test
```
