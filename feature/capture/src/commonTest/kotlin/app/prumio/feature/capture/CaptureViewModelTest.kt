package app.prumio.feature.capture

import app.cash.turbine.test
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.runViewModelTest
import app.prumio.core.ui.resources.validation_too_long
import app.prumio.feature.capture.resources.Res
import app.prumio.feature.capture.resources.capture_saved
import kotlinx.coroutines.flow.first
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import app.prumio.core.ui.resources.Res as CoreUiRes

class CaptureViewModelTest {
    private val domain = TestUseCases()

    private fun createViewModel() = CaptureViewModel(domain.captureInboxItem)

    @Test
    fun `BR-INBOX-001 saving stores the text, confirms and closes`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onAction(CaptureAction.ChangeText("  Call the plumber  "))
            viewModel.onAction(CaptureAction.Save)

            assertEquals(Res.string.capture_saved, assertIs<CaptureEffect.ShowMessage>(awaitItem()).message.text)
            assertEquals(CaptureEffect.Close, awaitItem())
        }
        assertEquals(listOf("Call the plumber"), domain.repos.inbox.observeAll().first().map { it.text })
    }

    @Test
    fun `BR-INBOX-001 blank text cannot be saved`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.onAction(CaptureAction.ChangeText("   "))
        viewModel.onAction(CaptureAction.Save)

        assertFalse(viewModel.state.value.canSave)
        assertTrue(domain.repos.inbox.observeAll().first().isEmpty())
    }

    @Test
    fun `BR-GEN-002 too long text shows a field error until edited`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.onAction(CaptureAction.ChangeText("x".repeat(2_001)))
        viewModel.onAction(CaptureAction.Save)
        assertEquals(CoreUiRes.string.validation_too_long, viewModel.state.value.error)
        assertFalse(viewModel.state.value.isSaving)

        viewModel.onAction(CaptureAction.ChangeText("short"))
        assertEquals(null, viewModel.state.value.error)
    }

    @Test
    fun `closing emits a close effect`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onAction(CaptureAction.Close)
            assertEquals(CaptureEffect.Close, awaitItem())
        }
    }
}
