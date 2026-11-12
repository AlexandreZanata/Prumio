package app.prumio.feature.tasks.list

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioChip
import app.prumio.core.designsystem.component.PrumioEmptyState
import app.prumio.core.designsystem.component.PrumioIconButton
import app.prumio.core.designsystem.component.PrumioScaffold
import app.prumio.core.designsystem.component.PrumioTopBar
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.domain.task.TaskListFilter
import app.prumio.core.model.Task
import app.prumio.core.model.TaskId
import app.prumio.core.ui.component.TaskRow
import app.prumio.core.ui.resources.action_add
import app.prumio.core.ui.resources.action_back
import app.prumio.feature.tasks.resources.Res
import app.prumio.feature.tasks.resources.tasks_all
import app.prumio.feature.tasks.resources.tasks_empty_anytime
import app.prumio.feature.tasks.resources.tasks_empty_completed
import app.prumio.feature.tasks.resources.tasks_empty_dismissed
import app.prumio.feature.tasks.resources.tasks_empty_overdue
import app.prumio.feature.tasks.resources.tasks_empty_upcoming
import app.prumio.feature.tasks.resources.tasks_filter_anytime
import app.prumio.feature.tasks.resources.tasks_filter_completed
import app.prumio.feature.tasks.resources.tasks_filter_dismissed
import app.prumio.feature.tasks.resources.tasks_filter_overdue
import app.prumio.feature.tasks.resources.tasks_filter_upcoming
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Stateless "All tasks" screen. */
@Composable
internal fun AllTasksScreen(state: AllTasksUiState, onAction: (AllTasksAction) -> Unit, modifier: Modifier = Modifier) {
    PrumioScaffold(
        modifier = modifier,
        topBar = {
            PrumioTopBar(
                title = stringResource(Res.string.tasks_all),
                onNavigateBack = { onAction(AllTasksAction.Close) },
                navigateBackContentDescription = stringResource(CoreUiRes.string.action_back),
                actions = {
                    PrumioIconButton(
                        icon = PrumioIcons.Add,
                        contentDescription = stringResource(CoreUiRes.string.action_add),
                        onClick = { onAction(AllTasksAction.Add) },
                    )
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = PrumioTheme.spacing.screenHorizontal),
                horizontalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm),
            ) {
                TaskListFilter.entries.forEach { filter ->
                    PrumioChip(
                        label = stringResource(filter.label),
                        selected = filter == state.filter,
                        onClick = { onAction(AllTasksAction.SelectFilter(filter)) },
                    )
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> Unit

                    state.tasks.isEmpty() -> PrumioEmptyState(
                        title = stringResource(state.filter.empty),
                        icon = PrumioIcons.TaskDone,
                        modifier = Modifier.align(Alignment.Center),
                    )

                    else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.tasks, key = { it.task.id.value }) { item ->
                            TaskRow(
                                task = item.task,
                                today = state.today,
                                isOverdue = item.isOverdue,
                                onToggle = { onAction(AllTasksAction.SetDone(item.task.id, it)) },
                                onClick = { onAction(AllTasksAction.Open(item.task.id)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

private val TaskListFilter.label: StringResource
    get() = when (this) {
        TaskListFilter.Anytime -> Res.string.tasks_filter_anytime
        TaskListFilter.Upcoming -> Res.string.tasks_filter_upcoming
        TaskListFilter.Overdue -> Res.string.tasks_filter_overdue
        TaskListFilter.Completed -> Res.string.tasks_filter_completed
        TaskListFilter.Dismissed -> Res.string.tasks_filter_dismissed
    }

private val TaskListFilter.empty: StringResource
    get() = when (this) {
        TaskListFilter.Anytime -> Res.string.tasks_empty_anytime
        TaskListFilter.Upcoming -> Res.string.tasks_empty_upcoming
        TaskListFilter.Overdue -> Res.string.tasks_empty_overdue
        TaskListFilter.Completed -> Res.string.tasks_empty_completed
        TaskListFilter.Dismissed -> Res.string.tasks_empty_dismissed
    }

@Preview
@Composable
private fun AllTasksScreenPreview() {
    val now = Instant.parse("2026-11-11T12:00:00Z")
    PrumioTheme {
        AllTasksScreen(
            state = AllTasksUiState(
                tasks = listOf(
                    TaskListItem(
                        Task(
                            TaskId("1"),
                            "Renew passport",
                            dueDate = LocalDate(2026, 11, 20),
                            createdAt = now,
                            updatedAt = now,
                        ),
                        isOverdue = false,
                    ),
                ),
                today = LocalDate(2026, 11, 11),
                isLoading = false,
            ),
            onAction = {},
        )
    }
}
