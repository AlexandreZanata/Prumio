package app.prumio.feature.matters

import app.cash.turbine.test
import app.prumio.core.model.MatterId
import app.prumio.core.navigation.MatterEditorKey
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.runViewModelTest
import app.prumio.feature.matters.editor.MatterEditorAction
import app.prumio.feature.matters.editor.MatterEditorEffect
import app.prumio.feature.matters.editor.MatterEditorViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class MatterEditorViewModelTest {
    private val domain = TestUseCases()
    private fun create(key: MatterEditorKey = MatterEditorKey()) =
        MatterEditorViewModel(key, domain.observeMatterDetail, domain.createMatter, domain.updateMatter)

    @Test
    fun `BR-MATTER-001 title alone creates matter and opens its detail`() = runViewModelTest {
        val vm = create()
        assertFalse(vm.state.value.canSave)
        vm.onAction(MatterEditorAction.Save)
        vm.effects.test {
            vm.onAction(MatterEditorAction.ChangeTitle("Car documents"))
            vm.onAction(MatterEditorAction.Save)
            assertIs<MatterEditorEffect.ShowMessage>(awaitItem())
            val id = assertIs<MatterEditorEffect.OpenCreated>(awaitItem()).id
            assertEquals("Car documents", domain.repos.matters.get(id)?.title)
        }
    }

    @Test
    fun `editor loads existing description and updates it`() = runViewModelTest {
        domain.repos.store.putMatter(aMatter("m1").copy(description = "Original"))
        val vm = create(MatterEditorKey("m1"))
        assertFalse(vm.state.value.isNew)
        assertEquals("Original", vm.state.value.description)
        vm.onAction(MatterEditorAction.ChangeDescription("Copies at home"))
        vm.effects.test {
            vm.onAction(MatterEditorAction.Save)
            assertIs<MatterEditorEffect.ShowMessage>(awaitItem())
            assertEquals(MatterEditorEffect.Close, awaitItem())
        }
        assertEquals("Copies at home", domain.repos.matters.get(MatterId("m1"))?.description)
    }

    @Test
    fun `BR-MATTER-001 invalid title displays a field error and can be corrected`() = runViewModelTest {
        val vm = create()
        vm.onAction(MatterEditorAction.ChangeTitle("x".repeat(501)))
        vm.onAction(MatterEditorAction.Save)
        assertNotNull(vm.state.value.titleError)
        assertFalse(vm.state.value.isSaving)
        vm.onAction(MatterEditorAction.ChangeTitle("Short"))
        vm.effects.test {
            vm.onAction(MatterEditorAction.Close)
            assertEquals(MatterEditorEffect.Close, awaitItem())
        }
    }

    @Test
    fun `editing a deleted matter shows a message`() = runViewModelTest {
        val vm = create(MatterEditorKey("missing"))
        vm.onAction(MatterEditorAction.ChangeTitle("Gone"))
        vm.effects.test {
            vm.onAction(MatterEditorAction.Save)
            assertIs<MatterEditorEffect.ShowMessage>(awaitItem())
        }
    }
}
