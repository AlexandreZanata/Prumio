package app.prumio.feature.matters

import app.cash.turbine.test
import app.prumio.core.domain.matter.OpenActionsChoice
import app.prumio.core.model.MatterId
import app.prumio.core.model.MatterStatus
import app.prumio.core.model.TaskId
import app.prumio.core.model.TaskStatus
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.MatterEditorKey
import app.prumio.core.navigation.TaskDetailKey
import app.prumio.core.navigation.TaskEditorKey
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aTask
import app.prumio.core.testing.runViewModelTest
import app.prumio.feature.matters.detail.MatterDetailAction
import app.prumio.feature.matters.detail.MatterDetailEffect
import app.prumio.feature.matters.detail.MatterDetailViewModel
import app.prumio.feature.matters.detail.WaitingDraft
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MatterDetailViewModelTest {
    private val domain = TestUseCases()
    private val id = MatterId("m1")
    private fun create(withTask: Boolean = false, promptNextAction: Boolean = false): MatterDetailViewModel {
        domain.repos.store.putMatter(aMatter("m1"))
        if (withTask) domain.repos.store.putTask(aTask("t1", matterId = "m1", dueDate = LocalDate(2026, 1, 14)))
        return MatterDetailViewModel(
            MatterDetailKey(
                "m1",
                promptNextAction,
            ),
            domain.observeMatterDetail, domain.waitOnMatter, domain.resumeMatter,
            domain.resolveMatter, domain.reopenMatter, domain.setMatterFollowUp, domain.completeTask,
            domain.reopenTask, domain.addMatterNote, domain.editMatterNote, domain.deleteMatterNote,
            domain.restoreMatterNote, domain.deleteMatter, domain.restoreMatter, domain.time,
            domain.checkReminderPermission, domain.recordNotificationPermission,
        )
    }

    @Test
    fun `BR-MATTER-006 navigation opens the prompt and an added action dismisses it`() = runViewModelTest {
        val vm = create(promptNextAction = true)
        assertTrue(vm.state.value.promptNextAction)
        domain.repos.store.putTask(aTask("new", matterId = "m1"))
        assertFalse(vm.state.value.promptNextAction)
    }

    @Test
    fun `BR-MATTER-004 waiting supports optional fields then resume clears waiting-for`() = runViewModelTest {
        val vm = create()
        vm.onAction(MatterDetailAction.ConfirmWaiting)
        vm.onAction(MatterDetailAction.StartWaiting)
        vm.onAction(MatterDetailAction.ChangeWaiting(WaitingDraft("The office", LocalDate(2026, 1, 20))))
        vm.onAction(MatterDetailAction.ConfirmWaiting)
        assertEquals(MatterStatus.Waiting, vm.state.value.detail?.matter?.status)
        assertEquals("The office", vm.state.value.detail?.matter?.waitingFor)
        vm.onAction(MatterDetailAction.Resume)
        assertEquals(MatterStatus.Open, vm.state.value.detail?.matter?.status)
        assertNull(vm.state.value.detail?.matter?.waitingFor)
    }

    @Test
    fun `BR-MATTER-007 resolves directly without actions and reopens`() = runViewModelTest {
        val vm = create()
        vm.onAction(MatterDetailAction.Resolve)
        assertEquals(MatterStatus.Resolved, domain.repos.matters.get(id)?.status)
        vm.onAction(MatterDetailAction.Reopen)
        assertEquals(MatterStatus.Open, domain.repos.matters.get(id)?.status)
    }

    @Test
    fun `BR-MATTER-007 resolving asks about open actions and can keep them standalone`() = runViewModelTest {
        val vm = create(withTask = true)
        assertTrue(TaskId("t1") in vm.state.value.overdueTaskIds)
        vm.onAction(MatterDetailAction.ToggleOtherActions)
        assertTrue(vm.state.value.showOtherActions)
        vm.onAction(MatterDetailAction.Resolve)
        assertTrue(vm.state.value.isConfirmingResolve)
        vm.onAction(MatterDetailAction.ConfirmResolve(OpenActionsChoice.Unlink))
        assertNull(domain.repos.tasks.get(TaskId("t1"))?.matterId)
        assertEquals(TaskStatus.Open, domain.repos.tasks.get(TaskId("t1"))?.status)
    }

    @Test
    fun `BR-MATTER-006 completing last action prompts for the next step`() = runViewModelTest {
        val vm = create(withTask = true)
        vm.onAction(MatterDetailAction.SetTaskDone(TaskId("t1"), true))
        assertTrue(vm.state.value.promptNextAction)
        vm.onAction(MatterDetailAction.Dismiss)
        assertFalse(vm.state.value.promptNextAction)
        vm.onAction(MatterDetailAction.SetTaskDone(TaskId("t1"), false))
        assertEquals(TaskStatus.Open, domain.repos.tasks.get(TaskId("t1"))?.status)
    }

    @Test
    fun `BR-MATTER-009 follow-up supports date time and clear`() = runViewModelTest {
        val vm = create()
        vm.onAction(MatterDetailAction.SetFollowUp(LocalDate(2026, 1, 20), LocalTime(14, 30)))
        assertEquals(LocalTime(14, 30), vm.state.value.detail?.matter?.followUpTime)
        vm.onAction(MatterDetailAction.SetFollowUp(null))
        assertNull(vm.state.value.detail?.matter?.followUpDate)
    }

    @Test
    fun `BR-MATTER-008 add backdated note edit it delete and Undo`() = runViewModelTest {
        val vm = create()
        vm.onAction(MatterDetailAction.SaveNote)
        vm.onAction(MatterDetailAction.DeleteNote)
        vm.onAction(MatterDetailAction.StartNote)
        vm.onAction(MatterDetailAction.ChangeNote("Called", LocalDate(2026, 1, 10)))
        vm.onAction(MatterDetailAction.SaveNote)
        assertNull(vm.state.value.note)
        val note = assertNotNull(vm.state.value.detail).history.first { it.isEditable }
        vm.onAction(MatterDetailAction.EditNote(note))
        vm.onAction(MatterDetailAction.ChangeNote("Called again", note.occurredOn))
        vm.onAction(MatterDetailAction.SaveNote)
        val edited = assertNotNull(domain.repos.matters.getEntry(note.id))
        assertEquals("Called again", edited.text)
        vm.onAction(MatterDetailAction.EditNote(edited))
        vm.effects.test {
            vm.onAction(MatterDetailAction.DeleteNote)
            val message = assertIs<MatterDetailEffect.ShowMessage>(awaitItem()).message
            assertNull(domain.repos.matters.getEntry(note.id))
            message.onAction?.invoke()
            assertEquals(edited, domain.repos.matters.getEntry(note.id))
        }
    }

    @Test
    fun `BR-MATTER-008 future notes show field errors`() = runViewModelTest {
        val vm = create()
        vm.onAction(MatterDetailAction.StartNote)
        vm.onAction(MatterDetailAction.ChangeNote("Future", LocalDate(2026, 1, 16)))
        vm.onAction(MatterDetailAction.SaveNote)
        assertNotNull(vm.state.value.note?.error)
        vm.onAction(MatterDetailAction.Dismiss)
        assertNull(vm.state.value.note)
    }

    @Test
    fun `BR-GEN-005 deleting a matter closes and Undo restores history and links`() = runViewModelTest {
        val vm = create(withTask = true)
        vm.effects.test {
            vm.onAction(MatterDetailAction.Delete)
            val message = assertIs<MatterDetailEffect.ShowMessage>(awaitItem()).message
            assertEquals(MatterDetailEffect.Close, awaitItem())
            assertNull(domain.repos.matters.get(id))
            message.onAction?.invoke()
            assertNotNull(domain.repos.matters.get(id))
            assertEquals(id, domain.repos.tasks.get(TaskId("t1"))?.matterId)
        }
    }

    @Test
    fun `detail navigation stays in shared keys`() = runViewModelTest {
        val vm = create()
        vm.effects.test {
            vm.onAction(MatterDetailAction.Edit)
            assertEquals(MatterDetailEffect.Navigate(MatterEditorKey("m1")), awaitItem())
            vm.onAction(MatterDetailAction.AddNextAction)
            assertEquals(MatterDetailEffect.Navigate(TaskEditorKey(matterId = "m1")), awaitItem())
            vm.onAction(MatterDetailAction.OpenTask(TaskId("t1")))
            assertEquals(MatterDetailEffect.Navigate(TaskDetailKey("t1")), awaitItem())
            vm.onAction(MatterDetailAction.Close)
            assertEquals(MatterDetailEffect.Close, awaitItem())
        }
    }

    @Test
    fun `domain errors become messages`() = runViewModelTest {
        val vm = create()
        vm.effects.test {
            vm.onAction(MatterDetailAction.SetTaskDone(TaskId("gone"), true))
            assertIs<MatterDetailEffect.ShowMessage>(awaitItem())
        }
    }
}
