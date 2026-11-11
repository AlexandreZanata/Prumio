# Recipe: add a kind of notification

Applies from phase 4 on. Read [reminders-and-notifications.md](../../domain/reminders-and-notifications.md) first.

Notifications are never scheduled by features: they are planned from data (BR-REM-001).

## Steps

1. **Rule**: add or update rules in the BR-REM area ([add-business-rule.md](add-business-rule.md)),
   including priority (time-critical vs deferrable) and channel.
2. **Planner input**: add the data the planner needs to `PlanningInput` and load it in
   `ReconcileRemindersUseCase` (and in the observation that triggers reconciliation).
3. **Planner**: add the anchor expansion and a new `AlarmKind`; keys follow
   `"<kind>:<itemId>:<anchorEpochMinute>:<offset>"`.
4. **Content**: extend `NotificationContentFactory` (title, body, actions, deep link) with strings in
   both languages in `:core:notifications` resources.
5. **Channel**: reuse an existing channel (BR-REM-016). A new channel needs owner approval.
6. **Actions** (if any): a use case per action; the receiver only calls it.
7. **Tests**: planner table tests (window, DST, quiet hours, snooze), content factory tests,
   instrumented delivery test, and a device QA checklist entry.

## Validation

```bash
./gradlew :core:domain:testAndroidHostTest :core:notifications:testAndroidHostTest
```
Then the reminders section of [device-qa-checklist.md](../../plan/qa/device-qa-checklist.md).
