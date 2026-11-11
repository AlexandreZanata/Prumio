package app.prumio.core.ui.label

import androidx.compose.runtime.Composable
import app.prumio.core.model.DayPeriod
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.greeting_afternoon
import app.prumio.core.ui.resources.greeting_evening
import app.prumio.core.ui.resources.greeting_morning
import org.jetbrains.compose.resources.stringResource

/** Localized greeting for a [DayPeriod] (BR-TODAY-001). Shared by Today, widgets and the morning summary. */
@Composable
fun DayPeriod.greeting(): String = when (this) {
    DayPeriod.Morning -> stringResource(Res.string.greeting_morning)
    DayPeriod.Afternoon -> stringResource(Res.string.greeting_afternoon)
    DayPeriod.Evening -> stringResource(Res.string.greeting_evening)
}
