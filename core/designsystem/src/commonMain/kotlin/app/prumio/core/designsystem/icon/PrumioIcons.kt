package app.prumio.core.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * The only icon registry of the app. Screens reference `PrumioIcons.X`, never `Icons.*`, so the
 * icon set can be replaced in one place (planned: Material Symbols, see docs/plan/phase-2-design-system.md).
 */
object PrumioIcons {
    val Add: ImageVector get() = Icons.Filled.Add
    val Back: ImageVector get() = Icons.AutoMirrored.Outlined.ArrowBack
    val Check: ImageVector get() = Icons.Outlined.Check
    val Close: ImageVector get() = Icons.Outlined.Close
    val Info: ImageVector get() = Icons.Outlined.Info
    val Notifications: ImageVector get() = Icons.Outlined.Notifications
    val Search: ImageVector get() = Icons.Outlined.Search
    val Settings: ImageVector get() = Icons.Outlined.Settings

    // Top-level destinations: outlined when idle, filled when selected.
    val Today: ImageVector get() = Icons.Outlined.Home
    val TodaySelected: ImageVector get() = Icons.Filled.Home
    val Agenda: ImageVector get() = Icons.Outlined.DateRange
    val AgendaSelected: ImageVector get() = Icons.Filled.DateRange
    val Matters: ImageVector get() = Icons.AutoMirrored.Outlined.List
    val MattersSelected: ImageVector get() = Icons.AutoMirrored.Outlined.List
    val Inbox: ImageVector get() = Icons.Outlined.MailOutline
    val InboxSelected: ImageVector get() = Icons.Filled.Email
}
