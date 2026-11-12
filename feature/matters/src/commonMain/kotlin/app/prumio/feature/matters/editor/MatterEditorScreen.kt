package app.prumio.feature.matters.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioButton
import app.prumio.core.designsystem.component.PrumioScaffold
import app.prumio.core.designsystem.component.PrumioTextField
import app.prumio.core.designsystem.component.PrumioTopBar
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.domain.text.TextLimits
import app.prumio.core.ui.resources.action_back
import app.prumio.core.ui.resources.action_save
import app.prumio.feature.matters.resources.Res
import app.prumio.feature.matters.resources.matter_description
import app.prumio.feature.matters.resources.matter_edit
import app.prumio.feature.matters.resources.matter_new
import app.prumio.feature.matters.resources.matter_title
import app.prumio.feature.matters.resources.matter_title_placeholder
import org.jetbrains.compose.resources.stringResource
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Stateless matter editor. */
@Composable
internal fun MatterEditorScreen(
    state: MatterEditorUiState,
    onAction: (MatterEditorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focus = remember { FocusRequester() }
    PrumioScaffold(
        modifier = modifier,
        topBar = {
            PrumioTopBar(
                title = stringResource(if (state.isNew) Res.string.matter_new else Res.string.matter_edit),
                onNavigateBack = { onAction(MatterEditorAction.Close) },
                navigateBackContentDescription = stringResource(CoreUiRes.string.action_back),
                actions = {
                    PrumioButton(
                        text = stringResource(CoreUiRes.string.action_save),
                        onClick = { onAction(MatterEditorAction.Save) },
                        enabled = state.canSave,
                    )
                },
            )
        },
    ) { padding ->
        if (!state.isLoading) {
            LaunchedEffect(Unit) { focus.requestFocus() }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(PrumioTheme.spacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.md),
            ) {
                PrumioTextField(
                    value = state.title,
                    onValueChange = { onAction(MatterEditorAction.ChangeTitle(it)) },
                    label = stringResource(Res.string.matter_title),
                    placeholder = stringResource(Res.string.matter_title_placeholder),
                    errorText = state.titleError?.let { stringResource(it) },
                    maxLength = TextLimits.TITLE,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    focusRequester = focus,
                    modifier = Modifier.fillMaxWidth(),
                )
                PrumioTextField(
                    value = state.description,
                    onValueChange = { onAction(MatterEditorAction.ChangeDescription(it)) },
                    label = stringResource(Res.string.matter_description),
                    maxLength = TextLimits.NOTES,
                    minLines = 4,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview
@Composable
private fun MatterEditorScreenPreview() {
    PrumioTheme {
        MatterEditorScreen(
            state = MatterEditorUiState(
                isLoading = false,
                title = "Sort out the car documents",
            ),
            onAction = {
            },
        )
    }
}
