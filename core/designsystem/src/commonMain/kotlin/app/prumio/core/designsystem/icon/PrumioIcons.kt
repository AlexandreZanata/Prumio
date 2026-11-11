package app.prumio.core.designsystem.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import app.prumio.core.designsystem.resources.Res
import app.prumio.core.designsystem.resources.ic_add
import app.prumio.core.designsystem.resources.ic_arrow_back
import app.prumio.core.designsystem.resources.ic_calendar_month
import app.prumio.core.designsystem.resources.ic_calendar_month_filled
import app.prumio.core.designsystem.resources.ic_check
import app.prumio.core.designsystem.resources.ic_check_circle
import app.prumio.core.designsystem.resources.ic_chevron_right
import app.prumio.core.designsystem.resources.ic_close
import app.prumio.core.designsystem.resources.ic_delete
import app.prumio.core.designsystem.resources.ic_edit
import app.prumio.core.designsystem.resources.ic_event
import app.prumio.core.designsystem.resources.ic_expand_less
import app.prumio.core.designsystem.resources.ic_expand_more
import app.prumio.core.designsystem.resources.ic_history
import app.prumio.core.designsystem.resources.ic_hourglass_empty
import app.prumio.core.designsystem.resources.ic_inbox
import app.prumio.core.designsystem.resources.ic_inbox_filled
import app.prumio.core.designsystem.resources.ic_info
import app.prumio.core.designsystem.resources.ic_label
import app.prumio.core.designsystem.resources.ic_location_on
import app.prumio.core.designsystem.resources.ic_mic
import app.prumio.core.designsystem.resources.ic_more_vert
import app.prumio.core.designsystem.resources.ic_notes
import app.prumio.core.designsystem.resources.ic_notifications
import app.prumio.core.designsystem.resources.ic_pending_actions
import app.prumio.core.designsystem.resources.ic_pending_actions_filled
import app.prumio.core.designsystem.resources.ic_radio_button_unchecked
import app.prumio.core.designsystem.resources.ic_repeat
import app.prumio.core.designsystem.resources.ic_schedule
import app.prumio.core.designsystem.resources.ic_search
import app.prumio.core.designsystem.resources.ic_settings
import app.prumio.core.designsystem.resources.ic_share
import app.prumio.core.designsystem.resources.ic_task_alt
import app.prumio.core.designsystem.resources.ic_today
import app.prumio.core.designsystem.resources.ic_today_filled
import app.prumio.core.designsystem.resources.ic_undo
import org.jetbrains.compose.resources.vectorResource

/**
 * The only icon registry of the app: Material Symbols Outlined (Apache-2.0) stored as vector
 * resources in `composeResources/drawable/ic_*.xml`. Screens reference `PrumioIcons.X`, never a
 * resource directly, so the set can be restyled in one place. New icon: docs/ai/recipes/add-design-system-component.md.
 */
object PrumioIcons {
    val Add: ImageVector @Composable get() = vectorResource(Res.drawable.ic_add)
    val Back: ImageVector @Composable get() = vectorResource(Res.drawable.ic_arrow_back)
    val Check: ImageVector @Composable get() = vectorResource(Res.drawable.ic_check)
    val CheckCircle: ImageVector @Composable get() = vectorResource(Res.drawable.ic_check_circle)
    val ChevronRight: ImageVector @Composable get() = vectorResource(Res.drawable.ic_chevron_right)
    val Close: ImageVector @Composable get() = vectorResource(Res.drawable.ic_close)
    val Delete: ImageVector @Composable get() = vectorResource(Res.drawable.ic_delete)
    val Edit: ImageVector @Composable get() = vectorResource(Res.drawable.ic_edit)
    val Event: ImageVector @Composable get() = vectorResource(Res.drawable.ic_event)
    val ExpandLess: ImageVector @Composable get() = vectorResource(Res.drawable.ic_expand_less)
    val ExpandMore: ImageVector @Composable get() = vectorResource(Res.drawable.ic_expand_more)
    val History: ImageVector @Composable get() = vectorResource(Res.drawable.ic_history)
    val Info: ImageVector @Composable get() = vectorResource(Res.drawable.ic_info)
    val Label: ImageVector @Composable get() = vectorResource(Res.drawable.ic_label)
    val Location: ImageVector @Composable get() = vectorResource(Res.drawable.ic_location_on)
    val Mic: ImageVector @Composable get() = vectorResource(Res.drawable.ic_mic)
    val More: ImageVector @Composable get() = vectorResource(Res.drawable.ic_more_vert)
    val Notes: ImageVector @Composable get() = vectorResource(Res.drawable.ic_notes)
    val Notifications: ImageVector @Composable get() = vectorResource(Res.drawable.ic_notifications)
    val Repeat: ImageVector @Composable get() = vectorResource(Res.drawable.ic_repeat)
    val Schedule: ImageVector @Composable get() = vectorResource(Res.drawable.ic_schedule)
    val Search: ImageVector @Composable get() = vectorResource(Res.drawable.ic_search)
    val Settings: ImageVector @Composable get() = vectorResource(Res.drawable.ic_settings)
    val Share: ImageVector @Composable get() = vectorResource(Res.drawable.ic_share)
    val TaskDone: ImageVector @Composable get() = vectorResource(Res.drawable.ic_task_alt)
    val TaskOpen: ImageVector @Composable get() = vectorResource(Res.drawable.ic_radio_button_unchecked)
    val Undo: ImageVector @Composable get() = vectorResource(Res.drawable.ic_undo)
    val Waiting: ImageVector @Composable get() = vectorResource(Res.drawable.ic_hourglass_empty)

    // Top-level destinations: outlined when idle, filled when selected.
    val Today: ImageVector @Composable get() = vectorResource(Res.drawable.ic_today)
    val TodaySelected: ImageVector @Composable get() = vectorResource(Res.drawable.ic_today_filled)
    val Agenda: ImageVector @Composable get() = vectorResource(Res.drawable.ic_calendar_month)
    val AgendaSelected: ImageVector @Composable get() = vectorResource(Res.drawable.ic_calendar_month_filled)
    val Matters: ImageVector @Composable get() = vectorResource(Res.drawable.ic_pending_actions)
    val MattersSelected: ImageVector @Composable get() = vectorResource(Res.drawable.ic_pending_actions_filled)
    val Inbox: ImageVector @Composable get() = vectorResource(Res.drawable.ic_inbox)
    val InboxSelected: ImageVector @Composable get() = vectorResource(Res.drawable.ic_inbox_filled)
}
