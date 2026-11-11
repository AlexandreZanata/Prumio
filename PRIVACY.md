# Privacy

**Short version: Prumio has no internet access, no account and no tracking. Your data stays on
your device unless you move it.**

## What Prumio does not do

- It does not request the Android `INTERNET` permission — it technically cannot send anything
  anywhere. The build fails if any code or library tries to add that permission.
- No analytics, telemetry, advertising, remote crash reporting or tracking SDKs.
- No account, sign-in or cloud service.
- No AI or remote processing of your data.

## Where your data lives

- Tasks, appointments, matters, inbox items and settings are stored in a SQLite database in
  Prumio's private app storage on your device.
- Notifications are scheduled locally by Android.

## When data leaves the device — only if you choose

| Way | Who triggers it | Where it goes |
|---|---|---|
| Backup export (and optional automatic backup) | You | A file in the location you pick |
| Encrypted backup | You | Same, protected by your password |
| Calendar export (.ics) | You | A file in the location you pick |
| Android system backup / device transfer | You, in Android settings | Your Google account backup (end-to-end encrypted on Android 9+ with a screen lock) or your new device. Turn it off in system settings if you prefer |
| Opening "Source code" or help links | You | Your browser opens the page; Prumio itself sends nothing |

## Permissions Prumio uses

| Permission | Why | When |
|---|---|---|
| Notifications | To show the reminders you set | Asked when you first create a reminder (phase 4) |
| Exact alarms | To deliver reminders on the exact minute | You may grant it in settings; without it reminders may be a few minutes late |
| Run at startup | To restore reminders after the phone restarts | Automatic |
| Microphone (voice capture, optional) | To dictate a thought into the inbox using the system recognizer | Only when you tap the microphone (phase 6) |

## Contact

Questions or concerns: open an issue at https://github.com/AlexandreZanata/Prumio/issues
(do not include personal data). Security issues: see [SECURITY.md](SECURITY.md).
