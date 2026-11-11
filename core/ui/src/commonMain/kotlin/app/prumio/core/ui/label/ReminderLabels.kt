package app.prumio.core.ui.label

import androidx.compose.runtime.Composable
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.reminder_at_time
import app.prumio.core.ui.resources.reminder_days_before
import app.prumio.core.ui.resources.reminder_hours_before
import app.prumio.core.ui.resources.reminder_minutes_before
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private const val MINUTES_PER_HOUR = 60
private const val MINUTES_PER_DAY = 1_440

/** "At time", "10 minutes before", "1 hour before", "2 days before" for a reminder offset (BR-REM-004). */
@Composable
fun reminderOffsetText(offsetMinutes: Int): String = when {
    offsetMinutes <= 0 -> stringResource(Res.string.reminder_at_time)

    offsetMinutes % MINUTES_PER_DAY == 0 -> (offsetMinutes / MINUTES_PER_DAY).let {
        pluralStringResource(Res.plurals.reminder_days_before, it, it)
    }

    offsetMinutes % MINUTES_PER_HOUR == 0 -> (offsetMinutes / MINUTES_PER_HOUR).let {
        pluralStringResource(Res.plurals.reminder_hours_before, it, it)
    }

    else -> pluralStringResource(Res.plurals.reminder_minutes_before, offsetMinutes, offsetMinutes)
}
