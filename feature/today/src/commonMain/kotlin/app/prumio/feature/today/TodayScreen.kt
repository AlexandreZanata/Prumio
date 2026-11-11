package app.prumio.feature.today

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioEmptyState
import app.prumio.core.designsystem.component.PrumioIconButton
import app.prumio.core.designsystem.component.PrumioScaffold
import app.prumio.core.designsystem.component.PrumioSectionHeader
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.component.PrumioTopBar
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.model.DayPeriod
import app.prumio.core.ui.format.dayHeaderText
import app.prumio.core.ui.label.greeting
import app.prumio.core.ui.resources.destination_search
import app.prumio.core.ui.resources.destination_settings
import app.prumio.core.ui.resources.destination_today
import app.prumio.feature.today.resources.Res
import app.prumio.feature.today.resources.today_empty_description
import app.prumio.feature.today.resources.today_empty_title
import app.prumio.feature.today.resources.today_section_summary
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Stateless Today screen: renders [state] and reports user intents through [onAction]. */
@Composable
internal fun TodayScreen(state: TodayUiState, onAction: (TodayAction) -> Unit, modifier: Modifier = Modifier) {
    PrumioScaffold(
        modifier = modifier,
        topBar = {
            PrumioTopBar(
                title = stringResource(CoreUiRes.string.destination_today),
                showBrandMark = true,
                actions = {
                    PrumioIconButton(
                        icon = PrumioIcons.Search,
                        contentDescription = stringResource(CoreUiRes.string.destination_search),
                        onClick = { onAction(TodayAction.OpenSearch) },
                    )
                    PrumioIconButton(
                        icon = PrumioIcons.Settings,
                        contentDescription = stringResource(CoreUiRes.string.destination_settings),
                        onClick = { onAction(TodayAction.OpenSettings) },
                    )
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = PrumioTheme.spacing.screenHorizontal),
        ) {
            state.date?.let { date ->
                PrumioText(
                    text = date.dayHeaderText(),
                    style = PrumioTextStyle.Overline,
                    color = PrumioTheme.colors.onSurfaceVariant,
                )
            }
            PrumioText(text = state.dayPeriod.greeting(), style = PrumioTextStyle.Headline)
            Spacer(modifier = Modifier.height(PrumioTheme.spacing.xl))
            PrumioSectionHeader(title = stringResource(Res.string.today_section_summary))
            PrumioEmptyState(
                title = stringResource(Res.string.today_empty_title),
                description = stringResource(Res.string.today_empty_description),
                icon = PrumioIcons.Check,
            )
        }
    }
}

@Preview
@Composable
private fun TodayScreenPreview() {
    PrumioTheme {
        TodayScreen(
            state = TodayUiState(dayPeriod = DayPeriod.Morning, date = LocalDate(2026, 11, 11)),
            onAction = {},
        )
    }
}
