package app.prumio.feature.inbox

import app.cash.turbine.test
import app.prumio.core.domain.inbox.ConversionType
import app.prumio.core.model.InboxItemId
import app.prumio.core.navigation.AppointmentEditorKey
import app.prumio.core.navigation.MatterEditorKey
import app.prumio.core.navigation.TaskEditorKey
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.anInboxItem
import app.prumio.core.testing.runViewModelTest
import app.prumio.core.ui.resources.validation_blank
import app.prumio.feature.inbox.resources.Res
import app.prumio.feature.inbox.resources.inbox_discarded
import kotlinx.coroutines.flow.first
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.hours
import app.prumio.core.ui.resources.Res as CoreUiRes

class InboxViewModelTest {
    private val domain = TestUseCases()
    private val older = anInboxItem("old", "Book the car service", createdAt = domain.time.now() - 2.hours)
    private val newer = anInboxItem("new", "Ask Ana about the invoice", createdAt = domain.time.now() - 1.hours)

    private fun createViewModel(): InboxViewModel {
        domain.repos.store.putInboxItem(older)
        domain.repos.store.putInboxItem(newer)
        return InboxViewModel(
            domain.observeInbox,
            domain.editInboxItem,
            domain.discardInboxItem,
            domain.restoreInboxItem,
            domain.convertInboxItem,
            domain.time,
        )
    }

    private suspend fun inboxTexts() = domain.repos.inbox.observeAll().first().map { it.text }

    @Test
    fun `BR-INBOX-003 items are listed newest first`() = runViewModelTest {
        val viewModel = createViewModel()

        assertEquals(listOf(newer, older), viewModel.state.value.items)
        assertEquals(domain.time.now(), viewModel.state.value.now)
    }

    @Test
    fun `BR-INBOX-006 BR-GEN-005 discarding offers Undo that restores the item`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onAction(InboxAction.Select(newer.id))
            viewModel.onAction(InboxAction.Discard)

            val message = assertIs<InboxEffect.ShowMessage>(awaitItem()).message
            assertEquals(Res.string.inbox_discarded, message.text)
            assertEquals(listOf(older.text), inboxTexts())
            message.onAction?.invoke()
            assertEquals(listOf(newer.text, older.text), inboxTexts())
        }
        assertNull(viewModel.state.value.selected)
    }

    @Test
    fun `BR-INBOX-006 editing changes the text and validates it`() = runViewModelTest {
        val viewModel = createViewModel()
        viewModel.onAction(InboxAction.Select(older.id))
        viewModel.onAction(InboxAction.StartEdit)
        assertEquals(older.text, viewModel.state.value.editText)

        viewModel.onAction(InboxAction.ChangeEditText(" "))
        viewModel.onAction(InboxAction.SaveEdit)
        assertEquals(CoreUiRes.string.validation_blank, viewModel.state.value.editError)

        viewModel.onAction(InboxAction.ChangeEditText("Book the car service on Monday"))
        viewModel.onAction(InboxAction.SaveEdit)
        assertNull(viewModel.state.value.selected)
        assertEquals(listOf(newer.text, "Book the car service on Monday"), inboxTexts())
    }

    @Test
    fun `BR-INBOX-004 organizing opens the editor of the new item`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onAction(InboxAction.Select(newer.id))
            viewModel.onAction(InboxAction.Organize(ConversionType.Task))
            assertIs<TaskEditorKey>(assertIs<InboxEffect.Navigate>(awaitItem()).key)

            viewModel.onAction(InboxAction.Select(older.id))
            viewModel.onAction(InboxAction.Organize(ConversionType.Appointment))
            assertIs<AppointmentEditorKey>(assertIs<InboxEffect.Navigate>(awaitItem()).key)
        }
        assertEquals(emptyList(), inboxTexts())
    }

    @Test
    fun `organizing a matter opens the matter editor and missing items report an error`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onAction(InboxAction.Select(newer.id))
            viewModel.onAction(InboxAction.Organize(ConversionType.Matter))
            assertIs<MatterEditorKey>(assertIs<InboxEffect.Navigate>(awaitItem()).key)

            viewModel.onAction(InboxAction.Select(older.id))
            domain.repos.inbox.delete(InboxItemId("old"))
            viewModel.onAction(InboxAction.Discard)
            assertIs<InboxEffect.ShowMessage>(awaitItem())
        }
        viewModel.onAction(InboxAction.DismissSheet)
        assertNull(viewModel.state.value.selected)
    }
}
