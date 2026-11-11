# Device QA checklist

Manual tests on real devices and emulators. Run the sections required by the current gate
(validation-gates.md) and record results in the phase file's gate record (date, device, API level,
pass/fail, issue links). Items marked *(phase N)* apply once that phase is implemented.

Useful commands:

```bash
./gradlew :app:android:installDebug
adb shell am start -n app.prumio.debug/app.prumio.app.android.MainActivity
adb shell dumpsys deviceidle force-idle                 # enter Doze
adb shell dumpsys deviceidle unforce                    # leave Doze
adb shell cmd alarm list | grep prumio                  # scheduled alarms (phase 4)
adb reboot
adb shell settings put global auto_time_zone 0 && adb shell setprop persist.sys.timezone "Europe/Lisbon"
adb shell pm revoke app.prumio.debug android.permission.POST_NOTIFICATIONS
"$ANDROID_HOME"/build-tools/*/aapt2 dump permissions app/android/build/outputs/apk/debug/*.apk
```

## A — Install, launch, shell

- [ ] Fresh install launches to Today without crash (API 26 and latest).
- [ ] Bottom bar: Today · Agenda · Matters · Inbox; selected state visible; back from any tab → Today; back on Today → home screen.
- [ ] "Add anything" visible on all tabs, hidden on Search/Settings.
- [ ] Rotation and process death (Developer options → "Don't keep activities") restore the current screen.
- [ ] `aapt2 dump permissions` shows no `android.permission.INTERNET`.

## B — Capture and inbox *(phase 3)*

- [ ] From each tab: Add → type → Save in two taps; keyboard opens immediately.
- [ ] Blank text cannot be saved; 2 000-character limit enforced.
- [ ] Inbox newest first; badge count correct; 99+ above 99.
- [ ] Discard → Undo restores; edit text persists.
- [ ] Organize into task / appointment / matter: title = first line, notes = rest; item leaves inbox.

## C — Tasks *(phase 3)*

- [ ] Create with title only; with date; with date + time; time without date impossible.
- [ ] Complete, reopen, dismiss, delete + undo; postpone tomorrow / next week / date.
- [ ] Overdue display after midnight and after the due time passes.
- [ ] All tasks segments show the right items.

## D — Agenda and appointments *(phase 3)*

- [ ] Timed, point-in-time and all-day appointments; multi-day all-day spans all days.
- [ ] End before start rejected.
- [ ] Travel test: create 16:00 São Paulo, switch device to Lisbon → shows 19:00/20:00 (DST-dependent); floating tasks keep their times.

## E — Matters *(phase 3)*

- [ ] Create → Waiting (with/without "waiting for" and follow-up) → Resume → Resolve → Reopen; history entries for each.
- [ ] Next action: add, complete → prompt for next action; resolve with open actions → dismiss/keep choice.
- [ ] Notes: add today, backdate, future date rejected; edit/delete notes only.
- [ ] Groups in the Matters list match BR-MATTER-013.

## F — Today and search *(phase 3)*

- [ ] Counts, ordering, next appointment and attention matters match fixtures.
- [ ] Leave the app open across midnight → Today updates.
- [ ] Search "documentacao" finds "Documentação"; multi-word queries; < 2 characters shows hint.

## G — Reminders *(phase 4)*

- [ ] First reminder creation asks notification permission after an explanation; deny → banner in Settings; deny twice → no more prompts.
- [ ] Task at time fires on the minute (exact alarms allowed).
- [ ] Revoke exact alarms → reminders still fire (possibly late); reliability screen explains; re-grant works.
- [ ] Appointment advance notice (30 min default) fires; all-day reminder at default time.
- [ ] Complete from notification closes the task; snooze 10 min / 1 h / tomorrow re-fires correctly.
- [ ] Edit due time after scheduling → old alarm gone, new one fires; delete item → no notification.
- [ ] Reboot → pending reminders still fire; reminder missed while off (< 12 h) is delivered once after boot.
- [ ] Doze (force-idle) → reminder delivered (allow-while-idle).
- [ ] Time zone change → floating reminders follow wall clock; appointment reminders keep the instant.
- [ ] App update (install new build over old) → reminders preserved.
- [ ] Tap notification → correct detail screen with Today underneath; deleted item → message.
- [ ] Morning summary, quiet hours, follow-ups *(phase 5)*.

## H — Settings and backup *(phases 3, 5)*

- [ ] Theme switch applies immediately; per-app language opens system screen.
- [ ] Export backup → file readable; import on a clean install restores everything *(phase 5)*.
- [ ] Import an invalid file → clear error, no data change *(phase 5)*.
- [ ] Automatic backup writes daily, keeps 7 *(phase 5)*.

## I — Localization and themes

- [ ] Device language pt-BR: every screen in Portuguese, natural wording, no truncation.
- [ ] Dark theme on every screen: readable, no hard-coded colors.

## J — Accessibility *(phase 6 full pass; spot-checks before)*

- [ ] TalkBack: every control announces action + item; logical focus order; headings navigable.
- [ ] Font size 200 % and display size max: no clipped or overlapping text.
- [ ] Touch targets ≥ 48 dp; status never conveyed by color only.

## K — Widgets, share, shortcuts, voice *(phase 6)*

- [ ] Today widget updates after completing a task in the app and vice versa.
- [ ] Share text from a browser/notes app lands in Quick capture.
- [ ] Launcher shortcuts and Quick Settings tile open capture.
- [ ] Voice capture works with on-device recognition; privacy gate still green.
