package app.prumio.core.domain.reminder

/** Explicit-time anchors are time critical; date-only and catch-up are deferrable (BR-REM-004). */
enum class AlarmPriority { TimeCritical, Deferrable }
