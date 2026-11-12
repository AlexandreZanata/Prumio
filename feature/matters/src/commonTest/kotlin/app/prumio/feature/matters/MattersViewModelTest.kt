package app.prumio.feature.matters

import app.cash.turbine.test
import app.prumio.core.model.MatterId
import app.prumio.core.model.MatterStatus
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.MatterEditorKey
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.runViewModelTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MattersViewModelTest {
    private val domain = TestUseCases()

    @Test
    fun `BR-MATTER-013 groups update live and resolved starts collapsed`() = runViewModelTest {
        val vm = MattersViewModel(domain.observeMatters, domain.time)
        assertTrue(vm.state.value.isEmpty)
        domain.repos.store.putMatter(aMatter("m1"))
        domain.repos.store.putMatter(aMatter("m2", status = MatterStatus.Resolved))
        assertFalse(vm.state.value.isEmpty)
        assertEquals(listOf(MatterId("m1")), vm.state.value.overview?.needsAttention?.map { it.matter.id })
        assertFalse(vm.state.value.showResolved)
        vm.onAction(MattersAction.ToggleResolved)
        assertTrue(vm.state.value.showResolved)
    }

    @Test
    fun `list opens existing matter or creates a new one`() = runViewModelTest {
        val vm = MattersViewModel(domain.observeMatters, domain.time)
        vm.effects.test {
            vm.onAction(MattersAction.New)
            assertEquals(MattersEffect.Navigate(MatterEditorKey()), awaitItem())
            vm.onAction(MattersAction.Open(MatterId("m1")))
            assertEquals(MattersEffect.Navigate(MatterDetailKey("m1")), awaitItem())
        }
    }
}
