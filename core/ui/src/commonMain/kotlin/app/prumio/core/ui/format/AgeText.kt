package app.prumio.core.ui.format

import androidx.compose.runtime.Composable
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.age_days
import app.prumio.core.ui.resources.age_hours
import app.prumio.core.ui.resources.age_just_now
import app.prumio.core.ui.resources.age_minutes
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration
import kotlin.time.Instant

/** How old something is: "Just now", "5 minutes ago", "2 hours ago", "3 days ago". */
@Composable
fun ageText(since: Instant, now: Instant): String {
    val age: Duration = now - since
    return when {
        age.inWholeMinutes < 1 -> stringResource(Res.string.age_just_now)
        age.inWholeHours < 1 -> age.inWholeMinutes.toInt().let { pluralStringResource(Res.plurals.age_minutes, it, it) }
        age.inWholeDays < 1 -> age.inWholeHours.toInt().let { pluralStringResource(Res.plurals.age_hours, it, it) }
        else -> age.inWholeDays.toInt().let { pluralStringResource(Res.plurals.age_days, it, it) }
    }
}
