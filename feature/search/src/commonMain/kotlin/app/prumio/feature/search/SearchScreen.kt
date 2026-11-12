package app.prumio.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioEmptyState
import app.prumio.core.designsystem.component.PrumioScaffold
import app.prumio.core.designsystem.component.PrumioSectionHeader
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.component.PrumioTextField
import app.prumio.core.designsystem.component.PrumioTopBar
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.domain.search.highlightRanges
import app.prumio.core.ui.resources.action_back
import app.prumio.core.ui.resources.destination_inbox
import app.prumio.core.ui.resources.destination_matters
import app.prumio.core.ui.resources.destination_search
import app.prumio.feature.search.resources.Res
import app.prumio.feature.search.resources.search_appointments
import app.prumio.feature.search.resources.search_hint
import app.prumio.feature.search.resources.search_nothing_found
import app.prumio.feature.search.resources.search_tasks
import org.jetbrains.compose.resources.stringResource
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Focused field and grouped results, highlighting original text with the domain's matching ranges. */
@Composable
internal fun SearchScreen(state: SearchUiState, onAction: (SearchAction) -> Unit, modifier: Modifier = Modifier) {
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { focus.requestFocus() }
    PrumioScaffold(
        modifier = modifier,
        topBar = {
            PrumioTopBar(
                title = stringResource(CoreUiRes.string.destination_search),
                onNavigateBack = { onAction(SearchAction.Close) },
                navigateBackContentDescription = stringResource(CoreUiRes.string.action_back),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(
                padding,
            ).padding(horizontal = PrumioTheme.spacing.screenHorizontal),
        ) {
            PrumioTextField(
                value = state.query,
                onValueChange = { onAction(SearchAction.ChangeQuery(it)) },
                label = stringResource(CoreUiRes.string.destination_search),
                focusRequester = focus,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            val results = state.results
            if (state.query.isBlank() || results?.queryTooShort == true) {
                PrumioEmptyState(title = stringResource(Res.string.search_hint), icon = PrumioIcons.Search)
            } else if (results?.isEmpty == true) {
                PrumioEmptyState(
                    title = stringResource(Res.string.search_nothing_found, state.query),
                    icon = PrumioIcons.Search,
                )
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm)) {
                if (results != null) {
                    if (results.tasks.isNotEmpty()) {
                        item { PrumioSectionHeader(title = stringResource(Res.string.search_tasks)) }
                        items(results.tasks, key = { "task-${it.id.value}" }) { task ->
                            SearchResultRow(
                                title = task.title,
                                supporting = task.notes,
                                query = state.query,
                                onClick = { onAction(SearchAction.OpenTask(task.id)) },
                            )
                        }
                    }
                    if (results.appointments.isNotEmpty()) {
                        item { PrumioSectionHeader(title = stringResource(Res.string.search_appointments)) }
                        items(results.appointments, key = { "appointment-${it.id.value}" }) { appointment ->
                            SearchResultRow(
                                title = appointment.title,
                                supporting = listOfNotNull(appointment.location, appointment.notes).joinToString(" · "),
                                query = state.query,
                                onClick = { onAction(SearchAction.OpenAppointment(appointment.id)) },
                            )
                        }
                    }
                    if (results.matters.isNotEmpty()) {
                        item { PrumioSectionHeader(title = stringResource(CoreUiRes.string.destination_matters)) }
                        items(results.matters, key = { "matter-${it.id.value}" }) { matter ->
                            SearchResultRow(
                                title = matter.title,
                                supporting = listOfNotNull(matter.description, matter.waitingFor).joinToString(" · "),
                                query = state.query,
                                onClick = { onAction(SearchAction.OpenMatter(matter.id)) },
                            )
                        }
                    }
                    if (results.inboxItems.isNotEmpty()) {
                        item { PrumioSectionHeader(title = stringResource(CoreUiRes.string.destination_inbox)) }
                        items(results.inboxItems, key = { "inbox-${it.id.value}" }) { item ->
                            SearchResultRow(
                                title = item.text,
                                supporting = null,
                                query = state.query,
                                onClick = { onAction(SearchAction.OpenInbox) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(title: String, supporting: String?, query: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = PrumioTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.xs),
    ) {
        HighlightedText(text = title, query = query)
        if (!supporting.isNullOrBlank()) {
            HighlightedText(
                text = supporting,
                query = query,
                style = PrumioTextStyle.Caption,
            )
        }
    }
}

@Composable
private fun HighlightedText(text: String, query: String, style: PrumioTextStyle = PrumioTextStyle.Body) {
    val color = PrumioTheme.colors.primary
    val highlighted = remember(text, query, color) {
        buildAnnotatedString {
            append(text)
            highlightRanges(text, query).forEach { range ->
                addStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold), range.first, range.last + 1)
            }
        }
    }
    PrumioText(text = highlighted, style = style, maxLines = 4)
}

@Preview
@Composable
private fun SearchScreenPreview() {
    PrumioTheme { SearchScreen(state = SearchUiState(), onAction = {}) }
}
