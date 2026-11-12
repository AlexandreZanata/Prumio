package app.prumio.feature.matters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioEmptyState
import app.prumio.core.designsystem.component.PrumioIconButton
import app.prumio.core.designsystem.component.PrumioScaffold
import app.prumio.core.designsystem.component.PrumioSectionHeader
import app.prumio.core.designsystem.component.PrumioTopBar
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.domain.matter.MatterSummary
import app.prumio.core.domain.matter.MattersOverview
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterId
import app.prumio.core.model.MatterStatus
import app.prumio.core.ui.component.MatterCard
import app.prumio.core.ui.resources.destination_matters
import app.prumio.feature.matters.resources.Res
import app.prumio.feature.matters.resources.matters_empty_description
import app.prumio.feature.matters.resources.matters_empty_title
import app.prumio.feature.matters.resources.matters_needs_attention
import app.prumio.feature.matters.resources.matters_new
import app.prumio.feature.matters.resources.matters_open
import app.prumio.feature.matters.resources.matters_resolved
import app.prumio.feature.matters.resources.matters_waiting
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Stateless Matters tab. Resolved matters are collapsed by default. */
@Composable
internal fun MattersScreen(state: MattersUiState, onAction: (MattersAction) -> Unit, modifier: Modifier = Modifier) {
    PrumioScaffold(
        modifier = modifier,
        topBar = {
            PrumioTopBar(
                title = stringResource(CoreUiRes.string.destination_matters),
                actions = {
                    PrumioIconButton(
                        icon = PrumioIcons.Add,
                        contentDescription = stringResource(Res.string.matters_new),
                        onClick = { onAction(MattersAction.New) },
                    )
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            val overview = state.overview
            when {
                overview == null -> Unit

                state.isEmpty -> PrumioEmptyState(
                    title = stringResource(Res.string.matters_empty_title),
                    description = stringResource(Res.string.matters_empty_description),
                    icon = PrumioIcons.Matters,
                    modifier = Modifier.align(Alignment.Center),
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = PrumioTheme.spacing.screenHorizontal,
                        vertical = PrumioTheme.spacing.sm,
                    ),
                    verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm),
                ) {
                    group("attention", Res.string.matters_needs_attention, overview.needsAttention, state, onAction)
                    group("waiting", Res.string.matters_waiting, overview.waiting, state, onAction)
                    group("open", Res.string.matters_open, overview.open, state, onAction)
                    resolved(overview.resolved, state, onAction)
                }
            }
        }
    }
}

private fun LazyListScope.group(
    key: String,
    title: StringResource,
    matters: List<MatterSummary>,
    state: MattersUiState,
    onAction: (MattersAction) -> Unit,
) {
    if (matters.isEmpty()) return
    item(key = "header-$key") { PrumioSectionHeader(title = stringResource(title)) }
    cards(matters, state, onAction)
}

private fun LazyListScope.resolved(
    matters: List<MatterSummary>,
    state: MattersUiState,
    onAction: (MattersAction) -> Unit,
) {
    if (matters.isEmpty()) return
    item(key = "header-resolved") {
        PrumioSectionHeader(title = stringResource(Res.string.matters_resolved, matters.size)) {
            PrumioIconButton(
                icon = if (state.showResolved) PrumioIcons.ExpandLess else PrumioIcons.ExpandMore,
                contentDescription = stringResource(Res.string.matters_resolved, matters.size),
                onClick = { onAction(MattersAction.ToggleResolved) },
            )
        }
    }
    if (state.showResolved) cards(matters, state, onAction)
}

private fun LazyListScope.cards(
    matters: List<MatterSummary>,
    state: MattersUiState,
    onAction: (MattersAction) -> Unit,
) {
    items(matters, key = { it.matter.id.value }) { summary ->
        MatterCard(
            matter = summary.matter,
            nextAction = summary.nextAction,
            today = state.today,
            onClick = { onAction(MattersAction.Open(summary.matter.id)) },
        )
    }
}

@Preview
@Composable
private fun MattersScreenPreview() {
    val now = Instant.parse("2026-11-11T12:00:00Z")
    fun matter(id: String, title: String, status: MatterStatus = MatterStatus.Open) = MatterSummary(
        Matter(MatterId(id), title, status = status, lastActivityAt = now, createdAt = now, updatedAt = now),
        nextAction = null,
        needsAttention = status == MatterStatus.Open,
    )
    PrumioTheme {
        MattersScreen(
            state = MattersUiState(
                overview = MattersOverview(
                    needsAttention = listOf(matter("1", "Apartment rental")),
                    waiting = listOf(matter("2", "Insurance refund", MatterStatus.Waiting)),
                    open = emptyList(),
                    resolved = listOf(matter("3", "Passport renewal", MatterStatus.Resolved)),
                ),
                today = LocalDate(2026, 11, 11),
            ),
            onAction = {},
        )
    }
}
