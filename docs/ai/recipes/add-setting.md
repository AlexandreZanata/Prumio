# Recipe: add a user setting

Settings are a typed registry over the `setting` key-value table: adding one needs **no migration**.
(The registry and `SettingsRepository` are created in task P1.09.)

## Steps

1. Add the row to the Settings table in [model.md](../../domain/model.md#settings) (key, type,
   default, phase) and mark it device-specific if it must not be backed up.
2. Declare the key in `core/domain/src/commonMain/kotlin/app/prumio/core/domain/settings/SettingKey.kt`:
   ```kotlin
   val QuietHoursEnabled = SettingKey.Boolean(name = "quiet_hours_enabled", default = false)
   ```
   Names are `snake_case`, never renamed once released (stored data and backups use them).
3. Expose it in the typed `Settings` model and its mapping (one line each).
4. If it affects reminders, include it in `ReminderSettings` so changes trigger reconciliation (BR-SET-002).
5. UI: add the control to the Settings screen with strings in both languages.
6. Tests: default value when absent, invalid stored value falls back to default (BR-SET-001),
   round-trip set/get.

## Validation

```bash
./gradlew :core:domain:testAndroidHostTest :core:data:testAndroidHostTest :feature:settings:testAndroidHostTest
```
